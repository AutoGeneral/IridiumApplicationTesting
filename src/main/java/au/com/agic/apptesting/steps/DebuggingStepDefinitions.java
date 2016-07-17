package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.ScreenshotUtils;
import au.com.agic.apptesting.utils.ThreadDetails;
import au.com.agic.apptesting.utils.impl.ScreenshotUtilsImpl;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import cucumber.api.java.en.When;

/**
 * Created by mcasperson on 18/07/2016.
 */
public class DebuggingStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(DebuggingStepDefinitions.class);
	private static final ScreenshotUtils SCREENSHOT_UTILS = new ScreenshotUtilsImpl();
	private static final long MILLISECONDS_PER_SECOND = 1000;

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread(
			Thread.currentThread().getName());

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

	// </editor-fold>

	// <editor-fold desc="Initialisation">

	/**
	 * This step can be used to define the amount of time each additional step will wait before continuing.
	 * This is useful for web applications that pop new elements into the page in response to user
	 * interaction, as there can be a delay before those elements are available. <p> Set this to 0 to make
	 * each step execute immediately after the last one.
	 *
	 * @param numberOfSeconds The number of seconds to wait before each step completes
	 */
	@When("^I set the default wait time between steps to \"(\\d+)\"(?: seconds)?$")
	public void setDefaultWaitTime(final String numberOfSeconds) {
		threadDetails.setDefaultSleep(Integer.parseInt(numberOfSeconds) * MILLISECONDS_PER_SECOND);
	}
}
