package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.ThreadDetails;

import java.util.Map;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

/**
 * Gherkin steps that are used to initialise the test script.
 *
 * These steps have Atom snipptets that start with the prefix "set".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class InitialisationStepDefinitions {
	private static final long MILLISECONDS_PER_SECOND = 1000;

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread(
			Thread.currentThread().getName());

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

	// </editor-fold>

	// <editor-fold desc="Open Page">

	/**
	 * Takes a gerkin table and saves the key value pairs (key being alias names referenced in other steps).
	 *
	 * @param aliasTable The key value pairs
	 */
	@Given("^(?:I set )?the alias mappings")
	public void pageObjectMappings(final Map<String, String> aliasTable) {
		final Map<String, String> dataset = threadDetails.getDataSet();
		dataset.putAll(aliasTable);
		threadDetails.setDataSet(dataset);
	}
}
