package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.ScreenshotUtils;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.WebDriverFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * Deals with the events that related to step and scenario handing
 */
@Component
public class StepEventHandling {

	private static final Logger LOGGER = LoggerFactory.getLogger(StepEventHandling.class);
	@Autowired
	private SystemPropertyUtils SYSTEM_PROPERTY_UTILS;
	@Autowired
	private ScreenshotUtils SCREENSHOT_UTILS;
	@Autowired
	private WebDriverFactory WEB_DRIVER_FACTORY;

	/**
	 * Get the web driver for this thread
	 */
	private final FeatureState featureState =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

	/**
	 * If any scenario failed, we throw an exception which prevents the new scenario from loading. This
	 * prevents a situation where the test script continues to run after some earlier failure, which doesn't
	 * make sense in end to end tests.
	 */
	@Before
	public void setup() {

		final String endAfterFirstError =
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.FAIL_ALL_AFTER_FIRST_SCENARIO_ERROR);
		final boolean endAfterFirstErrorBool = StringUtils.isBlank(endAfterFirstError)
			|| Boolean.parseBoolean(endAfterFirstError);

		if (endAfterFirstErrorBool && featureState.getFailed()) {
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
		/*
			At the end of the scenario, the user may have chosen to destroy the
			web driver.
		 */
		final String newDriverPerScenario =
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.NEW_BROWSER_PER_SCENARIO);
		final boolean clearDriver = Boolean.parseBoolean(newDriverPerScenario);

		if (clearDriver) {
			if (WEB_DRIVER_FACTORY.leaveWindowsOpen()) {
				State.THREAD_DESIRED_CAPABILITY_MAP.clearWebDriverForThread(false);
			} else {
				State.THREAD_DESIRED_CAPABILITY_MAP.clearWebDriverForThread(true);
			}
		}

		/*
			Take a screenshot
		 */
		if (!featureState.getFailed()) {
			SCREENSHOT_UTILS.takeScreenshot(" " + scenario.getName(), featureState);
		}

		featureState.setFailed(scenario.isFailed());
	}
}
