package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.exception.ProxyException;
import au.com.agic.apptesting.utils.LocalProxyUtils;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.ProxyManager;
import au.com.agic.apptesting.utils.ProxySettings;
import au.com.agic.apptesting.utils.SystemPropertyUtils;

import net.lightbody.bmp.BrowserMobProxy;

import org.zaproxy.clientapi.core.ClientApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * An implementation of the proxy manager service
 */
public class ProxyManagerImpl implements ProxyManager {
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();
	private static final LocalProxyUtils<ClientApi> ZAP_PROXY = new ZapProxyUtilsImpl();
	private static final LocalProxyUtils<BrowserMobProxy> BROWSERMOB_PROXY = new BrowsermobProxyUtilsImpl();

	@Override
	public List<ProxyDetails<?>> configureProxies(
		@NotNull final List<File> globalTempFiles,
		@NotNull final List<File> tempFiles) {

		checkNotNull(globalTempFiles);
		checkNotNull(tempFiles);

		try {
			final Optional<ProxySettings> proxySettings = ProxySettings.fromSystemProps();

			/*
				ZAP always uses the upstream proxy if ZAP is enabled.
			 */
			final Optional<ProxyDetails<ClientApi>> zapProxy =
				ZAP_PROXY.initProxy(globalTempFiles, tempFiles, proxySettings);

			/*
				Browsermob will upstream to zap if configured to do so
			 */
			final Optional<ProxySettings> browserMobUpstream = zapProxy.isPresent()
				? Optional.of(new ProxySettings("localhost", zapProxy.get().getPort()))
				: proxySettings;

			final Optional<ProxyDetails<BrowserMobProxy>> browermobProxy =
				BROWSERMOB_PROXY.initProxy(globalTempFiles, tempFiles, browserMobUpstream);

			/*
				Create the collection of proxies
			 */
			final List<ProxyDetails<?>> proxies = new ArrayList<>();

			if (browermobProxy.isPresent()) {
				proxies.add(browermobProxy.get());

				/*
					Forward browsermob to ZAP
				 */
				if (zapProxy.isPresent()) {
					proxies.add(zapProxy.get());
				}
			} else if (zapProxy.isPresent()) {
				/*
					In the event that zap is enabled and browsermob isn't, ZAP is the main proxy
				 */
				zapProxy.get().setMainProxy(true);
			}

			return proxies;
		} catch (final Exception ex) {
			throw new ProxyException(
				"An exception was thrown while attempting to configure the proxies",
				ex);
		}
	}

	@Override
	public void stopProxies(@NotNull final List<ProxyDetails<?>> proxies) {
		checkNotNull(proxies);

		proxies.stream()
			.filter(BrowsermobProxyUtilsImpl.PROXY_NAME::equals)
			.forEach(x -> x.getInterface()
				.map(BrowserMobProxy.class::cast)
				.ifPresent(BrowserMobProxy::stop));
	}
}
