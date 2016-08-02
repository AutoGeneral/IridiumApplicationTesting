package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.ScreenshotUtils;
import au.com.agic.apptesting.utils.impl.ScreenshotUtilsImpl;
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
	}
}
