package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.ProxyException;
import au.com.agic.apptesting.utils.*;
import io.vavr.control.Try;
import net.lightbody.bmp.BrowserMobProxy;
import org.apache.commons.lang3.StringUtils;
import org.zaproxy.clientapi.core.ClientApi;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
				proxies.add(zapProxy.get());

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
	public void stopProxies(@NotNull final List<ProxyDetails<?>> proxies, final String reportOutput) {
		checkNotNull(proxies);
		checkArgument(StringUtils.isNotBlank(reportOutput));

		proxies.stream()
			.filter(proxyDetails -> BrowsermobProxyUtilsImpl.PROXY_NAME.equals(proxyDetails.getProxyName()))
			.forEach(x -> x.getInterface()
				.map(BrowserMobProxy.class::cast)
				.ifPresent(proxy -> {
					/*
						Save the HAR file before the proxy is shut down. Doing this work
						here means that the HAR file is always available, even if the
						test failed and a step like "I dump the HAR file" was not executed.
					 */
					if (proxy.getHar() != null) {
						Try.run(() -> {
							final String filename = Constants.HAR_FILE_NAME_PREFIX
								+ new SimpleDateFormat(Constants.FILE_DATE_FORMAT).format(new Date())
								+ "."
								+ Constants.HAR_FILE_NAME_EXTENSION;

							final File file = new File(
								reportOutput
									+ "/"
									+ filename);
							proxy.getHar().writeTo(file);
						});
					}
					proxy.abort();
				}));
	}
}
