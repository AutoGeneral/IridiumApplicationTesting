package au.com.agic.apptesting.utils;

import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * A service for creating web driver instances
 */
public interface WebDriverFactory {
	/**
	 * @param proxies The list of proxies that are used when configuring the web driver
	 * @return A new instance of a webdriver
     */
	WebDriver createWebDriver(final List<ProxyDetails<?>> proxies);
}
