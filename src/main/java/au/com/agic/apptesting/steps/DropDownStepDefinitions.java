package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkState;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import au.com.agic.apptesting.utils.BrowserInteropUtils;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.SystemPropertyUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cucumber.api.java.en.When;

/**
 * Gherkin steps for working with drop down lists.
 *
 * These steps have Atom snipptets that start with the prefix "select".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class DropDownStepDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(DropDownStepDefinitions.class);
	@Autowired
	private GetBy GET_BY;
	@Autowired
	private AutoAliasUtils AUTO_ALIAS_UTILS;
	@Autowired
	private SimpleWebElementInteraction SIMPLE_WEB_ELEMENT_INTERACTION;
	@Autowired
	private SleepUtils SLEEP_UTILS;
	@Autowired
	private SystemPropertyUtils SYSTEM_PROPERTY_UTILS;
	@Autowired
	private BrowserInteropUtils BROWSER_INTEROP_UTILS;

	/**
	 * Select an item from a drop down list using simple selection
	 *
	 * @param itemAlias     If this word is found in the step, it means the itemName is found from the data
	 *                      set.
	 * @param itemName     The index of the item to select
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I select( alias)? \"([^\"]*)\" from (?:a|the) drop down list found by( alias)? "
		+ "\"([^\"]*)\"( if it exists)?$")
	public void selectSimpleDropDownListItemStep(
		final String itemAlias,
		final String itemName,
		final String alias,
		final String selectorValue,
		final String exists) {

		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final String selection = AUTO_ALIAS_UTILS.getValue(
				itemName, StringUtils.isNotBlank(itemAlias), State.getFeatureStateForThread());

			checkState(selection != null, "the aliased item name does not exist");

			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			BROWSER_INTEROP_UTILS.selectFromDropDownList(webDriver, element, selection);

			SLEEP_UTILS.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Select an item from a drop down list
	 *
	 * @param itemAlias     If this word is found in the step, it means the itemName is found from the data
	 *                      set.
	 * @param itemName     The index of the item to select
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I select( alias)? \"([^\"]*)\" from (?:a|the) drop down list with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\"( if it exists)?$")
	public void selectDropDownListItemStep(
		final String itemAlias,
		final String itemName,
		final String selector,
		final String alias,
		final String selectorValue,
		final String exists) {

		try {
			final String selection = AUTO_ALIAS_UTILS.getValue(
				itemName, StringUtils.isNotBlank(itemAlias), State.getFeatureStateForThread());

			checkState(selection != null, "the aliased item name does not exist");

			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

			BROWSER_INTEROP_UTILS.selectFromDropDownList(webDriver, element, selection);

			SLEEP_UTILS.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Select an item index from a drop down list using simple selection
	 *
	 * @param itemAlias     If this word is found in the step, it means the itemName is found from the data
	 *                      set.
	 * @param itemIndex     The index of the item to select
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I select option(?: number)?( alias)? \"(\\d+)\" from (?:a|the) drop down list found by"
		+ "( alias)? \"([^\"]*)\"( if it exists)?$")
	public void selectSimpleDropDownListIndexStep(
		final String itemAlias,
		final String itemIndex,
		final String alias,
		final String selectorValue,
		final String exists) {
		try {
			final String selection = AUTO_ALIAS_UTILS.getValue(
				itemIndex, StringUtils.isNotBlank(itemAlias), State.getFeatureStateForThread());

			checkState(selection != null, "the aliased item index does not exist");

			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			final Select select = new Select(element);
			select.selectByIndex(Integer.parseInt(selection));
			SLEEP_UTILS.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Select an item index from a drop down list
	 *
	 * @param itemAlias     If this word is found in the step, it means the itemName is found from the data
	 *                      set.
	 * @param itemIndex     The index of the item to select
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I select option(?: number)?( alias)? \"([^\"]*)\" from (?:a|the) drop down list with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\"( if it exists)?$")
	public void selectDropDownListIndexStep(
		final String itemAlias,
		final String itemIndex,
		final String selector,
		final String alias,
		final String selectorValue,
		final String exists) {
		try {
			final String selection = AUTO_ALIAS_UTILS.getValue(
				itemIndex, StringUtils.isNotBlank(itemAlias), State.getFeatureStateForThread());

			checkState(selection != null, "the aliased item index does not exist");

			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

			final Select select = new Select(element);
			select.selectByIndex(Integer.parseInt(selection));
			SLEEP_UTILS.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	// </editor-fold>
}
