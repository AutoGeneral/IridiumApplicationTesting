package au.com.agic.apptesting.utils;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.impl.SystemPropertyUtilsImpl;
import org.openqa.selenium.WebDriver;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;

/**
 * A service for creating web driver instances
 */
public interface WebDriverFactory {
	SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	/**
	 * @param proxies The list of proxies that are used when configuring the web driver
	 * @param tempFiles maintains a list of temp files that are deleted once Iridium is closed
	 * @return A new instance of a webdriver
     */
	WebDriver createWebDriver(@NotNull List<ProxyDetails<?>> proxies, @NotNull List<File> tempFiles);

	/**
	 *
	 * @return true if webdriver instances should be quit after the feature or scenario has
	 * been run.
	 */
	default boolean leaveWindowsOpen() {
		final String leaveWindowsOpen =
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.LEAVE_WINDOWS_OPEN_SYSTEM_PROPERTY);
		final String browser =
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.TEST_DESTINATION_SYSTEM_PROPERTY);

		/*
			We can't leave the window open for headless browsers
		 */
		return Boolean.parseBoolean(leaveWindowsOpen)
			&& !Constants.PHANTOMJS.equalsIgnoreCase(browser);
	}
}
