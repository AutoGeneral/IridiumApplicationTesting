package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.ProxyException;
import au.com.agic.apptesting.utils.EnableDisableListUtils;
import au.com.agic.apptesting.utils.FileSystemUtils;
import au.com.agic.apptesting.utils.LocalProxyUtils;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.ProxySettings;
import au.com.agic.apptesting.utils.ServerPortUtils;
import au.com.agic.apptesting.utils.SystemPropertyUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.zap.ZAP;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * An implementation of the local proxy service
 */
@Component
public class ZapProxyUtilsImpl implements LocalProxyUtils<ClientApi> {

	public static final String PROXY_NAME = "ZAP";
	private static final Logger LOGGER = LoggerFactory.getLogger(ZapProxyUtilsImpl.class);
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();
	private static final ServerPortUtils SERVER_PORT_UTILS = new ServerPortUtilsImpl();
	private static final FileSystemUtils FILE_SYSTEM_UTILS = new FileSystemUtilsImpl();
	private static final EnableDisableListUtils ENABLE_DISABLE_LIST_UTILS = new EnableDisableListUtilsImpl();

	private static final int WAIT_FOR_START = 30000;

	/**
	 * ZAP is not designed to be started and restarted, so we create one instance
	 * and share it across tests.
	 */
	private static ProxyDetailsImpl<ClientApi> zapSingleton;

	@Override
	public Optional<ProxyDetails<ClientApi>> initProxy(
			@NotNull final List<File> globalTempFiles,
			@NotNull final List<File> tempFolders,
			@NotNull final Optional<ProxySettings> upstreamProxy) {

		checkNotNull(globalTempFiles);
		checkNotNull(tempFolders);
		checkNotNull(upstreamProxy);

		try {
			final String proxyName =
				SYSTEM_PROPERTY_UTILS.getProperty(Constants.START_INTERNAL_PROXY);

			if (StringUtils.isNotBlank(proxyName)) {
				final boolean enabled = ENABLE_DISABLE_LIST_UTILS.enabled(
					proxyName,
					Constants.ZED_ATTACK_PROXY);

				if (enabled) {
					return Optional.of(startZAPProxy(globalTempFiles, upstreamProxy));
				}
			}

			return Optional.empty();
		} catch (final Exception ex) {
			throw new ProxyException(ex);
		}
	}

	/**
	 * Starts the Zed Attack Proxy
	 *
	 * @param tempFolders A list of folders that need to be deleted once the app is finished
	 * @return The port that the proxy is listening on
	 */
	private synchronized ProxyDetails<ClientApi> startZAPProxy(
			@NotNull final List<File> tempFolders,
			@NotNull final Optional<ProxySettings> upstreamProxy) throws Exception {

		checkNotNull(tempFolders);
		checkNotNull(upstreamProxy);

		if (zapSingleton != null) {
			return zapSingleton;
		}

		/*
			There is a small chance that between the call to getFreePort() and the
			actual execution of the ZAP application, some other thread could get
			the same port. We synchronize here to prevent threads in the testing app
			from colliding.

			Of course, there is also the small possibility that another application
			could also take the port between the call to getFreePort() and the
			actual execution of the ZAP application, but there isn't much we can do
			about that.
		 */
		synchronized (ServerPortUtils.class) {
			/*
				Grab an open port
			 */
			final Integer freePort = SERVER_PORT_UTILS.getFreePort();

			/*
				ZAP expects to find files on the filesystem, not in a self contained jar,
				so we copy them to a temp location
			 */
			final Path zapdir = Files.createTempDirectory("zaptmp");

			/*
				It is handy to be able to see this dir
			 */
			LOGGER.debug("Created install dir for ZAP in {}", zapdir);

			/*
				Make a note of the temp folder so it can be cleaned up later
			 */
			tempFolders.add(zapdir.toFile());

			/*
				Copy the ZAP files
			 */
			FILE_SYSTEM_UTILS.copyFromJar(getClass().getResource("/zap").toURI(), zapdir);

			/*
				Build up the command line args
			 */
			final List<String> args = new ArrayList<>(Arrays.asList(
				"-daemon",
				"-port", freePort.toString(),
				"-dir", zapdir.toString(),
				"-installdir", zapdir.toString(),
				"-config", "api.disablekey=true"
			));

			if (upstreamProxy.isPresent()) {
				args.addAll(Arrays.asList(
					"-config", "connection.proxyChain.enabled=true",
					"-config", "connection.proxyChain.hostName=" + upstreamProxy.get().getHost(),
					"-config", "connection.proxyChain.port=" + upstreamProxy.get().getPort()
				));

				if (StringUtils.isNotBlank(upstreamProxy.get().getUsername())) {
					args.addAll(Arrays.asList(
						"-config", "connection.proxyChain.userName="
							+ upstreamProxy.get().getUsername(),
						"-config", "connection.proxyChain.password="
							+ upstreamProxy.get().getPassword()
					));
				}

				if (StringUtils.isNotBlank(upstreamProxy.get().getRealm())) {
					args.addAll(Arrays.asList(
						"-config", "connection.proxyChain.realm="
							+ upstreamProxy.get().getRealm()
					));
				}
			}

			/*
				Run ZAP
			 */
			ZAP.main(args.toArray(new String[args.size()]));

			final ClientApi clientApi = new ClientApi("localhost", freePort);
			clientApi.waitForSuccessfulConnectionToZap(WAIT_FOR_START);

			zapSingleton = new ProxyDetailsImpl<>(
				freePort,
				false,
				PROXY_NAME,
				new ClientApi("localhost", freePort));

			return zapSingleton;
		}
	}
}


