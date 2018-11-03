package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.WebElementException;
import au.com.agic.apptesting.utils.*;
import au.com.agic.apptesting.utils.impl.MouseMovementUtilsImpl;
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

import static com.google.common.base.Preconditions.checkState;

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
	private GetBy getBy;
	@Autowired
	private AutoAliasUtils autoAliasUtils;
	@Autowired
	private SimpleWebElementInteraction simpleWebElementInteraction;
	@Autowired
	private SleepUtils sleepUtils;
	@Autowired
	private SystemPropertyUtils systemPropertyUtils;
	@Autowired
	private BrowserInteropUtils browserInteropUtils;
	@Autowired
	private MouseMovementUtilsImpl mouseMovementUtils;

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
	@When("^I select( alias)? \"([^\"]*)\" from (?:a|the)(?: drop down list found by)?( alias)? "
		+ "\"([^\"]*)\"(?: \\w+)*?( if it exists)?$")
	public void selectSimpleDropDownListItemStep(
		final String itemAlias,
		final String itemName,
		final String alias,
		final String selectorValue,
		final String exists) {

		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final String selection = autoAliasUtils.getValue(
				itemName, StringUtils.isNotBlank(itemAlias), State.getFeatureStateForThread());

			checkState(selection != null, "the aliased item name does not exist");

			final WebElement element = simpleWebElementInteraction.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			mouseMovementUtils.mouseGlide(
				webDriver,
				(JavascriptExecutor) webDriver,
				element,
				Constants.MOUSE_MOVE_TIME,
				Constants.MOUSE_MOVE_STEPS);

			browserInteropUtils.selectFromDropDownList(webDriver, element, selection);

			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final WebElementException ex) {
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
			final String selection = autoAliasUtils.getValue(
				itemName, StringUtils.isNotBlank(itemAlias), State.getFeatureStateForThread());

			checkState(selection != null, "the aliased item name does not exist");

			final By by = getBy.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

			mouseMovementUtils.mouseGlide(
				webDriver,
				(JavascriptExecutor) webDriver,
				element,
				Constants.MOUSE_MOVE_TIME,
				Constants.MOUSE_MOVE_STEPS);

			browserInteropUtils.selectFromDropDownList(webDriver, element, selection);

			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
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
	@When("^I select option(?: number)?( alias)? \"([^\"]*)\" from (?:a|the)(?: drop down list found by)?"
		+ "( alias)? \"([^\"]*)\"(?: \\w+)*?( if it exists)?$")
	public void selectSimpleDropDownListIndexStep(
		final String itemAlias,
		final String itemIndex,
		final String alias,
		final String selectorValue,
		final String exists) {
		try {
			final String selection = autoAliasUtils.getValue(
				itemIndex, StringUtils.isNotBlank(itemAlias), State.getFeatureStateForThread());

			checkState(selection != null, "the aliased item index does not exist");

			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();

			final WebElement element = simpleWebElementInteraction.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			mouseMovementUtils.mouseGlide(
				webDriver,
				(JavascriptExecutor) webDriver,
				element,
				Constants.MOUSE_MOVE_TIME,
				Constants.MOUSE_MOVE_STEPS);

			final Select select = new Select(element);
			select.selectByIndex(Integer.parseInt(selection));
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final WebElementException ex) {
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
			final String selection = autoAliasUtils.getValue(
				itemIndex, StringUtils.isNotBlank(itemAlias), State.getFeatureStateForThread());

			checkState(selection != null, "the aliased item index does not exist");

			final By by = getBy.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

			mouseMovementUtils.mouseGlide(
				webDriver,
				(JavascriptExecutor) webDriver,
				element,
				Constants.MOUSE_MOVE_TIME,
				Constants.MOUSE_MOVE_STEPS);

			final Select select = new Select(element);
			select.selectByIndex(Integer.parseInt(selection));
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	// </editor-fold>
}
