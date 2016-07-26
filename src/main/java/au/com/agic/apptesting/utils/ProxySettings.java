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
	 * @param host The proxy hostname
	 * @param port The proxy port
	 */
	public ProxySettings(@NotNull final String host, final int port) {
		checkArgument(StringUtils.isNotBlank(host));

		this.host = host;
		this.port = port;
		this.username = null;
		this.password = null;
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
			@NotNull final String username,
			@NotNull final String password) {
		checkArgument(StringUtils.isNotBlank(host));
		checkArgument(StringUtils.isNotBlank(username));
		checkArgument(StringUtils.isNotBlank(password));

		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	public static Optional<ProxySettings> fromSystemProps() {
		final Optional<String> proxyHostname = Optional.ofNullable(
			SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(Constants.EXTERNAL_PROXY_HOST));
		final Optional<String> proxyPort = Optional.ofNullable(
			SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(Constants.EXTERNAL_PROXY_PORT));

		final Optional<String> proxyUsername = Optional.ofNullable(
			SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(Constants.EXTERNAL_PROXY_USERNAME));
		final Optional<String> proxyPassword = Optional.ofNullable(
			SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(Constants.EXTERNAL_PROXY_PASSWORD));

		if (proxyHostname.isPresent() && proxyPort.isPresent()) {
			if (proxyUsername.isPresent() && proxyPassword.isPresent()) {
				return Optional.of(new ProxySettings(
					proxyHostname.get(),
					Integer.parseInt(proxyPort.get()),
					proxyUsername.get(),
					proxyPassword.get()
				));
			}

			return Optional.of(new ProxySettings(
				proxyHostname.get(),
				Integer.parseInt(proxyPort.get())
			));
		}

		return Optional.empty();
	}
}
