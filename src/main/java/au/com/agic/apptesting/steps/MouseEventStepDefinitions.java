package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.JavaScriptRunner;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cucumber.api.java.en.When;

/**
 * Gherkin steps for sending javascript events to elements.
 *
 * These steps have Atom snipptets that start with the prefix "mouse".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class MouseEventStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(MouseEventStepDefinitions.class);
	@Autowired
	private GetBy getBy;
	@Autowired
	private SimpleWebElementInteraction simpleWebElementInteraction;
	@Autowired
	private SleepUtils sleepUtils;
	@Autowired
	private JavaScriptRunner javaScriptRunner;

	/**
	 * Some applications use mouse events instead of clicks, and PhantomJS will often need us to supply these
	 * events manually. This step uses simple selection.
	 *
	 * @param event         The mouse event we want to generate (mousedown, mouseup etc)
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I(?: dispatch a)? ?\"(mouse.*?)\"(?: event)? on (?:a|an|the) hidden element found by"
			+ "( alias)? \"([^\"]*)\"( if it exists)?$")
	public void mouseEventSimpleHiddenElementStep(
		final String event,
		final String alias,
		final String selectorValue,
		final String exists) {

		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebElement element = simpleWebElementInteraction.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			/*
				Just like the click, sometimes we need to trigger mousedown events manually
			 */
			javaScriptRunner.interactHiddenElementMouseEvent(element, event, js);
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Some applications use mouse events instead of clicks, and PhantomJS will often need us to supply these
	 * events manually.
	 *
	 * @param event         The mouse event we want to generate (mousedown, mouseup etc)
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I(?: dispatch a)? ?\"(mouse.*?)\"(?: event)? on (?:a|an|the) hidden element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\"( if it exists)?$")
	public void mouseEventHiddenElementStep(
		final String event,
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
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			/*
				Just like the click, sometimes we need to trigger mousedown events manually
			 */
			javaScriptRunner.interactHiddenElementMouseEvent(element, event, js);
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}
}
