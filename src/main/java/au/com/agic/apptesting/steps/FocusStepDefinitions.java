package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.exception.WebElementException;
import au.com.agic.apptesting.utils.BrowserInteropUtils;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cucumber.api.java.en.When;

/**
 * Gherkin steps used to focus elements
 *
 * These steps have Atom snipptets that start with the prefix "focus".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class FocusStepDefinitions {

	@Autowired
	private SleepUtils sleepUtils;

	@Autowired
	private GetBy getBy;

	@Autowired
	private SimpleWebElementInteraction simpleWebElementInteraction;

	@Autowired
	private BrowserInteropUtils browserInteropUtils;

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
	@When("^I focus(?: on)? (?:a|an|the)(?: element found by)?( alias)? "
		+ "\"([^\"]*)\"(?: \\w+)*?( if it exists)?$")
	public void focusElementStep(
		final String alias,
		final String selectorValue,
		final String exists) {
		try {
			final WebElement element = simpleWebElementInteraction.getPresenceElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			browserInteropUtils.focusOnElement(webDriver, element);
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final WebElementException ex) {
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
			final By by = getBy.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebElement element = webDriver.findElement(by);
			browserInteropUtils.focusOnElement(webDriver, element);
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

}
