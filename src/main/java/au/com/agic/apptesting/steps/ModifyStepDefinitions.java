package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.en.Then;

/**
 * Contains Gherkin step definitions for modifying aliased values.
 *
 * These steps have Atom snipptets that start with the prefix "modify".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class ModifyStepDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModifyStepDefinitions.class);
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final FeatureState featureState =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

	/**
	 * Modify an aliased value. This is useful if you want to turn a string like $1,2345.50
	 * to an decimal like 12345.50.
	 *
	 * @param alias The alias to modify
	 * @param regex The regex to match
	 */
	@Then("^I modify the alias \"(.*?)\" by removing all characters that match the regex \"(.*?)\"$")
	public void removeCharsInAlias(final String alias, final String regex) {
		final String value = featureState.getDataSet().get(alias);
		final String fixedValue = value.replaceAll(regex, "");
		featureState.getDataSet().put(alias, fixedValue);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Modify an aliased value by replacing any matched characters.
	 *
	 * @param alias The alias to modify
	 * @param regex The regex to match
	 * @param replacement The text to replace any matched string with
	 */
	@Then("^I modify the alias \"(.*?)\" by replacing all characters that match the regex \"(.*?)\" with \"(.*?)\"$")
	public void replaceCharsInAlias(final String alias, final String regex, final String replacement) {
		final String value = featureState.getDataSet().get(alias);
		final String fixedValue = value.replaceAll(regex, replacement);
		featureState.getDataSet().put(alias, fixedValue);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Modify an aliased value by replacing the first matched characters.
	 *
	 * @param alias The alias to modify
	 * @param regex The regex to match
	 * @param replacement The text to replace the first matched string with
	 */
	@Then("^I modify the alias \"(.*?)\" by replacing the first characters that match the regex \"(.*?)\" with \"(.*?)\"$")
	public void replaceFirstCharsInAlias(final String alias, final String regex, final String replacement) {
		final String value = featureState.getDataSet().get(alias);
		final String fixedValue = value.replaceFirst(regex, replacement);
		featureState.getDataSet().put(alias, fixedValue);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Modify an aliased value by prepending it with a string.
	 *
	 * @param alias The alias to modify
	 * @param prepend The text to prepend the aliased value with
	 */
	@Then("^I modify the alias \"(.*?)\" by prepending it with \"(.*?)\"$")
	public void prependAlias(final String alias, final String prepend) {
		final String value = featureState.getDataSet().get(alias);
		featureState.getDataSet().put(alias, prepend + value);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}

	/**
	 * Modify an aliased value by appending it with a string.
	 *
	 * @param alias The alias to modify
	 * @param prepend The text to prepend the aliased value with
	 */
	@Then("^I modify the alias \"(.*?)\" by appending it with \"(.*?)\"$")
	public void appendAlias(final String alias, final String append) {
		final String value = featureState.getDataSet().get(alias);
		featureState.getDataSet().put(alias, value + append);
		SLEEP_UTILS.sleep(featureState.getDefaultSleep());
	}
}
