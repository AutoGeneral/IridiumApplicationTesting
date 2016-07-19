package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.ProxyException;
import au.com.agic.apptesting.utils.LocalProxyUtils;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.ProxyManager;
import au.com.agic.apptesting.utils.SystemPropertyUtils;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.proxy.auth.AuthType;

import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import java.io.File;
import java.net.InetSocketAddress;
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
	public List<ProxyDetails<?>> configureProxies(@NotNull final List<File> tempFiles) {
		checkNotNull(tempFiles);

		try {
			final Optional<String> proxyHostname = Optional.ofNullable(
				SYSTEM_PROPERTY_UTILS.getProperty(Constants.EXTERNAL_PROXY_HOST));
			final Optional<String> proxyPort = Optional.ofNullable(
				SYSTEM_PROPERTY_UTILS.getProperty(Constants.EXTERNAL_PROXY_PORT));

			final Optional<String> proxyUsername = Optional.ofNullable(
				SYSTEM_PROPERTY_UTILS.getProperty(Constants.EXTERNAL_PROXY_USERNAME));
			final Optional<String> proxyPassword = Optional.ofNullable(
				SYSTEM_PROPERTY_UTILS.getProperty(Constants.EXTERNAL_PROXY_PASSWORD));

			final Optional<ProxyDetails<ClientApi>> zapProxy =
				ZAP_PROXY.startProxy(tempFiles);
			final Optional<ProxyDetails<BrowserMobProxy>> browermobProxy =
				BROWSERMOB_PROXY.startProxy(tempFiles);

			final List<ProxyDetails<?>> proxies = new ArrayList<>();
			proxies.add(browermobProxy.get());

			/*
				Forward browsermod to ZAP
			 */
			if (zapProxy.isPresent()) {
				browermobProxy.get().getInterface().get()
					.setChainedProxy(new InetSocketAddress("localhost", zapProxy.get().getPort()));
				proxies.add(zapProxy.get());

				/*
					Then forward ZAP to the external proxy
				 */
				if (proxyHostname.isPresent() && proxyPort.isPresent()) {
					forwardZAPToExternalProxy(
						zapProxy.get(),
						proxyHostname,
						proxyPort,
						proxyUsername,
						proxyPassword);
				}
			} else {
				/*
					Forward browsermob to the external proxy
				 */
				if (proxyHostname.isPresent() && proxyPort.isPresent()) {
					forwardBrowserMobToExternalProxy(
						browermobProxy.get(),
						proxyHostname,
						proxyPort,
						proxyUsername,
						proxyPassword);
				}
			}

			return proxies;
		} catch (final Exception ex) {
			throw new ProxyException(
				"An exception was thrown while attempting to configure the proxies",
				ex);
		}
	}

	private void forwardBrowserMobToExternalProxy(
			@NotNull final ProxyDetails<BrowserMobProxy> browserMobProxy,
			@NotNull final Optional<String> hostname,
			@NotNull final Optional<String> port,
			@NotNull final Optional<String> username,
			@NotNull final Optional<String> password) {

		checkNotNull(browserMobProxy);
		checkNotNull(hostname);
		checkNotNull(port);
		checkNotNull(username);
		checkNotNull(password);

		if (hostname.isPresent() && port.isPresent()) {

			if (browserMobProxy.getInterface().isPresent()) {
				final BrowserMobProxy proxyInterface = browserMobProxy.getInterface().get();

				proxyInterface.setChainedProxy(new InetSocketAddress(
					hostname.get(),
					Integer.parseInt(port.get())));

				if (username.isPresent() && password.isPresent()) {
					proxyInterface.chainedProxyAuthorization(
						username.get(),
						password.get(),
						AuthType.BASIC);
				}
			}
		}
	}

	private void forwardZAPToExternalProxy(
			@NotNull final ProxyDetails<ClientApi> zapProxy,
			@NotNull final Optional<String> hostname,
			@NotNull final Optional<String> port,
			@NotNull final Optional<String> username,
			@NotNull final Optional<String> password) throws ClientApiException {

		checkNotNull(zapProxy);
		checkNotNull(hostname);
		checkNotNull(port);
		checkNotNull(username);
		checkNotNull(password);

		if (zapProxy.getInterface().isPresent()) {
			final ClientApi proxyInterface = zapProxy.getInterface().get();

			proxyInterface.core.setOptionUseProxyChain(
				Constants.ZAP_API_KEY,
				true);

			proxyInterface.core.setOptionProxyChainName(
				Constants.ZAP_API_KEY,
				hostname.get());

			proxyInterface.core.setOptionProxyChainPort(
				Constants.ZAP_API_KEY,
				Integer.parseInt(port.get()));

			if (username.isPresent() && password.isPresent()) {
				proxyInterface.core.setOptionProxyChainUserName(
					Constants.ZAP_API_KEY,
					username.get());

				proxyInterface.core.setOptionProxyChainPassword(
					Constants.ZAP_API_KEY,
					password.get());
			}
		}
	}
}
