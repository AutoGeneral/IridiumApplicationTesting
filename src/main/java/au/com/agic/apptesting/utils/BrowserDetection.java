package au.com.agic.apptesting.utils;

import org.openqa.selenium.WebDriver;

import javax.validation.constraints.NotNull;

/**
 * A service for detecting the type of browser being used.
 */
public interface BrowserDetection {

	/**
	 *
	 * @param webDriver The current web driver
	 * @return true if the webdriver is controlling the Edge browser
	 */
	boolean isEdge(@NotNull WebDriver webDriver);
	/**
	 *
	 * @param webDriver The current web driver
	 * @return true if the webdriver is controlling the PhantomJS browser
	 */
	boolean isPhantomJS(@NotNull WebDriver webDriver);

	/**
	 * @param webDriver The current web driver
	 * @return true if the webdriver is controlling the Opera browser
	 */
	boolean isOpera(@NotNull WebDriver webDriver);

	/**
	 *
	 * @param webDriver The current web driver
	 * @return true if the webdriver is controlling the Firefox browser
	 */
	boolean isFirefox(@NotNull WebDriver webDriver);

	/**
	 *
	 * @param webDriver The current web driver
	 * @return true if the webdriver is controlling the Chrome browser
	 */
	boolean isChrome(@NotNull WebDriver webDriver);

	/**
	 * @param webDriver The current web driver
	 * @return true if the webdriver is controlling the Safari browser
	 */
	boolean isSafari(@NotNull final WebDriver webDriver);

	/**
	 * @param webDriver The current web driver
	 * @return true if the webdriver is controlling a remote browser
	 */
	boolean isRemote(@NotNull WebDriver webDriver);

	/**
	 * @param webDriver The current web driver
	 * @return true if the webdriver is controlling an android browser
	 */
	boolean isAndroid(@NotNull WebDriver webDriver);

	/**
	 * @param webDriver The current web driver
	 * @return true if the webdriver is controlling an iPad
	 */
	boolean isIPad(@NotNull WebDriver webDriver);

	/**
	 * @param webDriver The current web driver
	 * @return true if the webdriver is controlling an iPad
	 */
	boolean isIPhone(@NotNull WebDriver webDriver);
}
