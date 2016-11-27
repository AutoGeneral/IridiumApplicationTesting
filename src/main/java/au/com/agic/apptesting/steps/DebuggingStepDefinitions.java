package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.ScreenshotUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import cucumber.api.java.en.When;

/**
 * Gherkin steps used to debug a test script.
 *
 * These steps have Atom snipptets that start with the prefix "dump" and "delete".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class DebuggingStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(DebuggingStepDefinitions.class);
	@Autowired
	private ScreenshotUtils screenshotUtils;

	/**
	 * Manually save a screenshot
	 *
	 * @param filename The optional filename to use for the screenshot
	 */
	@When("^I take a screenshot(?:(?: called)? \"(.*?)\")?$")
	public void takeScreenshotStep(final String filename) {
		screenshotUtils.takeScreenshot(
			StringUtils.defaultIfBlank(filename, ""),
			State.getFeatureStateForThread());
	}

	/**
	 * Dumps the value of a cookie to the logger
	 *
	 * @param cookieName The name of the cookie to dump
	 */
	@When("^I dump the value of the cookie called \"(.*?)\"$")
	public void dumpCookieName(final String cookieName) {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		webDriver.manage().getCookies().stream()
			.filter(e -> StringUtils.equals(cookieName, e.getName()))
			.forEach(e -> LOGGER.info("Dumping cookie {}", e));
	}

	/**
	 * Deletes a cookie with the name and path
	 *
	 * @param cookieName The name of the cookie to delete
	 * @param path       The optional path of the cookie to delete. If omitted, all cookies with the
	 *                   cookieName are deleted.
	 */
	@When("^I delete cookies called \"(.*?)\"(?: with the path \"(.*?)\")?$")
	public void deleteCookie(final String cookieName, final String path) {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final List<Cookie> deleteCookies = webDriver.manage().getCookies().stream()
			.filter(e -> StringUtils.equals(cookieName, e.getName()))
			.filter(e -> StringUtils.isBlank(path) || StringUtils.equals(path, e.getPath()))
			.collect(Collectors.toList());

		deleteCookies.stream()
			.forEach(e -> {
				LOGGER.info("Removing cookie {}", e);
				webDriver.manage().deleteCookie(e);
			});
	}

	/**
	 * Deletes all cookies
	 */
	@When("^I delete all cookies$")
	public void deleteAllCookie() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		webDriver.manage().deleteAllCookies();
	}

	/**
	 * Dumps the alias map to the console
	 */
	@When("I dump the alias map to the console$")
	public void dumpAliasMap() {
		LOGGER.info("Dump of the alias map for thread {}", Thread.currentThread().getName());
		for (final String key : State.getFeatureStateForThread().getDataSet().keySet()) {
			LOGGER.info("{}: {}", key, State.getFeatureStateForThread().getDataSet().get(key));
		}
	}

	/**
	 * Dumps the value of an alias to the console. This is useful when running test scripts
	 * with data sets and finding out what value was used in a step for a certain run.
	 * @param alias The name of the alias
	 */
	@When("I dump the value of the alias \"(.*?)\" to the console$")
	public void dumpIndividualAlias(final String alias) {
		LOGGER.info("Alias {} is set to {}", alias, State.getFeatureStateForThread().getDataSet().get(alias));
	}

	/**
	 * When comparing the performance of a page in a recorded video, it is useful to have some kind of
	 * visual indication when the script is started. This step dumps some text to a blank page before a
	 * URL is loaded.
	 */
	@When("I display a starting marker$")
	public void displayStartingMarker() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("javascript:window.document.body.innerHTML = "
			+ "'<div style=\"margin: 50px; font-size: 20px\">Starting</div>'");
	}
}
