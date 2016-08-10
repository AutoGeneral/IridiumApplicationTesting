package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.ProxyException;
import au.com.agic.apptesting.utils.*;
import javaslang.control.Try;
import net.lightbody.bmp.BrowserMobProxy;
import org.zaproxy.clientapi.core.ClientApi;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An implementation of the proxy manager service
 */
public class ProxyManagerImpl implements ProxyManager {
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();
	private static final LocalProxyUtils<ClientApi> ZAP_PROXY = new ZapProxyUtilsImpl();
	private static final LocalProxyUtils<BrowserMobProxy> BROWSERMOB_PROXY = new BrowsermobProxyUtilsImpl();

	@Override
	public List<ProxyDetails<?>> configureProxies(@NotNull final List<File> tempFiles) {
		checkNotNull(tempFiles);

		try {
			final Optional<ProxySettings> proxySettings = ProxySettings.fromSystemProps();

			/*
				ZAP always uses the upstream proxy if ZAP is enabled.
			 */
			final Optional<ProxyDetails<ClientApi>> zapProxy =
				ZAP_PROXY.initProxy(tempFiles, proxySettings);

			/*
				Browsermob will upstream to zap if configured to do so
			 */
			final Optional<ProxySettings> browserMobUpstream = zapProxy.isPresent()
				? Optional.of(new ProxySettings("localhost", zapProxy.get().getPort()))
				: proxySettings;

			final Optional<ProxyDetails<BrowserMobProxy>> browermobProxy =
				BROWSERMOB_PROXY.initProxy(tempFiles, browserMobUpstream);

			/*
				We always enable the BrowserMob proxy
			 */
			final List<ProxyDetails<?>> proxies = new ArrayList<>();
			proxies.add(browermobProxy.get());

			/*
				Forward browsermob to ZAP
			 */
			if (zapProxy.isPresent()) {
				proxies.add(zapProxy.get());
			}

			return proxies;
		} catch (final Exception ex) {
			throw new ProxyException(
				"An exception was thrown while attempting to configure the proxies",
				ex);
		}
	}

	@Override
	public void stopProxies(final List<ProxyDetails<?>> proxies) {

		if (proxies != null) {
			proxies.stream()
				.forEach(x -> BrowserMobProxy.class.cast(x.getInterface().get()).stop());

			proxies.stream()
				.filter(ZapProxyUtilsImpl.PROXY_NAME::equals)
				.map(x ->
					Try.of(() ->
						ClientApi.class.cast(x.getInterface().get())
							.core.shutdown(Constants.ZAP_API_KEY)));
		}
	}
}
