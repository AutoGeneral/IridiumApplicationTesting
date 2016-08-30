package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.JavaScriptRunner;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.impl.GetByImpl;
import au.com.agic.apptesting.utils.impl.JavaScriptRunnerImpl;
import au.com.agic.apptesting.utils.impl.SimpleWebElementInteractionImpl;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.en.When;

/**
 * Gherkin steps for simulating custom HTML events.
 *
 * These steps have Atom snipptets that start with the prefix "disptach".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class CustomEventStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomEventStepDefinitions.class);
	private static final GetBy GET_BY = new GetByImpl();
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();
	private static final SimpleWebElementInteraction SIMPLE_WEB_ELEMENT_INTERACTION =
		new SimpleWebElementInteractionImpl();
	private static final JavaScriptRunner JAVA_SCRIPT_RUNNER = new JavaScriptRunnerImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final FeatureState featureState =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

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

		final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getPresenceElementFoundBy(
			StringUtils.isNotBlank(alias),
			selectorValue,
			featureState);

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("var ev = document.createEvent('HTMLEvents');"
			+ "    ev.initEvent("
			+ "        'change',"
			+ "        false,"
			+ "		   true"
			+ "    );"
			+ "    arguments[0].dispatchEvent(ev);", element);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
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
		final By by = GET_BY.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, featureState);
		final WebDriverWait wait = new WebDriverWait(webDriver, featureState.getDefaultWait());
		final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;

		js.executeScript("var ev = document.createEvent('HTMLEvents');"
			+ "    ev.initEvent("
			+ "        'change',"
			+ "        false,"
			+ "		   true"
			+ "    );"
			+ "    arguments[0].dispatchEvent(ev);", element);

		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
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

		final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getPresenceElementFoundBy(
			StringUtils.isNotBlank(alias),
			selectorValue,
			featureState);

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("var ev = document.createEvent('HTMLEvents');"
			+ "    ev.initEvent("
			+ "        'focus',"
			+ "        false,"
			+ "		   true"
			+ "    );"
			+ "    arguments[0].dispatchEvent(ev);", element);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
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
		final By by = GET_BY.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, featureState);
		final WebDriverWait wait = new WebDriverWait(webDriver, featureState.getDefaultWait());
		final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;

		js.executeScript("var ev = document.createEvent('HTMLEvents');"
			+ "    ev.initEvent("
			+ "        'focus',"
			+ "        false,"
			+ "		   true"
			+ "    );"
			+ "    arguments[0].dispatchEvent(ev);", element);

		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}
}
