package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.ThreadDetails;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;

import cucumber.api.java.en.When;

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
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

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
}
