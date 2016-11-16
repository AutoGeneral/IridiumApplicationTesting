package au.com.agic.apptesting.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import javax.validation.constraints.NotNull;

/**
 * Services for running JavaScript
 */
public interface JavaScriptRunner {
	/**
	 * use JavaScript to simulate a mouse event on an element
	 *
	 * @param element The element to click
	 * @param event   The type of event to simulate
	 * @param js      The JavaScript executor
	 */
	void interactHiddenElementMouseEvent(
		@NotNull WebElement element,
		@NotNull String event,
		@NotNull JavascriptExecutor js);

	/**
	 * use JavaScript to simulate a key event on an element
	 *
	 * @param element The element to click
	 * @param event   The type of event to simulate
	 * @param js      The JavaScript executor
	 */
	void interactHiddenElementKeyEvent(
		@NotNull WebElement element,
		@NotNull String event,
		@NotNull JavascriptExecutor js);
}
