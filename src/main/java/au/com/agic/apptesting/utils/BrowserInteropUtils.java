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
		@NotNull final WebDriver webDriver,
		@NotNull final WebElement element,
		@NotNull final JavascriptExecutor js);
}
