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

import java.math.BigDecimal;
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
	private SleepUtils sleepUtils;
	@Autowired
	private ChronoConverterUtils chronoConverterUtils;
	@Autowired
	private AutoAliasUtils autoAliasUtils;

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
	 * Transforms the alias to an upper case value
	 *
	 * @param alias The text to append the aliased value with
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by converting it to upper case$")
	public void uppercase(final String alias) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		final String uppercase = value.toUpperCase();

		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(alias, uppercase);
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Transforms the alias to an lower case value
	 *
	 * @param alias The text to append the aliased value with
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by converting it to lower case")
	public void lowercase(final String alias) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		final String uppercase = value.toLowerCase();

		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(alias, uppercase);
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
			final String fixedOffsetAmount = autoAliasUtils.getValue(
				offsetAmount,
				StringUtils.isNotBlank(offsetAlias),
				State.getFeatureStateForThread());

			if (!fixedOffsetAmount.matches("^-?\\d+ \\w+$")) {
				throw new InvalidInputException(
					fixedOffsetAmount + " needs to match the format \"^-?\\d+ \\w+$\"");
			}

			final String[] offsetRaw = fixedOffsetAmount.split(" ");
			final int offset = Integer.parseInt(offsetRaw[0]);
			final ChronoUnit chronoUnit = chronoConverterUtils.fromString(offsetRaw[1]);
			date = date.plus(offset, chronoUnit);
		}

		dataset.put(alias, dateFormatter.format(date));
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Subtract two aliases
	 *
	 * @param alias The alias that holds the first value
	 * @param subtractAlias include the word alias to get the value of the subtraction from an aliased value
	 * @param subtractAlias The alias that holds the value to subtract from the first value
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by subtracting( alias)? \"(.*?)\" from it")
	public void subtract(final String alias, final String subtractAlias, final String subtract) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);

		final String subtractValue = autoAliasUtils.getValue(
			subtract,
			StringUtils.isNotBlank(subtractAlias),
			State.getFeatureStateForThread());

		final BigDecimal result = new BigDecimal(value).subtract(new BigDecimal(subtractValue));

		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(alias, result.toString());
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Add two aliases
	 *
	 * @param alias The alias that holds the first value
	 * @param addAlias include the word alias to get the value of the addition from an aliased value
	 * @param add The alias that holds the value to add to the first value
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by adding( alias)? \"(.*?)\" to it")
	public void add(final String alias, final String addAlias, final String add) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);

		final String addValue = autoAliasUtils.getValue(
			add,
			StringUtils.isNotBlank(addAlias),
			State.getFeatureStateForThread());

		final BigDecimal result = new BigDecimal(value).add(new BigDecimal(addValue));

		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(alias, result.toString());
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Multiply two aliases
	 *
	 * @param alias The alias that holds the first value
	 * @param multiplyAlias include the word alias to get the value of the multiplier from an aliased value
	 * @param multiply The alias that holds the value to multiply with the first value
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by multiplying( alias)? \"(.*?)\" with it")
	public void multiply(final String alias, final String multiplyAlias, final String multiply) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);

		final String multiplyValue = autoAliasUtils.getValue(
			multiply,
			StringUtils.isNotBlank(multiplyAlias),
			State.getFeatureStateForThread());

		final BigDecimal result = new BigDecimal(value).multiply(new BigDecimal(multiplyValue));

		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(alias, result.toString());
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Divide two aliases
	 *
	 * @param alias The alias that holds the first value
	 * @param divideAlias include the word alias to get the value of the divisor from an aliased value
	 * @param divide The alias that holds the value to divide with the first value
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by dividing( alias)? \"(.*?)\" into it")
	public void divide(final String alias, final String divideAlias, final String divide) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);

		final String divideValue = autoAliasUtils.getValue(
			divide,
			StringUtils.isNotBlank(divideAlias),
			State.getFeatureStateForThread());

		final BigDecimal result = new BigDecimal(value).divide(new BigDecimal(divideValue));

		final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
		dataset.put(alias, result.toString());
		State.getFeatureStateForThread().setDataSet(dataset);
	}

	/**
	 * Set an alias to the maximum value
	 *
	 * @param alias The alias that holds the first value
	 * @param maxValueAlias include the word alias to get the value of the max value from an aliased value
	 * @param maxValue The value
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by setting it to( alias)? \"(.*?)\" if the value it holds is smaller")
	public void max(final String alias, final String maxValueAlias, final String maxValue) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);

		final String fixedMaxValue = autoAliasUtils.getValue(
			maxValue,
			StringUtils.isNotBlank(maxValueAlias),
			State.getFeatureStateForThread());

		if (Double.parseDouble(value) < Double.parseDouble(fixedMaxValue)) {
			final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
			dataset.put(alias, fixedMaxValue);
			State.getFeatureStateForThread().setDataSet(dataset);
		}
	}

	/**
	 * Set an alias to the minimum value
	 *
	 * @param alias The alias that holds the first value
	 * @param minValueAlias include the word alias to get the value of the max value from an aliased value
	 * @param minValue The value
	 */
	@Then("^I modify(?: the)? alias \"(.*?)\" by setting it to( alias)? \"(.*?)\" if the value it holds is larger")
	public void min(final String alias, final String minValueAlias, final String minValue) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);

		final String fixedMinValue = autoAliasUtils.getValue(
			minValue,
			StringUtils.isNotBlank(minValueAlias),
			State.getFeatureStateForThread());

		if (Double.parseDouble(fixedMinValue) < Double.parseDouble(value)) {
			final Map<String, String> dataset = State.getFeatureStateForThread().getDataSet();
			dataset.put(alias, fixedMinValue);
			State.getFeatureStateForThread().setDataSet(dataset);
		}
	}

}
