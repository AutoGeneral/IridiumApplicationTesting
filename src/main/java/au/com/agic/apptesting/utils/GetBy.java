package au.com.agic.apptesting.utils;

import org.openqa.selenium.By;

/**
 * A service for creating WebDriver By objects
 */
public interface GetBy {
	String CSS_SELECTOR = "css selector";
	String ID = "ID";
	String XPATH = "xpath";
	String NAME = "name";
	String VALUE = "value";
	String TEXT = "text";
	String CLASS = "class";

	/**
	 * Map common selectors to Selenium selection objects
	 *
	 * @param selector   The name of the selector
	 * @param valueAlias true if the value is an alias, false otherwose
	 * @param value      The alias key or value to use with the selector
	 * @param threadDetails The current thread's state object
	 * @return The Selenium selection object
	 */
	By getBy(
		final String selector,
		final boolean valueAlias,
		final String value,
		final ThreadDetails threadDetails);
}
