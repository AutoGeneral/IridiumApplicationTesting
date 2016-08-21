package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

/**
 * Gherkin steps for navigating in the browser.
 *
 * These steps have Atom snipptets that start with the prefix "go".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class NavigationStepDefinitions {

	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final FeatureState featureState =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

	/**
	 * Go back
	 * @param ignoreErrors Ignores any errors thrown by the web driver. Only useful for debugging
	 */
	@When("I go back( ignoring errors)?")
	public void goBack(final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			webDriver.navigate().back();
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
		} catch (final Exception ex) {
			/*
				Safari doesn't support navigation:
				org.openqa.selenium.WebDriverException: Yikes! Safari history navigation does not work. We can go forward or back, but once we do, we can no longer communicate with the page...
			 */
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Go forward
	 * @param ignoreErrors Ignores any errors thrown by the web driver. Only useful for debugging
	 */
	@When("I go forward( ignoring errors)?")
	public void goForward(final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			webDriver.navigate().forward();
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
		} catch (final Exception ex) {
			/*
				Safari doesn't support navigation:
				org.openqa.selenium.WebDriverException: Yikes! Safari history navigation does not work. We can go forward or back, but once we do, we can no longer communicate with the page...
			 */
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}
}
