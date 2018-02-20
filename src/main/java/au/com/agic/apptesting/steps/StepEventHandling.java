package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.ScreenshotUtils;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.WebDriverFactory;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Deals with the events that related to step and scenario handing
 */
@Component
public class StepEventHandling {

	private static final Logger LOGGER = LoggerFactory.getLogger(StepEventHandling.class);
	@Autowired
	private SystemPropertyUtils systemPropertyUtils;
	@Autowired
	private ScreenshotUtils screenshotUtils;
	@Autowired
	private WebDriverFactory webDriverFactory;

	/**
	 * If any scenario failed, and Iridium is not set to continue after a scenario failure,
	 * we skip any additional steps. This prevents a situation where the test script continues
	 * to run after some earlier failure, which doesn't make sense in end to end tests.
	 */
	@Before
	public void setup() {

		final String endAfterFirstError =
			systemPropertyUtils.getProperty(Constants.FAIL_ALL_AFTER_FIRST_SCENARIO_ERROR);

		final boolean endAfterFirstErrorBool = StringUtils.isBlank(endAfterFirstError)
			|| Boolean.parseBoolean(endAfterFirstError);

		if (endAfterFirstErrorBool && State.getFeatureStateForThread().getFailed()) {
			State.getFeatureStateForThread().setSkipSteps(true);
		}
	}

	/**
	 * If this scenario failed, note this in the thread details so subsequent scenarios are not run
	 *
	 * @param scenario The cucumber scenario
	 */
	@After
	public void teardown(final Scenario scenario) {
		final boolean screenshotOnError = systemPropertyUtils.getPropertyAsBoolean(
			Constants.ENABLE_SCREENSHOT_ON_ERROR,
			false);
		final boolean newDriverPerScenario =
			systemPropertyUtils.getPropertyAsBoolean(
				Constants.NEW_BROWSER_PER_SCENARIO,
				false);
		final boolean enabledScreenshots = Boolean.parseBoolean(
			systemPropertyUtils.getProperty(Constants.ENABLE_SCREENSHOTS));

		/*
			Take a screenshot
		 */
		if (!State.getFeatureStateForThread().getFailed() && enabledScreenshots) {
			screenshotUtils.takeScreenshot(" " + scenario.getName(), State.getFeatureStateForThread());
		}

		State.getFeatureStateForThread().setFailed(scenario.isFailed());

		/*
			Take a screenshot on error if requested
		 */
		final boolean shouldTakeFailureScreenshot = State.getFeatureStateForThread().getFailed()
			&& !State.getFeatureStateForThread().getFailedScreenshotTaken()
			&& screenshotOnError;

		if (shouldTakeFailureScreenshot) {
			screenshotUtils.takeScreenshot(
				" " + Constants.FAILURE_SCREENSHOT_SUFFIX + " " + scenario.getName(),
				State.getFeatureStateForThread());
			State.getFeatureStateForThread().setFailedScreenshotTaken(true);
		}

		/*
			At the end of the scenario, the user may have chosen to destroy the
			web driver.
		 */
		if (newDriverPerScenario) {
			if (webDriverFactory.leaveWindowsOpen()) {
				State.getThreadDesiredCapabilityMap().clearWebDriverForThread(false);
			} else {
				State.getThreadDesiredCapabilityMap().clearWebDriverForThread(true);
			}
		}


	}
}
