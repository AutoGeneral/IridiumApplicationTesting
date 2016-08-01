package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.ScreenshotUtils;
import au.com.agic.apptesting.utils.ThreadDetails;
import au.com.agic.apptesting.utils.impl.ScreenshotUtilsImpl;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import cucumber.api.java.en.When;

/**
 * Gherkin steps used to debug a test script.
 *
 * These steps have Atom snipptets that start with the prefix "dump" and "delete".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class DebuggingStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(DebuggingStepDefinitions.class);
	private static final ScreenshotUtils SCREENSHOT_UTILS = new ScreenshotUtilsImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

	/**
	 * Manually save a screenshot
	 *
	 * @param filename The optional filename to use for the screenshot
	 */
	@When("^I take a screenshot(?:(?: called)? \"(.*?)\")?$")
	public void takeScreenshotStep(final String filename) {
		SCREENSHOT_UTILS.takeScreenshot(
			StringUtils.defaultIfBlank(filename, ""),
			threadDetails);
	}

	/**
	 * Dumps the value of a cookie to the logger
	 *
	 * @param cookieName The name of the cookie to dump
	 */
	@When("^I dump the value of the cookie called \"(.*?)\"$")
	public void dumpCookieName(final String cookieName) {
		threadDetails.getWebDriver().manage().getCookies().stream()
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
		final List<Cookie> deleteCookies = threadDetails.getWebDriver().manage().getCookies().stream()
			.filter(e -> StringUtils.equals(cookieName, e.getName()))
			.filter(e -> StringUtils.isBlank(path) || StringUtils.equals(path, e.getPath()))
			.collect(Collectors.toList());

		deleteCookies.stream()
			.forEach(e -> {
				LOGGER.info("Removing cookie {}", e);
				threadDetails.getWebDriver().manage().deleteCookie(e);
			});
	}

	/**
	 * Deletes all cookies
	 */
	@When("^I delete all cookies$")
	public void deleteAllCookie() {
		threadDetails.getWebDriver().manage().deleteAllCookies();
	}

	@When("I dump the alias map to the console$")
	public void dumpAliasMap() {
		LOGGER.info("Dump of the alias map.");
		for (final String key : threadDetails.getDataSet().keySet()) {
			LOGGER.info("{}: {}", key, threadDetails.getDataSet().get(key));
		}
	}

	/**
	 * When comparing the performance of a page in a recorded video, it is useful to have some kind of
	 * visual indication when the script is started. This step dumps some text to a blank page before a
	 * URL is loaded.
	 */
	@When("I display a starting marker$")
	public void displayStartingMarker() {
		final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();
		js.executeScript("javascript:window.document.body.innerHTML = "
			+ "'<div style=\"margin: 50px; font-size: 20px\">Starting</div>'");
	}
}
