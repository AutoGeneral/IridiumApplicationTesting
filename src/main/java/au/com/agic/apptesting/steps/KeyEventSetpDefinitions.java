package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.*;
import au.com.agic.apptesting.utils.impl.GetByImpl;
import au.com.agic.apptesting.utils.impl.JavaScriptRunnerImpl;
import au.com.agic.apptesting.utils.impl.SimpleWebElementInteractionImpl;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * Gherkin steps for simulating key presses and other key events.
 *
 * These steps have Atom snipptets that start with the prefix "disptach" and "press".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class KeyEventSetpDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(KeyEventSetpDefinitions.class);
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
	 * Press the CTRL-A keys to the active element
	 */
	@When("^I press CTRL-A on the active element")
	public void pressCtrlAStep() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final WebElement element = webDriver.switchTo().activeElement();
		element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Press the CMD-A keys to the active element
	 */
	@When("^I press CMD-A on the active element")
	public void pressCmdAStep() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final WebElement element = webDriver.switchTo().activeElement();
		element.sendKeys(Keys.chord(Keys.COMMAND, "a"));
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Press the CMD-A or CTRL-A keys to the active element depending on the client os
	 */
	@When("^I select all the text in the active element")
	public void pressCmdOrCtrlAStep() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final WebElement element = webDriver.switchTo().activeElement();

		if (SystemUtils.IS_OS_MAC) {
			element.sendKeys(Keys.chord(Keys.COMMAND, "a"));
		} else {
			element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		}
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	@When("^I press(?: the)? Delete(?: key)? on the active element(?: \"(\\d+)\" times)?")
	public void pressDeleteStep(final Integer times) {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final WebElement element = webDriver.switchTo().activeElement();

		for (int i = 0; i < ObjectUtils.defaultIfNull(times, 1); ++i) {
			element.sendKeys(Keys.DELETE);
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
		}
	}

	/**
	 * Press the tab key on the active element
	 */
	@When("^I press(?: the)? tab(?: key)? on the active element")
	public void pressTabStep() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final WebElement element = webDriver.switchTo().activeElement();
		element.sendKeys(Keys.TAB);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Press the tab key on the active element
	 */
	@When("^I press(?: the)? down arrow(?: key)? on the active element")
	public void pressDownArrowStep() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final WebElement element = webDriver.switchTo().activeElement();
		element.sendKeys(Keys.ARROW_DOWN);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Presses the backspace key on the active element
	 */
	@When("^I press(?: the)? backspace(?: key)? on the active element(?: \"(\\d+)\" times)")
	public void pressBackspaceStep(final Integer times) {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final WebElement element = webDriver.switchTo().activeElement();
		for (int i = 0; i < ObjectUtils.defaultIfNull(times, 1); ++i) {
			element.sendKeys(Keys.BACK_SPACE);
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
		}
	}

	/**
	 * Presses the enter key on the active element
	 */
	@When("^I press(?: the)? enter(?: key)? on the active element")
	public void pressEnterStep() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final WebElement element = webDriver.switchTo().activeElement();
		element.sendKeys(Keys.ENTER);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * sendKeys will often not trigger the key up event, which some elements of the page need in order to
	 * complete their processing. <p> Calling this step after you have populated the field can be used as a
	 * workaround.
	 *
	 * @param event 		The type of key event to simulate
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 */
	@When("I(?: dispatch a)? \"(key.*?)\"(?: event)? on (?:a|an|the) hidden element found by( alias)? \"([^\"]*)\"")
	public void triggetKeyUp(
		final String event,
		final String alias,
		final String selectorValue) throws ExecutionException, InterruptedException {
		final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getPresenceElementFoundBy(
			StringUtils.isNotBlank(alias),
			selectorValue,
			featureState);

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;
		JAVA_SCRIPT_RUNNER.interactHiddenElementKeyEvent(element, event, js);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * sendKeys will often not trigger the key up event, which some elements of the page need in order to
	 * complete their processing. <p> Calling this step after you have populated the field can be used as a
	 * workaround.
	 *
	 * @param event 		The type of key event to simulate
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 */
	@When("I(?: dispatch a)? \"(key.*?)\"(?: event)? on (?:a|an|the) hidden element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\"")
	public void triggetKeyUp(final String event, final String selector, final String alias, final String selectorValue) {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final By by = GET_BY.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, featureState);
		final WebDriverWait wait = new WebDriverWait(webDriver, featureState.getDefaultWait());
		final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;
		JAVA_SCRIPT_RUNNER.interactHiddenElementKeyEvent(element, event, js);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}
}
