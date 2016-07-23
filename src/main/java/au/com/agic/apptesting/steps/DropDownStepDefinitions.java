package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkState;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.ThreadDetails;
import au.com.agic.apptesting.utils.impl.GetByImpl;
import au.com.agic.apptesting.utils.impl.SimpleWebElementInteractionImpl;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

import cucumber.api.java.en.When;

/**
 * Gherkin steps for working with drop down lists.
 *
 * These steps have Atom snipptets that start with the prefix "select".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class DropDownStepDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(DropDownStepDefinitions.class);
	private static final GetBy GET_BY = new GetByImpl();
	private static final SimpleWebElementInteraction SIMPLE_WEB_ELEMENT_INTERACTION =
		new SimpleWebElementInteractionImpl();
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

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
		final String exists) throws ExecutionException, InterruptedException {

		try {
			final String selection = " alias".equals(itemAlias)
				? threadDetails.getDataSet().get(itemName) : itemName;

			checkState(selection != null, "the aliased item name does not exist");

			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails).get();

			final Select select = new Select(element);
			select.selectByVisibleText(selection);

			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
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
			final String selection = " alias".equals(itemAlias)
				? threadDetails.getDataSet().get(itemName) : itemName;

			checkState(selection != null, "the aliased item name does not exist");

			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails);
			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

			final Select select = new Select(element);
			select.selectByVisibleText(selection);

			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
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
	@When("^I select option(?: number)?( alias)? \"([^\"]*)\" from (?:a|the) drop down list found by"
		+ "( alias)? of \"([^\"]*)\"( if it exists)?$")
	public void selectSimpleDropDownListIndexStep(
		final String itemAlias,
		final String itemIndex,
		final String alias,
		final String selectorValue,
		final String exists) throws ExecutionException, InterruptedException {
		try {
			final String selection = " alias".equals(itemAlias)
				? threadDetails.getDataSet().get(itemIndex) : itemIndex;

			checkState(selection != null, "the aliased item index does not exist");

			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails).get();

			final Select select = new Select(element);
			select.selectByIndex(Integer.parseInt(selection));
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
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
			final String selection = " alias".equals(itemAlias)
				? threadDetails.getDataSet().get(itemIndex) : itemIndex;

			checkState(selection != null, "the aliased item index does not exist");

			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails);
			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

			final Select select = new Select(element);
			select.selectByIndex(Integer.parseInt(selection));
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	// </editor-fold>
}
