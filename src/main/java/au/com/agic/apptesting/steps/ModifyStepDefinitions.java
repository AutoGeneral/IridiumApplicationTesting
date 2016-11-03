package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.exception.InvalidInputException;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import au.com.agic.apptesting.utils.ChronoConverterUtils;
import au.com.agic.apptesting.utils.SleepUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Contains Gherkin step definitions for modifying aliased values.
 *
 * These steps have Atom snipptets that start with the prefix "modify".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class ModifyStepDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModifyStepDefinitions.class);
	@Autowired
	private SleepUtils SLEEP_UTILS;
	@Autowired
	private ChronoConverterUtils CHRONO_CONVERTER_UTILS;
	@Autowired
	private AutoAliasUtils AUTO_ALIAS_UTILS;

	/**
	 * Modify an aliased value. This is useful if you want to turn a string like $1,2345.50
	 * to an decimal like 12345.50.
	 *
	 * @param alias The alias to modify
	 * @param regex The regex to match
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by removing all characters that match the regex \"(.*?)\"$")
	public void removeCharsInAlias(final String alias, final String regex) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		final String fixedValue = value.replaceAll(regex, "");
		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(alias, fixedValue);
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Modify an aliased value by replacing any matched characters.
	 *
	 * @param alias The alias to modify
	 * @param regex The regex to match
	 * @param replacement The text to replace any matched string with
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by replacing all characters that match the regex "
			+ "\"(.*?)\" with \"(.*?)\"$")
	public void replaceCharsInAlias(final String alias, final String regex, final String replacement) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		final String fixedValue = value.replaceAll(regex, replacement);
		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(alias, fixedValue);
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Modify an aliased value by replacing the first matched characters.
	 *
	 * @param alias The alias to modify
	 * @param regex The regex to match
	 * @param replacement The text to replace the first matched string with
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by replacing the first characters that match the regex "
			+ "\"(.*?)\" with \"(.*?)\"$")
	public void replaceFirstCharsInAlias(final String alias, final String regex, final String replacement) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		final String fixedValue = value.replaceFirst(regex, replacement);
		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(alias, fixedValue);
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Modify an aliased value by prepending it with a string.
	 *
	 * @param alias The alias to modify
	 * @param valueAlias Set this text to get the value to be prepended from an existing alias
	 * @param prepend The text to prepend the aliased value with
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by prepending it with( alias)? \"(.*?)\"$")
	public void prependAlias(final String alias, final String valueAlias, final String prepend) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		final String prependValue = StringUtils.isNotBlank(valueAlias)
			? State.getFeatureStateForThread().getDataSet().get(prepend)
			: prepend;

		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(alias, prependValue + value);
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Modify an aliased value by appending it with a string.
	 *
	 * @param alias The alias to modify
	 * @param valueAlias Set this text to get the value to be appended from an existing alias
	 * @param append The text to append the aliased value with
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by appending it with( alias)? \"(.*?)\"$")
	public void appendAlias(final String alias, final String valueAlias, final String append) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		final String appendValue = StringUtils.isNotBlank(valueAlias)
			? State.getFeatureStateForThread().getDataSet().get(append)
			: append;

		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(alias, value + appendValue);
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Trims the string referenced by the alias
	 * @param alias The text to append the aliased value with
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by trimming it$")
	public void trimAlias(final String alias) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		final String trimmedValue = value.trim();

		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(alias, trimmedValue);
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Copy an alias
	 *
	 * @param source The source alias
	 * @param destination The destination alias
	 */
	@Then("^I copy(?: the)? alias \"(.*?)\" to(?: the)? alias \"(.*?)\"$")
	public void copyAlias(final String source, final String destination) {
		final String value = State.getFeatureStateForThread().getDataSet().get(source);

		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(destination, value);
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Save the current date and time to an aliased value.
	 * @param format The format of the date: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
	 * @param offsetAlias include the word alias to get the value of the offset from an aliased value
	 * @param offsetAmount The optional amount to offset todays date by e.g. "1 day" or "2 weeks"
	 * @param alias The alias to save the date into
	 */
	@When("^I save the current date(?: offset by( alias)? \"(.*?)\")? with the format \"(.*?)\" to(?: the)? alias \"(.*?)\"")
	public void saveDateToAlias(
		final String offsetAlias,
		final String offsetAmount,
		final String format,
		final String alias) {

		final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);
		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();

		LocalDateTime date = LocalDateTime.now();

		if (StringUtils.isNotBlank(offsetAmount)) {
			final String fixedOffsetAmount = AUTO_ALIAS_UTILS.getValue(
				offsetAmount,
				StringUtils.isNotBlank(offsetAlias),
				State.getFeatureStateForThread());

			if (!fixedOffsetAmount.matches("^-?\\d+ \\w+$")) {
				throw new InvalidInputException(
					fixedOffsetAmount + " needs to match the format \"^-?\\d+ \\w+$\"");
			}

			final String[] offsetRaw = fixedOffsetAmount.split(" ");
			final int offset = Integer.parseInt(offsetRaw[0]);
			final ChronoUnit chronoUnit = CHRONO_CONVERTER_UTILS.fromString(offsetRaw[1]);
			date = date.plus(offset, chronoUnit);
		}

		dataset.put(alias, dateFormatter.format(date));
		State.getFeatureStateForThread().setDataSet(dataset);
	}

}
