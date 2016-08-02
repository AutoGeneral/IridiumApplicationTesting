package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;
import cucumber.api.java.en.When;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains Gherkin step definitions for working with tabs and windows
 */
public class TabAndWindowStepDefinition {
	private static final Logger LOGGER = LoggerFactory.getLogger(TabAndWindowStepDefinition.class);
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final FeatureState featureState =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

	/**
	 * Switchs to the specified tab. This is useful when you open a link that opens in a new window.
	 *
	 * @param tabIndex The index of the new tab. Usually 1 (the original tab will be 0)
	 */
	@When("I switch to tab \"(\\d+)\"$")
	public void switchTabs(final String tabIndex) {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final List<String> tabs2 = new ArrayList<>(webDriver.getWindowHandles());
		webDriver.switchTo().window(tabs2.get(Integer.parseInt(tabIndex)));
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Switchs to the specified window. This is useful when you open a link that opens in a new window.
	 */
	@When("I switch to the new window")
	public void switchWindows() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		webDriver.getWindowHandles().stream()
			.filter(e -> !e.equals(webDriver.getWindowHandle()))
			.forEach(e -> webDriver.switchTo().window(e));

		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Maximise the browser window
	 */
	@When("I maximi(?:s|z)e the window")
	public void maximiseWindow() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		webDriver.manage().window().maximize();
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Sets the dimensions of the browser window
	 *
	 * @param width  The width of the browser window
	 * @param height The height of the browser window
	 */
	@When("I set the window size to \"(\\d+)x(\\d+)\"")
	public void setWindowSize(final Integer width, final Integer height) {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		webDriver.manage().window().setPosition(new Point(0, 0));
		webDriver.manage().window().setSize(new Dimension(width, height));
	}
}
