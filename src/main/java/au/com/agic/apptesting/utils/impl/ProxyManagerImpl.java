package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.exception.ProxyException;
import au.com.agic.apptesting.utils.LocalProxyUtils;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.ProxyManager;
import au.com.agic.apptesting.utils.ProxySettings;
import au.com.agic.apptesting.utils.SleepUtils;
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
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();
	private static final int PROXY_STOP_SLEEP = 1000;

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
				.filter(BrowsermobProxyUtilsImpl.PROXY_NAME::equals)
				.forEach(x -> BrowserMobProxy.class.cast(x.getInterface().get()).stop());

			/*
				Give the proxies time to actually stop
			 */
			SLEEP_UTILS.sleep(PROXY_STOP_SLEEP);
		}
	}
}
