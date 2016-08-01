package au.com.agic.apptesting.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import javax.validation.constraints.NotNull;

/**
 * Services for running JavaScript
 */
public interface JavaScriptRunner {
	/**
	 * use JavaScript to simulate a click on an element
	 *
	 * @param element The element to click
	 * @param event   The type of event to simulate
	 * @param js      The JavaScript executor
	 */
	void interactHiddenElement(
		@NotNull final WebElement element,
		@NotNull final String event,
		@NotNull final JavascriptExecutor js);
}
