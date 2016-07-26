package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.exception.ProxyException;
import au.com.agic.apptesting.utils.FileSystemUtils;
import au.com.agic.apptesting.utils.LocalProxyUtils;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.ServerPortUtils;
import au.com.agic.apptesting.utils.SystemPropertyUtils;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.filters.ResponseFilter;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import io.netty.handler.codec.http.HttpResponse;

/**
 * An implementation of the browsermob proxy. This proxy allows us to block access to urls
 * and can work with an upstream proxy like ZAP.
 */
public class BrowsermobProxyUtilsImpl implements LocalProxyUtils<BrowserMobProxy> {

	public static final String PROXY_NAME = "BROWSERMOB";
	/**
	 * This is the name of the key that will be saved in ProxyDetails properties
	 * that keeps track of any error responses that have been passed through the
	 * proxy.
	 */
	public static final String INVALID_REQUESTS = "Invalid Requests";
	private static final Logger LOGGER = LoggerFactory.getLogger(BrowsermobProxyUtilsImpl.class);
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();
	private static final ServerPortUtils SERVER_PORT_UTILS = new ServerPortUtilsImpl();
	private static final FileSystemUtils FILE_SYSTEM_UTILS = new FileSystemUtilsImpl();

	private static final int WAIT_FOR_START = 30000;

	@Override
	public Optional<ProxyDetails<BrowserMobProxy>> startProxy(@NotNull final List<File> tempFolders) {
		checkNotNull(tempFolders);

		try {
			return Optional.of(startBrowsermobProxy());
		} catch (final Exception ex) {
			throw new ProxyException(ex);
		}
	}

	/**
	 * Starts the Browsermob Proxy
	 *
	 * @return The port that the proxy is listening on
	 */
	private ProxyDetails<BrowserMobProxy> startBrowsermobProxy() throws Exception {
		final BrowserMobProxy proxy = new BrowserMobProxyServer();
		proxy.setTrustAllServers(true);
		proxy.start(0);

		final ProxyDetails<BrowserMobProxy> proxyDetails =
			new ProxyDetailsImpl<>(proxy.getPort(), true, PROXY_NAME, proxy);

		trackErrorResponses(proxy, proxyDetails);

		return proxyDetails;
	}

	@SuppressWarnings("unchecked")
	private void trackErrorResponses(
		final BrowserMobProxy proxy,
		final ProxyDetails<BrowserMobProxy> proxyDetails) {

		proxy.addResponseFilter(new ResponseFilter() {
			@Override
			public void filterResponse(
				final HttpResponse response,
				final HttpMessageContents contents,
				final HttpMessageInfo messageInfo) {

				/*
					Track anything other than a 200 range response
				 */
				if (response.getStatus().code() >= 400 && response.getStatus().code() <= 599) {
					final Map<String, Object> properties = proxyDetails.getProperties();
					if (!properties.containsKey(INVALID_REQUESTS)) {
						properties.put(INVALID_REQUESTS, new ArrayList<HttpMessageInfo>());
					}
					ArrayList.class.cast(properties.get(INVALID_REQUESTS)).add(messageInfo);
				}
			}
		});
	}
}
