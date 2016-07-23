package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.ThreadDetails;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import cucumber.api.java.en.When;

/**
 * This class contains Gherkin step definitions for working with tabs and windows
 */
public class TabAndWindowStepDefinition {
	private static final Logger LOGGER = LoggerFactory.getLogger(TabAndWindowStepDefinition.class);
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

	/**
	 * Switchs to the specified tab. This is useful when you open a link that opens in a new window.
	 *
	 * @param tabIndex The index of the new tab. Usually 1 (the original tab will be 0)
	 */
	@When("I switch to tab \"(\\d+)\"$")
	public void switchTabs(final String tabIndex) {
		List<String> tabs2 = new ArrayList<>(threadDetails.getWebDriver().getWindowHandles());
		threadDetails.getWebDriver().switchTo().window(tabs2.get(Integer.parseInt(tabIndex)));
		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
	}

	/**
	 * Switchs to the specified window. This is useful when you open a link that opens in a new window.
	 */
	@When("I switch to the new window")
	public void switchWindows() {
		threadDetails.getWebDriver().getWindowHandles().stream()
			.filter(e -> !e.equals(threadDetails.getWebDriver().getWindowHandle()))
			.forEach(e -> threadDetails.getWebDriver().switchTo().window(e));

		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
	}

	/**
	 * Maximise the browser window
	 */
	@When("I maximi(?:s|z)e the window")
	public void maximiseWindow() {
		threadDetails.getWebDriver().manage().window().maximize();
		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
	}

	/**
	 * Sets the dimensions of the browser window
	 *
	 * @param width  The width of the browser window
	 * @param height The height of the browser window
	 */
	@When("I set the window size to \"(\\d+)x(\\d+)\"")
	public void setWindowSize(final Integer width, final Integer height) {
		threadDetails.getWebDriver().manage().window().setPosition(new Point(0, 0));
		threadDetails.getWebDriver().manage().window().setSize(new Dimension(width, height));
	}
}
