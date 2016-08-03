package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.ScreenshotUtils;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.impl.ScreenshotUtilsImpl;
import au.com.agic.apptesting.utils.impl.SystemPropertyUtilsImpl;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deals with the events that related to step and scenario handing
 */
public class StepEventHandling {

	private static final Logger LOGGER = LoggerFactory.getLogger(StepEventHandling.class);
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();
	private static final ScreenshotUtils SCREENSHOT_UTILS = new ScreenshotUtilsImpl();

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
		if (featureState.getFailed()) {
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
		if (!featureState.getFailed()) {
			SCREENSHOT_UTILS.takeScreenshot(" " + scenario.getName(), featureState);
		}

		featureState.setFailed(scenario.isFailed());

		/*
			At the end of the scenario, the user may have choosen to destroy the
			web driver.
		 */
		final boolean clearDriver =
			Boolean.parseBoolean(
				SYSTEM_PROPERTY_UTILS.getProperty(Constants.NEW_BROWSER_PER_SCENARIO));
		if (clearDriver) {
			State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread().quit();
			State.THREAD_DESIRED_CAPABILITY_MAP.clearWebDriverForThread();
		}
	}
}
