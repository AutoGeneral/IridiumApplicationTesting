package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;
import cucumber.api.java.en.When;
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
	 */
	@When("I go back")
	public void goBack() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		webDriver.navigate().back();
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Go forward
	 */
	@When("I go forward")
	public void goForward() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		webDriver.navigate().forward();
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}
}
