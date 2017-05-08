package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.exception.BrowserWindowException;
import au.com.agic.apptesting.utils.SleepUtils;
import cucumber.api.java.en.When;
import javaslang.control.Try;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains Gherkin step definitions for working with tabs and windows
 */
@Component
public class TabAndWindowStepDefinition {
	private static final Logger LOGGER = LoggerFactory.getLogger(TabAndWindowStepDefinition.class);
	@Autowired
	private SleepUtils sleepUtils;

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
		sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
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

		sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
	}

	/**
	 * Switchs to the specified window. This is useful when you open a link that opens in a new
	 * window.
	 */
	@When("I close the current window")
	public void closeWindow() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		/*
			Close the current window
		 */
		webDriver.close();
		/*
			Switch to another window (otherwise all other commands fail)
		 */
		if (webDriver.getWindowHandles().iterator().hasNext()) {
			webDriver.switchTo().window(webDriver.getWindowHandles().iterator().next());
		} else {
			throw new BrowserWindowException("You can only use this step when there is more than one tab or window.");
		}

		sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
	}

	/**
	 * Maximise the browser window
	 */
	@When("I maximi(?:s|z)e the window")
	public void maximiseWindow() throws Throwable {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		/*
		 	This step will sometimes fail in Chrome, so retry a few times in the event of an error
		 	because it doesn't matter if we resize a few times.
		 	https://github.com/SeleniumHQ/selenium/issues/1853
		  */
		Try<Void> resizeTry = null;
		for (int i = 0; i < 3; ++i) {
			if ((resizeTry = Try.run(() -> webDriver.manage().window().maximize())).isSuccess()) {
				return;
			}
		}

		resizeTry.getOrElseThrow(ex -> ex);

		sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
	}

	/**
	 * Sets the dimensions of the browser window.
	 *
	 * @param width  The width of the browser window
	 * @param height The height of the browser window
	 */
	@When("I set the window size to \"(\\d+)x(\\d+)\"")
	public void setWindowSize(final Integer width, final Integer height) throws Throwable {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		/*
		 	This step will sometimes fail in Chrome, so retry a few times in the event of an error
		 	because it doesn't matter if we resize a few times.
		 	https://github.com/SeleniumHQ/selenium/issues/1853
		  */
		Try<Void> resizeTry = null;
		for (int i = 0; i < 3; ++i) {
			if ((resizeTry = resizeWindow(webDriver, width, height)).isSuccess()) {
				return;
			}
		}

		resizeTry.getOrElseThrow(ex -> ex);

		sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
	}

	private Try<Void> resizeWindow(@NotNull final WebDriver webDriver,
								   @NotNull final Integer width,
								   @NotNull final Integer height) {
		return Try.run(() -> webDriver.manage().window().setPosition(new Point(0, 0)))
			.andThenTry(() -> webDriver.manage().window().setSize(new Dimension(width, height)));
	}
}
