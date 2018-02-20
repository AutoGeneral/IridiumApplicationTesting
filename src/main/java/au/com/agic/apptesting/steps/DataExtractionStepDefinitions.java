package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.WebElementException;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Gherkin steps used to extract data from the web page.
 *
 * These steps have Atom snipptets that start with the prefix "save".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class DataExtractionStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataExtractionStepDefinitions.class);
	@Autowired
	private GetBy getBy;
	@Autowired
	private SimpleWebElementInteraction simpleWebElementInteraction;

	/**
	 * Saves the text value of an element against an alias using simple selection. Retrieves the "value"
	 * attribute content
	 *
	 * @param alias            If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue    The value used in conjunction with the selector to match the element. If alias
	 *                         was set, ' this value is found from the data set. Otherwise it is a literal
	 *                         value.
	 * @param destinationAlias The name of the alias to save the text content against
	 * @param exists           If this text is set, an error that would be thrown because the element was not
	 *                         found is ignored. Essentially setting this text makes this an optional
	 *                         statement.
	 */
	@When("^I save the value of (?:a|an|the)(?: element found by)?( alias)? "
		+ "\"([^\"]*)\"(?: \\w+)*? to the alias \"([^\"]*)\"( if it exists)?")
	public void saveSimpleValueAttribute(
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {

		saveSimpleAttributeContent("value", alias, selectorValue, destinationAlias, exists);
	}

	/**
	 * Saves the text value of an element against an alias. Retrieves the "value" attribute content
	 *
	 * @param selector         Either ID, class, xpath, name or css selector
	 * @param alias            If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue    The value used in conjunction with the selector to match the element. If alias
	 *                         was set, ' this value is found from the data set. Otherwise it is a literal
	 *                         value.
	 * @param destinationAlias The name of the alias to save the text content against
	 * @param exists           If this text is set, an error that would be thrown because the element was not
	 *                         found is ignored. Essentially setting this text makes this an optional
	 *                         statement.
	 */
	@When("^I save the value of (?:a|an|the) element with (?:a|an|the) (ID|class|xpath|name|css selector)( alias)? "
		+ "of \"([^\"]*)\" to the alias \"([^\"]*)\"( if it exists)?")
	public void saveValueAttribute(
		final String selector,
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {
		saveAttributeContent("value", selector, alias, selectorValue, destinationAlias, exists);
	}

	/**
	 * Saves the text value of an element attribute against an alias
	 *
	 * @param attribute        The name of the attribute to select
	 * @param alias            If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue    The value used in conjunction with the selector to match the element. If alias
	 *                         was set, ' this value is found from the data set. Otherwise it is a literal
	 *                         value.
	 * @param destinationAlias The name of the alias to save the text content against
	 * @param exists           If this text is set, an error that would be thrown because the element was not
	 *                         found is ignored. Essentially setting this text makes this an optional
	 *                         statement.
	 */
	@When("^I save the attribute content of \"([^\"]*)\" from (?:a|an|the)(?: element found by)?( alias)? \"([^\"]*)\"(?: \\w+)*? "
		+ "to the alias \"([^\"]*)\"( if it exists)?")
	public void saveSimpleAttributeContent(
		final String attribute,
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {
		try {
			final WebElement element = simpleWebElementInteraction.getVisibleElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			final Map<String, String> dataSet = State.getFeatureStateForThread().getDataSet();
			final String value = element.getAttribute(attribute).trim();
			dataSet.put(destinationAlias, value);

			State.getFeatureStateForThread().setDataSet(dataSet);
		} catch (final WebElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Saves the text value of an element attribute against an alias
	 *
	 * @param attribute        The name of the attribute to select
	 * @param selector         Either ID, class, xpath, name or css selector
	 * @param alias            If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue    The value used in conjunction with the selector to match the element. If alias
	 *                         was set, ' this value is found from the data set. Otherwise it is a literal
	 *                         value.
	 * @param destinationAlias The name of the alias to save the text content against
	 * @param exists           If this text is set, an error that would be thrown because the element was not
	 *                         found is ignored. Essentially setting this text makes this an optional
	 *                         statement.
	 */
	@When("^I save the attribute content of \"([^\"]*)\" from (?:a|an|the) element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\" to the alias "
		+ "\"([^\"]*)\"( if it exists)?")
	public void saveAttributeContent(
		final String attribute,
		final String selector,
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final By by = getBy.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));

			final Map<String, String> dataSet = State.getFeatureStateForThread().getDataSet();
			dataSet.put(destinationAlias, element.getAttribute(attribute).trim());
			State.getFeatureStateForThread().setDataSet(dataSet);
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Saves the text content of an element against an alias
	 *
	 * @param alias            If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue    The value used in conjunction with the selector to match the element. If alias
	 *                         was set, ' this value is found from the data set. Otherwise it is a literal
	 *                         value.
	 * @param destinationAlias The name of the alias to save the text content against
	 * @param exists           If this text is set, an error that would be thrown because the element was not
	 *                         found is ignored. Essentially setting this text makes this an optional
	 *                         statement.
	 */
	@When("^I save the text content of (?:a|an|the)(?: element found by)?( alias)? \"([^\"]*)\"(?: \\w+)*? to the alias "
		+ "\"([^\"]*)\"( if it exists)?")
	public void saveSimpleTextContent(
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {
		try {
			final WebElement element = simpleWebElementInteraction.getVisibleElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			final Map<String, String> dataSet = State.getFeatureStateForThread().getDataSet();
			dataSet.put(destinationAlias, element.getText().trim());
			State.getFeatureStateForThread().setDataSet(dataSet);
		} catch (final WebElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Saves the text content of an element against an alias
	 *
	 * @param selector         Either ID, class, xpath, name or css selector
	 * @param alias            If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue    The value used in conjunction with the selector to match the element. If alias
	 *                         was set, ' this value is found from the data set. Otherwise it is a literal
	 *                         value.
	 * @param destinationAlias The name of the alias to save the text content against
	 * @param exists           If this text is set, an error that would be thrown because the element was not
	 *                         found is ignored. Essentially setting this text makes this an optional
	 *                         statement.
	 */
	@When("^I save the text content of (?:a|an|the) element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\" to the alias "
		+ "\"([^\"]*)\"( if it exists)?")
	public void saveTextContent(
		final String selector,
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final By by = getBy.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));

			final Map<String, String> dataSet = State.getFeatureStateForThread().getDataSet();
			dataSet.put(destinationAlias, element.getText().trim());
			State.getFeatureStateForThread().setDataSet(dataSet);
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Saves the text content of an element against an alias using simple selection. This version extracts
	 * the value using javascript, which means it can return content when the method above does not.
	 *
	 * @param alias            If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue    The value used in conjunction with the selector to match the element. If alias
	 *                         was set, ' this value is found from the data set. Otherwise it is a literal
	 *                         value.
	 * @param destinationAlias The name of the alias to save the text content against
	 * @param exists           If this text is set, an error that would be thrown because the element was not
	 *                         found is ignored. Essentially setting this text makes this an optional
	 *                         statement.
	 */
	@When("^I save the text content of (?:a|an|the) hidden(?: element found by)?( alias)? \"([^\"]*)\""
		+ "(?: \\w+)*? to the alias \"([^\"]*)\"( if it exists)?")
	public void saveSimpleHiddenTextContent(
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {
		try {
			final WebElement element = simpleWebElementInteraction.getPresenceElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;
			final String text = js.executeScript(
				"return arguments[0].textContent.trim();",
				element).toString();

			final Map<String, String> dataSet = State.getFeatureStateForThread().getDataSet();
			dataSet.put(destinationAlias, text.trim());
			State.getFeatureStateForThread().setDataSet(dataSet);
		} catch (final WebElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Saves the text content of an element against an alias. This version extracts the value using
	 * javascript, which means it can return content when the method above does not.
	 *
	 * @param selector         Either ID, class, xpath, name or css selector
	 * @param alias            If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue    The value used in conjunction with the selector to match the element. If alias
	 *                         was set, ' this value is found from the data set. Otherwise it is a literal
	 *                         value.
	 * @param destinationAlias The name of the alias to save the text content against
	 * @param exists           If this text is set, an error that would be thrown because the element was not
	 *                         found is ignored. Essentially setting this text makes this an optional
	 *                         statement.
	 */
	@When("^I save the text content of (?:a|an|the) hidden element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\" to the alias \"([^\"]*)\""
		+ "( if it exists)?")
	public void saveHiddenTextContent(
		final String selector,
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final By by = getBy.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));

			final JavascriptExecutor js = (JavascriptExecutor) webDriver;
			final String text = js.executeScript(
				"return arguments[0].textContent.trim();",
				element).toString();

			final Map<String, String> dataSet = State.getFeatureStateForThread().getDataSet();
			dataSet.put(destinationAlias, text.trim());
			State.getFeatureStateForThread().setDataSet(dataSet);
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	@When("^I save the ((?:content)|(?:value)) of the first selected option from (?:a|an|the) "
		+ "drop down list with (?:a|an|the) (ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\" "
		+ "to the alias \"([^\"]*)\"( if it exists)?")
	public void saveSelectedTextContent(
		final String valueOrContent,
		final String selector,
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {

		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();

			final By by = getBy.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));

			final Select select = new Select(element);

			final String extractedValue = "content".equalsIgnoreCase(valueOrContent)
				? select.getFirstSelectedOption().getText()
				: select.getFirstSelectedOption().getAttribute("value");

			final Map<String, String> dataSet = State.getFeatureStateForThread().getDataSet();
			dataSet.put(destinationAlias, extractedValue);
			State.getFeatureStateForThread().setDataSet(dataSet);

		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Saves the text content the first selected element in a drop down list
	 *
	 * @param valueOrContent   Defines whether or not we are saving the text content or the value attribute
	 *                         of the selected option
	 * @param alias            If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue    The value used in conjunction with the selector to match the element. If alias
	 *                         was set, ' this value is found from the data set. Otherwise it is a literal
	 *                         value.
	 * @param destinationAlias The name of the alias to save the text content against
	 * @param exists           If this text is set, an error that would be thrown because the element was not
	 *                         found is ignored. Essentially setting this text makes this an optional
	 *                         statement.
	 */
	@When("^I save the ((?:content)|(?:value)) of the first selected option from (?:a|an|the)(?: drop down list found by)?"
		+ "( alias)? \"([^\"]*)\"(?: \\w+)*? to the alias \"([^\"]*)\"( if it exists)?")
	public void saveSelectedTextContent(
		final String valueOrContent,
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {
		try {
			final WebElement element = simpleWebElementInteraction.getVisibleElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			final Select select = new Select(element);

			final String extractedValue = "content".equalsIgnoreCase(valueOrContent)
				? select.getFirstSelectedOption().getText()
				: select.getFirstSelectedOption().getAttribute("value");

			final Map<String, String> dataSet = State.getFeatureStateForThread().getDataSet();
			dataSet.put(destinationAlias, extractedValue);
			State.getFeatureStateForThread().setDataSet(dataSet);
		} catch (final WebElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}
}
