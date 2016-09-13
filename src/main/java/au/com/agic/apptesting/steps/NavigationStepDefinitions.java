package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.SleepUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cucumber.api.java.en.When;

/**
 * Gherkin steps for navigating in the browser.
 *
 * These steps have Atom snipptets that start with the prefix "go".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class NavigationStepDefinitions {

	@Autowired
	private SleepUtils SLEEP_UTILS;
	@Autowired
	private AutoAliasUtils AUTO_ALIAS_UTILS;

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
				org.openqa.selenium.WebDriverException: Yikes! Safari history navigation does not work.
				We can go forward or back, but once we do, we can no longer communicate with the page...
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
				org.openqa.selenium.WebDriverException: Yikes! Safari history navigation does not work.
				We can go forward or back, but once we do, we can no longer communicate with the page...
			 */
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Refresh the page
	 * @param ignoreErrors Ignores any errors thrown by the web driver. Only useful for debugging
	 */
	@When("I refresh the page( ignoring errors)?")
	public void refresh(final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			webDriver.navigate().refresh();
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
		} catch (final Exception ex) {
			/*
				Safari doesn't support navigation:
				org.openqa.selenium.WebDriverException: Yikes! Safari history navigation does not work.
				We can go forward or back, but once we do, we can no longer communicate with the page...
			 */
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Updates the location with the given hash. This is useful for manually navigating
	 * around a single page application.
	 * @param alias Add this word to indicate that the hash comes from an alias
	 * @param hash The name of the hash
	 */
	@When("I go to the hash location called( alias)? \"(.*?)\"")
	public void openHash(final String alias, final String hash) {
		final String hashValue = AUTO_ALIAS_UTILS.getValue(hash, StringUtils.isNotBlank(alias), featureState);

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		((JavascriptExecutor) webDriver).executeScript("window.location.hash='#" + hashValue + "'");
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}
}
