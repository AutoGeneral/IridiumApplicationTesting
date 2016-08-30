package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.impl.GetByImpl;
import au.com.agic.apptesting.utils.impl.SimpleWebElementInteractionImpl;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import cucumber.api.java.en.When;

/**
 * Gherkin steps used to extract data from the web page.
 *
 * These steps have Atom snipptets that start with the prefix "save".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class DataExtractionStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataExtractionStepDefinitions.class);
	private static final GetBy GET_BY = new GetByImpl();
	private static final SimpleWebElementInteraction SIMPLE_WEB_ELEMENT_INTERACTION =
		new SimpleWebElementInteractionImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final FeatureState featureState =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

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
	@When("^I save the value of (?:a|an|the) element found by( alias)? "
		+ "\"([^\"]*)\" to the alias \"([^\"]*)\"( if it exists)?")
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
	@When("^I save the attribute content of \"([^\"]*)\" from (?:a|an|the) element found by( alias)? \"([^\"]*)\" "
		+ "to the alias \"([^\"]*)\"( if it exists)?")
	public void saveSimpleAttributeContent(
		final String attribute,
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {
		try {
			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getVisibleElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);

			final Map<String, String> dataSet = featureState.getDataSet();
			dataSet.put(destinationAlias, element.getAttribute(attribute).trim());
			featureState.setDataSet(dataSet);
		} catch (final TimeoutException ex) {
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
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);
			final WebDriverWait wait = new WebDriverWait(webDriver, featureState.getDefaultWait());
			final WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));

			final Map<String, String> dataSet = featureState.getDataSet();
			dataSet.put(destinationAlias, element.getAttribute(attribute).trim());
			featureState.setDataSet(dataSet);
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
	@When("^I save the text content of (?:a|an|the) element found by( alias)? \"([^\"]*)\" to the alias "
		+ "\"([^\"]*)\"( if it exists)?")
	public void saveSimpleTextContent(
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {
		try {
			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getVisibleElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);

			final Map<String, String> dataSet = featureState.getDataSet();
			dataSet.put(destinationAlias, element.getText().trim());
			featureState.setDataSet(dataSet);
		} catch (final TimeoutException ex) {
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
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);
			final WebDriverWait wait = new WebDriverWait(webDriver, featureState.getDefaultWait());
			final WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));

			final Map<String, String> dataSet = featureState.getDataSet();
			dataSet.put(destinationAlias, element.getText().trim());
			featureState.setDataSet(dataSet);
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
	@When("^I save the text content of (?:a|an|the) hidden element found by( alias)? \"([^\"]*)\""
		+ " to the alias \"([^\"]*)\"( if it exists)?")
	public void saveSimpleHiddenTextContent(
		final String alias,
		final String selectorValue,
		final String destinationAlias,
		final String exists) {
		try {
			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getPresenceElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;
			final String text = js.executeScript(
				"return arguments[0].textContent.trim();",
				element).toString();

			final Map<String, String> dataSet = featureState.getDataSet();
			dataSet.put(destinationAlias, text.trim());
			featureState.setDataSet(dataSet);
		} catch (final TimeoutException ex) {
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
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);
			final WebDriverWait wait = new WebDriverWait(webDriver, featureState.getDefaultWait());
			final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));

			final JavascriptExecutor js = (JavascriptExecutor) webDriver;
			final String text = js.executeScript(
				"return arguments[0].textContent.trim();",
				element).toString();

			final Map<String, String> dataSet = featureState.getDataSet();
			dataSet.put(destinationAlias, text.trim());
			featureState.setDataSet(dataSet);
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	// </editor-fold>
}
