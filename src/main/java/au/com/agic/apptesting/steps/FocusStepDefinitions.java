package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.impl.GetByImpl;
import au.com.agic.apptesting.utils.impl.SimpleWebElementInteractionImpl;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;

import java.util.concurrent.ExecutionException;

/**
 * Gherkin steps used to focus elements
 *
 * These steps have Atom snipptets that start with the prefix "focus".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class FocusStepDefinitions {

	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();
	private static final GetBy GET_BY = new GetByImpl();
	private static final SimpleWebElementInteraction SIMPLE_WEB_ELEMENT_INTERACTION =
		new SimpleWebElementInteractionImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final FeatureState featureState =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

	/**
	 * Focuses on an element. <p> Often with text fields that have some kind of mask you need to first focus
	 * on the element before populating it, otherwise you might not enter all characters correctly.
	 *
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I focus(?: on)? (?:a|an|the) element found by( alias)? "
		+ "\"([^\"]*)\"( if it exists)?$")
	public void focusElementStep(
		final String alias,
		final String selectorValue,
		final String exists) throws ExecutionException, InterruptedException {
		try {
			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getPresenceElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript("arguments[0].focus();", element);
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Focuses on an element. <p> Often with text fields that have some kind of mask you need to first focus
	 * on the element before populating it, otherwise you might not enter all characters correctly.
	 *
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I focus(?: on)? (?:a|an|the) element with (?:a|an|the) (ID|class|xpath|name|css selector)( alias)? "
		+ "of \"([^\"]*)\"( if it exists)?$")
	public void focusElementStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String exists) {
		try {
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebElement element = webDriver.findElement(by);
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript("arguments[0].focus();", element);
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

}
