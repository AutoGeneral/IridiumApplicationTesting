package au.com.agic.apptesting.utils;

/**
 * A service for automatically extracting and configuring web drivers
 */
public interface WebDriverHandler {

	/**
	 * Extracts the integrated web driver and configures the system properties
	 */
	void configureWebDriver();
}
