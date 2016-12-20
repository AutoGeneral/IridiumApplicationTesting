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
 * Gherkin steps for simulating custom HTML events.
 *
 * These steps have Atom snipptets that start with the prefix "disptach".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class CustomEventStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomEventStepDefinitions.class);
	@Autowired
	private GetBy getBy;
	@Autowired
	private SleepUtils sleepUtils;
	@Autowired
	private SimpleWebElementInteraction simpleWebElementInteraction;
	@Autowired
	private JavaScriptRunner javaScriptRunner;

	/**
	 * Manually dispatch a change event to the element
	 *
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 */
	@When("I(?: dispatch a)? \"change\"(?: event)? on (?:a|an|the) hidden element found by( alias)? \"([^\"]*)\"")
	public void triggerChange(
		final String alias,
		final String selectorValue) {

		final WebElement element = simpleWebElementInteraction.getPresenceElementFoundBy(
			StringUtils.isNotBlank(alias),
			selectorValue,
			State.getFeatureStateForThread());

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("var ev = document.createEvent('HTMLEvents');"
			+ "    ev.initEvent("
			+ "        'change',"
			+ "        false,"
			+ "		   true"
			+ "    );"
			+ "    arguments[0].dispatchEvent(ev);", element);
		sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
	}

	/**
	 * Manually dispatch a change event to the element
	 *
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 */
	@When("I(?: dispatch a)? \"change\"(?: event)? on (?:a|an|the) hidden element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\"")
	public void triggerChange(
			final String selector,
			final String alias,
			final String selectorValue) {

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final By by = getBy.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, State.getFeatureStateForThread());
		final WebDriverWait wait = new WebDriverWait(
			webDriver,
			State.getFeatureStateForThread().getDefaultWait(),
			Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
		final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;

		js.executeScript("var ev = document.createEvent('HTMLEvents');"
			+ "    ev.initEvent("
			+ "        'change',"
			+ "        false,"
			+ "		   true"
			+ "    );"
			+ "    arguments[0].dispatchEvent(ev);", element);

		sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
	}

	/**
	 * Manually dispatch a focus event to the element
	 *
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 */
	@When("I(?: dispatch a)? \"focus\"(?: event)? on (?:a|an|the) hidden element found by( alias)? \"([^\"]*)\"")
	public void triggetFocus(
		final String alias,
		final String selectorValue) {

		final WebElement element = simpleWebElementInteraction.getPresenceElementFoundBy(
			StringUtils.isNotBlank(alias),
			selectorValue,
			State.getFeatureStateForThread());

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("var ev = document.createEvent('HTMLEvents');"
			+ "    ev.initEvent("
			+ "        'focus',"
			+ "        false,"
			+ "		   true"
			+ "    );"
			+ "    arguments[0].dispatchEvent(ev);", element);
		sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
	}

	/**
	 * Manually dispatch a focus event to the element
	 *
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 */
	@When("I(?: dispatch a)? \"focus\"(?: event)? on (?:a|an|the) hidden element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\"")
	public void triggetFocus(
		final String selector,
		final String alias,
		final String selectorValue) {

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final By by = getBy.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, State.getFeatureStateForThread());
		final WebDriverWait wait = new WebDriverWait(
			webDriver,
			State.getFeatureStateForThread().getDefaultWait(),
			Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
		final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;

		js.executeScript("var ev = document.createEvent('HTMLEvents');"
			+ "    ev.initEvent("
			+ "        'focus',"
			+ "        false,"
			+ "		   true"
			+ "    );"
			+ "    arguments[0].dispatchEvent(ev);", element);

		sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
	}

	/**
	 * Manually dispatch a custom event to the element
	 *
	 * @param event         The type of event
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found
	 *                      from the data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If
	 *                      alias was set, this value is found from the data set. Otherwise it is a
	 *                      literal value.
	 */
	@When("I(?: dispatch a)? \"(.*?)\"(?: event)? on (?:a|an|the) hidden element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\"")
	public void triggetCustom(
		final String event,
		final String selector,
		final String alias,
		final String selectorValue) {

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final By by = getBy.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, State.getFeatureStateForThread());
		final WebDriverWait wait = new WebDriverWait(
			webDriver,
			State.getFeatureStateForThread().getDefaultWait(),
			Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
		final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;

		js.executeScript("var ev = document.createEvent('HTMLEvents');"
			+ "    ev.initEvent("
			+ "        '" + event.replace("'", "\\'") + "',"
			+ "        false,"
			+ "		   true"
			+ "    );"
			+ "    arguments[0].dispatchEvent(ev);", element);

		sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
	}
}
