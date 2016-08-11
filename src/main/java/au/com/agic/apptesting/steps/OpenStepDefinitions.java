package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

/**
 * Gherin steps used to open web pages.
 *
 * These steps have Atom snipptets that start with the prefix "open".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class OpenStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenStepDefinitions.class);
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();
	private static final int LINK_OPEN_POOL_COUNT = 5;
	/**
	 * This has to be long enough to allow a request to be made, but too long
	 * and testing a page with lots of links will take forever.
	 */
	private static final int TAB_OPEN_TIME = 5000;

	/**
	 * Get the web driver for this thread
	 */
	private final FeatureState featureState =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

	/**
	 * Opens up the supplied URL.
	 *
	 * @param alias include this text if the url is actually an alias to be loaded from the configuration
	 *              file
	 * @param url   The URL of the page to open
	 */
	@When("^I open the page( alias)? \"([^\"]*)\"$")
	public void openPage(final String alias, final String url) {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		webDriver.get(
			StringUtils.isNotBlank(alias) ? featureState.getDataSet().get(url) : url);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Opens up the application with the URL that is mapped to the app attribute in the {@code <feature>}
	 * element in the profile holding the test script. <p> This is different to the "{@code I open the page
	 * <url>}" step in that the URL that is actually used comes from a list maintained in the
	 * WebAppTesting-Capabilities profile. This means that the same script can be run multiple times against
	 * different URLs. This is usually used when you want to test multiple brands, or multiple feature
	 * branches.
	 *
	 * @param urlName The URL name from mappings to load.
	 */
	@When("^I open the application(?: \"([^\"]*)\")?$")
	public void openApplication(final String urlName) {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();

		if (StringUtils.isNotBlank(urlName)) {
			LOGGER.info("WEBAPPTESTER-INFO-0001: Opened the url {}",
				featureState.getUrlDetails().getUrl(urlName));

			final String url = featureState.getUrlDetails().getUrl(urlName);

			checkState(StringUtils.isNotBlank(url), "The url associated with the app name "
				+ urlName + " was not found. "
				+ "This may mean that you have defined a default URL with the appURLOverride "
				+ "system property. "
				+ "When you set the appURLOverride system property, you can no longer reference "
				+ "named applications. "
				+ "Alternatively, make sure the configuration file defines the named application.");


			webDriver.get(url);
		} else {
			LOGGER.info("WEBAPPTESTER-INFO-0001: Opened the url {}",
				featureState.getUrlDetails().getDefaultUrl());
			webDriver.get(featureState.getUrlDetails().getDefaultUrl());
		}

		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Scans the page for all link elements, opens them in new tabs, and then closes the tabs. This
	 * is most useful when used in conjunction with the "I verify that there were no HTTP errors" step
	 * as a way of verifying that all links open valid pages.
	 */
	@When("^I open all links in new tabs and then close the tabs$")
	public void openAllLinks() throws InterruptedException {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final JavascriptExecutor js = JavascriptExecutor.class.cast(webDriver);
		final List<WebElement> links = webDriver.findElements(By.tagName("a"));
		final ExecutorService executor = Executors.newFixedThreadPool(LINK_OPEN_POOL_COUNT);

		final List<Callable<Object>> calls = links.stream()
			.map(x -> new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					try {
						final URL url = new URL(x.getAttribute("href"));
						final String urlString = url.toString();
						js.executeScript("(function() {"
							+ "var newWindow = window.open('" + urlString + "','_blank'); "
							+ "window.setTimeout(function(){newWindow.close()}, "
							+ TAB_OPEN_TIME
							+ ");"
							+ "})()"
						);

						SLEEP_UTILS.sleep(TAB_OPEN_TIME);
					} catch (final Exception ignored) {
						/*
							ignored because the link didn't contain a valid url
						 */
					}

					return null;
				}
			})
		.collect(Collectors.toList());

		executor.invokeAll(calls);
	}
}
