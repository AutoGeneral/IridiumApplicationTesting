package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkState;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.BrowserInteropUtils;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.ScreenshotUtils;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.ThreadDetails;
import au.com.agic.apptesting.utils.impl.BrowserInteropUtilsImpl;
import au.com.agic.apptesting.utils.impl.GetByImpl;
import au.com.agic.apptesting.utils.impl.ScreenshotUtilsImpl;
import au.com.agic.apptesting.utils.impl.SimpleWebElementInteractionImpl;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;
import au.com.agic.apptesting.utils.impl.SystemPropertyUtilsImpl;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.security.UserAndPassword;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

/**
 * Implementations of the Cucumber steps.
 */
public class StepDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(StepDefinitions.class);

	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();
	private static final BrowserInteropUtils BROWSER_INTEROP_UTILS = new BrowserInteropUtilsImpl();
	private static final GetBy GET_BY = new GetByImpl();
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();
	private static final SimpleWebElementInteraction SIMPLE_WEB_ELEMENT_INTERACTION =
		new SimpleWebElementInteractionImpl();
	private static final ScreenshotUtils SCREENSHOT_UTILS = new ScreenshotUtilsImpl();

	private static final long MILLISECONDS_PER_SECOND = 1000;

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread(
			Thread.currentThread().getName());

	// <editor-fold desc="Events">

	/**
	 * If any scenario failed, we throw an exception which prevents the new scenario from loading. This
	 * prevents a situation where the test script continues to run after some earlier failure, which doesn't
	 * make sense in end to end tests.
	 */
	@Before
	public void setup() {
		if (threadDetails.getFailed()) {
			throw new IllegalStateException("Previous scenario failed!");
		}
	}

	/**
	 * If this scenario failed, note this in the thread details so subsequent scenarios are not run
	 *
	 * @param scenario The cucumber scenario
	 */
	@After
	public void teardown(final Scenario scenario) {
		if (!threadDetails.getFailed()) {
			SCREENSHOT_UTILS.takeScreenshot(" " + scenario.getName(), threadDetails);
		}

		threadDetails.setFailed(scenario.isFailed());
	}

	// </editor-fold>

	// <editor-fold desc="Initialisation">

	/**
	 * This step can be used to define the amount of time each additional step will wait before continuing.
	 * This is useful for web applications that pop new elements into the page in response to user
	 * interaction, as there can be a delay before those elements are available. <p> Set this to 0 to make
	 * each step execute immediately after the last one.
	 *
	 * @param numberOfSeconds The number of seconds to wait before each step completes
	 */
	@When("^I set the default wait time between steps to \"(\\d+)\"(?: seconds)?$")
	public void setDefaultWaitTime(final String numberOfSeconds) {
		threadDetails.setDefaultSleep(Integer.parseInt(numberOfSeconds) * MILLISECONDS_PER_SECOND);
	}

	// </editor-fold>

	// <editor-fold desc="Open Page">

	/**
	 * Takes a gerkin table and saves the key value pairs (key being alias names referenced in other steps).
	 *
	 * @param aliasTable The key value pairs
	 */
	@Given("^the alias mappings")
	public void pageObjectMappings(final Map<String, String> aliasTable) {
		final Map<String, String> dataset = threadDetails.getDataSet();
		dataset.putAll(aliasTable);
		threadDetails.setDataSet(dataset);
	}



	// </editor-fold>

	// <editor-fold desc="Selection and Focus">

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
				threadDetails).get();

			final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();
			js.executeScript("arguments[0].focus();", element);
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
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
				threadDetails);
			final WebElement element = threadDetails.getWebDriver().findElement(by);
			final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();
			js.executeScript("arguments[0].focus();", element);
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	// </editor-fold>

	// <editor-fold desc="Login">

	/**
	 * This code is supposed to populate the login dialog, but it actually doesn't work with most modern
	 * browsers.
	 *
	 * @param username The username
	 * @param password The password
	 * @param exists   If this text is set, an error that would be thrown because the element was not found is
	 *                 ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("I log in with username \"([^\"]*)\" and password \"([^\"]*)\"( if it exists)?$")
	public void login(final String username, final String password, final String exists) {
		try {
			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final Alert alert = wait.until(ExpectedConditions.alertIsPresent());
			alert.authenticateUsing(new UserAndPassword(username, password));
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	// </editor-fold>

	// <editor-fold desc="Navigation">

	/**
	 * Go back
	 */
	@When("I go back")
	public void goBack() {
		threadDetails.getWebDriver().navigate().back();
		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
	}

	/**
	 * Go forward
	 */
	@When("I go forward")
	public void goForward() {
		threadDetails.getWebDriver().navigate().forward();
		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
	}

	// </editor-fold>
}
