package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkArgument;

import au.com.agic.apptesting.State;

import org.springframework.stereotype.Component;

import java.util.Map;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

/**
 * Gherkin steps that are used to initialise the test script.
 *
 * These steps have Atom snipptets that start with the prefix "set".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class InitialisationStepDefinitions {
	private static final long MILLISECONDS_PER_SECOND = 1000;

	/**
	 * Sets the default amount of time to wait between simulated key presses
	 * @param delay The number of milliseconds to pause between simulated key presses
	 */
	@When("^I set the default keystroke delay to \"(\\d+)\" milliseconds$")
	public void setKeystrokeDelay(final Integer delay) {
		checkArgument(delay >= 0);
		State.getFeatureStateForThread().setDefaultKeyStrokeDelay(delay);
	}

	/**
	 * This step can be used to define the amount of time each additional step will wait before continuing.
	 * This is useful for web applications that pop new elements into the page in response to user
	 * interaction, as there can be a delay before those elements are available. <p> Set this to 0 to make
	 * each step execute immediately after the last one.
	 *
	 * @param numberOfSeconds The number of seconds to wait before each step completes
	 */
	@When("^I set the default wait time between steps to \"(\\d+(?:\\.\\d+)?)\"(?: seconds?)?$")
	public void setDefaultSleepTime(final String numberOfSeconds) {
		final float waitTime = Float.parseFloat(numberOfSeconds) * MILLISECONDS_PER_SECOND;
		State.getFeatureStateForThread().setDefaultSleep((long) waitTime);
	}

	/**
	 * When elements are requested by steps in Iridium, an optional wait time can be defined
	 * that will allow the script to account for elements that might not yet be present. This
	 * wait time is set to {@link au.com.agic.apptesting.constants.Constants#WAIT} by default,
	 * and can be overriden with this step.
	 * @param numberOfSeconds The number of seconds to wait for elements to be available
	 *                        before continuing with a step
	 */
	@When("^I set the default wait for elements to be available to \"(\\d+)\"(?: seconds?)?$")
	public void setDefaultWaitTime(final Integer numberOfSeconds) {
		checkArgument(numberOfSeconds >= 0);
		State.getFeatureStateForThread().setDefaultWait(numberOfSeconds);
	}

	/**
	 * Takes a gerkin table and saves the key value pairs (key being alias names referenced in other steps).
	 *
	 * @param aliasTable The key value pairs
	 */
	@Given("^(?:I set )?the alias mappings")
	public void pageObjectMappings(final Map<String, String> aliasTable) {
		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.putAll(aliasTable);
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Enables or disables autoaliasing.
	 * @param enabled set to true or false
	 */
	@Given("^I set autoaliasing to \"((?:true)|(?:false))\"$")
	public void configureAutoaliasing(final String enabled) {
		final boolean enabledValue = Boolean.parseBoolean(enabled);
		State.getFeatureStateForThread().setAutoAlias(enabledValue);
	}

	/**
	 * Enables autoaliasing.
	 */
	@Given("^I enable autoaliasing$")
	public void enableAutoaliasing() {
		State.getFeatureStateForThread().setAutoAlias(true);
	}

	/**
	 * Disables autoaliasing.
	 */
	@Given("^I disable autoaliasing$")
	public void disableAutoaliasing() {
		State.getFeatureStateForThread().setAutoAlias(false);
	}
}
