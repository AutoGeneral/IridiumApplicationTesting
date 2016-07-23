package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkState;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.ThreadDetails;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.en.When;

/**
 * Gherin steps used to open web pages.
 *
 * These steps have Atom snipptets that start with the prefix "open".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class OpenStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenStepDefinitions.class);
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
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
		threadDetails.getWebDriver().get(
			StringUtils.isNotBlank(alias) ? threadDetails.getDataSet().get(url) : url);
		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
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

		if (StringUtils.isNotBlank(urlName)) {
			LOGGER.info("WEBAPPTESTER-INFO-0001: Opened the url {}",
				threadDetails.getUrlDetails().getUrl(urlName));

			final String url = threadDetails.getUrlDetails().getUrl(urlName);

			checkState(StringUtils.isNotBlank(url), "The url associated with the app name "
				+ urlName + " was not found. "
				+ "This may mean that you have defined a default URL with the appURLOverride "
				+ "system property. "
				+ "When you set the appURLOverride system property, you can no longer reference "
				+ "named applications. "
				+ "Alternatively, make sure the configuration file defines the named application.");

			threadDetails.getWebDriver().get(url);
		} else {
			LOGGER.info("WEBAPPTESTER-INFO-0001: Opened the url {}",
				threadDetails.getUrlDetails().getDefaultUrl());
			threadDetails.getWebDriver().get(threadDetails.getUrlDetails().getDefaultUrl());
		}

		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
	}
}
