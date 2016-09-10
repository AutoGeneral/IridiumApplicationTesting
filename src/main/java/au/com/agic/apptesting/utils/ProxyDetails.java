package au.com.agic.apptesting.utils;

import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * This represents the details of a proxy that our test steps can use
 */
public interface ProxyDetails<T> {

	/**
	 *
	 * @return The port that the proxy is run on
	 */
	int getPort();

	/**
	 *
	 * @return The interface (like a client API) that we can use to access the proxy, or an emoty result if
	 * there is no such interface.
	 */
	Optional<T> getInterface();

	/**
	 *
	 * @return A general map of name value pairs that define the properties of the proxy
	 */
	@NotNull
	Map<String, Object> getProperties();

	/**
	 *
	 * @param properties A general map of name value pairs that define the properties of the proxy
	 */
	void setProperties(@NotNull final Map<String, Object> properties);

	/**
	 *
	 * @return true if this is the main proxy i.e. the one that the browser should use
	 */
	boolean isMainProxy();

	/**
	 *
	 * @param mainProxy true if this is the main proxy i.e. the one that the browser should use
	 */
	void setMainProxy(boolean mainProxy);

	/**
	 *
	 * @return The name of the proxy
	 */
	String getProxyName();
}
