package au.com.agic.apptesting.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.validation.constraints.NotNull;

/**
 * We need to test for certain conditions between the various browsers to account for compatibility
 * issues.
 */
public interface BrowserInteropUtils {

	/**
	 * PhantomJS has issues with <a href="javascript:;"></a> and <a href="#"></a> elements. This
	 * check determines if we need to treat the element as hidden, which means that we'll
	 * use javascript to click them instead of the webdriver.
	 *
	 * @param webDriver The selenium webdriver
	 * @param element The element to check
	 * @param js The javascript executor (which may or may not be the same object as webDriver)
	 * @return true if we need to treat this element as hidden, and false otherwise
	 */
	boolean treatElementAsHidden(
		@NotNull WebDriver webDriver,
		@NotNull WebElement element,
		@NotNull JavascriptExecutor js);

	/**
	 * Deals with inconsistencies between browsers in how they select items from a drop down list
	 * @param webDriver The selenium webdriver
	 * @param element The drop down list
	 * @param selectElement The item that we want to select
	 */
	void selectFromDropDownList(
		@NotNull WebDriver webDriver,
		@NotNull WebElement element,
		@NotNull String selectElement);

	/**
	 * Deals with inconsistencies between browsers in how they focus on items
	 * @param webDriver The selenium webdriver
	 * @param element The element to focus on
	 */
	void focusOnElement(
		@NotNull WebDriver webDriver,
		@NotNull WebElement element);

	/**
	 * Waits for an alert
	 *
	 * @param webDriver    The selenium webdriver
	 * @param waitDuration How long to wait for
	 */
	void waitForAlert(@NotNull WebDriver webDriver, int waitDuration);

	/**
	 * Accepts a browser alert
	 *
	 * @param webDriver The selenium webdriver
	 */
	void acceptAlert(@NotNull WebDriver webDriver);

	/**
	 * Cancels a browser alert
	 *
	 * @param webDriver The selenium webdriver
	 */
	void cancelAlert(@NotNull WebDriver webDriver);
}
