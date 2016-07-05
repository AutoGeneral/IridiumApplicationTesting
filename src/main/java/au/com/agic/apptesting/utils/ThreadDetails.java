package au.com.agic.apptesting.utils;

import au.com.agic.apptesting.profiles.configuration.UrlMapping;

import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * Represents the details required by a feature to run in a particular thread.
 */
public interface ThreadDetails {

	/**
	 * @return The url associated with this instance of the test
	 */
	UrlMapping getUrlDetails();

	/**
	 * @return The web driver associated with this instance of the test
	 */
	WebDriver getWebDriver();

	/**
	 * @return The data set associated with this instance of the test
	 */
	Map<String, String> getDataSet();

	/**
	 * @param dataSet The data set associated with this instance of the test
	 */
	void setDataSet(Map<String, String> dataSet);

	/**
	 * @return true if there was a failed scenario, and false otherwise
	 */
	boolean getFailed();

	/**
	 * @param failed true if there was a failed scenario, and false otherwise
	 */
	void setFailed(final boolean failed);

	/**
	 * @return The directory where reports and other test output is saved
	 */
	String getReportDirectory();

	/**
	 *
	 * @return The optional details of the proxy being used
	 */
	List<ProxyDetails<?>> getProxyInterface();

	/**
	 *
	 * @param name The name of the proxy to find
	 * @return The proxy that matches the name, or an empty result
	 */
	Optional<ProxyDetails<?>> getProxyInterface(@NotNull final String name);

	/**
	 *
	 * @param proxyInterface The optional details of the proxy being used
	 */
	void setProxyInterface(final List<ProxyDetails<?>> proxies);
}
