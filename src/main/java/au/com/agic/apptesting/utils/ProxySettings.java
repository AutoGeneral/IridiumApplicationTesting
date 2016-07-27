package au.com.agic.apptesting.utils;

import static com.google.common.base.Preconditions.checkArgument;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.impl.SystemPropertyUtilsImpl;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * Represents the settings of a proxy
 */
public class ProxySettings {
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	private final String host;
	private final int port;
	private final String username;
	private final String password;
	private final String realm;

	/**
	 *
	 * @return The proxy hostname
	 */
	public String getHost() {
		return host;
	}

	/**
	 *
	 * @return The proxy port
	 */
	public int getPort() {
		return port;
	}

	/**
	 *
	 * @return The proxy username, or null if it was not set
	 */
	public String getUsername() {
		return username;
	}

	/**
	 *
	 * @return The proxy password, or null if it was not set
	 */
	public String getPassword() {
		return password;
	}

	/**
	 *
	 * @return The realm
	 */
	public String getRealm() {
		return realm;
	}

	/**
	 *
	 * @param host The proxy hostname
	 * @param port The proxy port
	 */
	public ProxySettings(@NotNull final String host, final int port) {
		checkArgument(StringUtils.isNotBlank(host));

		this.host = host;
		this.port = port;
		this.username = null;
		this.password = null;
		this.realm = null;
	}

	/**
	 *
	 * @param host The proxy hostname
	 * @param port The proxy port
	 * @param realm The realm
	 */
	public ProxySettings(
			@NotNull final String host,
			final int port,
			final String realm) {

		checkArgument(StringUtils.isNotBlank(host));

		this.host = host;
		this.port = port;
		this.username = null;
		this.password = null;
		this.realm = realm;
	}

	/**
	 *
	 * @param host The proxy hostname
	 * @param port The proxy port
	 * @param   username The proxy username
	 * @param   password The proxy password
	 */
	public ProxySettings(
			@NotNull final String host,
			final int port,
			final String username,
			final String password) {

		checkArgument(StringUtils.isNotBlank(host));

		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.realm = null;
	}
	/**
	 *
	 * @param host The proxy hostname
	 * @param port The proxy port
	 * @param   username The proxy username
	 * @param   password The proxy password
	 * @param realm The realm
	 */
	public ProxySettings(
			@NotNull final String host,
			final int port,
			final String realm,
			final String username,
			final String password) {

		checkArgument(StringUtils.isNotBlank(host));

		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.realm = realm;
	}

	public static Optional<ProxySettings> fromSystemProps() {
		final String proxyHostname =
			SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(Constants.EXTERNAL_PROXY_HOST);
		final String proxyPort =
			SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(Constants.EXTERNAL_PROXY_PORT);

		final String proxyUsername =
			SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(Constants.EXTERNAL_PROXY_USERNAME);
		final String proxyPassword =
			SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(Constants.EXTERNAL_PROXY_PASSWORD);

		final String proxyRealm =
			SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(Constants.EXTERNAL_PROXY_REALM);

		if (StringUtils.isNotBlank(proxyHostname) && StringUtils.isNotBlank(proxyPort)) {
			return Optional.of(new ProxySettings(
				proxyHostname,
				Integer.parseInt(proxyPort),
				proxyRealm,
				proxyUsername,
				proxyPassword)
			);
		}

		return Optional.empty();
	}
}
