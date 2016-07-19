package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

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
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

import cucumber.api.java.en.Then;

/**
 * Contains Gherkin step definitions for checking the current state of the web page
 */
public class ValidationStepDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationStepDefinitions.class);
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();
	private static final GetBy GET_BY = new GetByImpl();
	private static final SimpleWebElementInteraction SIMPLE_WEB_ELEMENT_INTERACTION =
		new SimpleWebElementInteractionImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread(
			Thread.currentThread().getName());

	/**
	 * Verify the title in the browser
	 *
	 * @param browserTitle Defines what the browser title should be
	 */
	@Then("^the browser title should be \"([^\"]*)\"$")
	public void checkBrowserTitleStep(final String browserTitle) {
		Assert.assertEquals(browserTitle, threadDetails.getWebDriver().getTitle());
		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
	}

	/**
	 * Verify that an element has the specified class using simple selection
	 *
	 * @param selectorAlias If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param classAlias    If this word is found in the step, it means the classValue is found from the data
	 *                      set.
	 * @param classValue    The class value
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@Then("^the element found by( alias)? \"([^\"]*)\" should have a "
		+ "class( alias)? of \"([^\"]*)\"( if it exists)?$")
	public void checkElementClassStep(
		final String selectorAlias,
		final String selectorValue,
		final String classAlias,
		final String classValue,
		final String exists) throws ExecutionException, InterruptedException {
		try {
			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(selectorAlias),
				selectorValue,
				threadDetails).get();

			final String className = StringUtils.isNotBlank(classAlias)
				? threadDetails.getDataSet().get(classValue) : classValue;

			checkState(className != null, "the aliased class name does not exist");

			final Iterable<String> split = Splitter.on(' ')
				.trimResults()
				.omitEmptyStrings()
				.split(element.getAttribute("class"));

			Assert.assertTrue(Iterables.contains(split, className));
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Verify that an element has the specified class
	 *
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param selectorAlias If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param classAlias    If this word is found in the step, it means the classValue is found from the data
	 *                      set.
	 * @param classValue    The class value
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@Then("^the element with the (ID|class|xpath|name|css selector)( alias)? \"([^\"]*)\" should have a "
		+ "class( alias)? of \"([^\"]*)\"( if it exists)?$")
	public void checkElementClassStep(
		final String selector,
		final String selectorAlias,
		final String selectorValue,
		final String classAlias,
		final String classValue,
		final String exists) {
		try {
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(selectorAlias),
				selectorValue,
				threadDetails);
			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

			final String className = " alias".equals(classAlias)
				? threadDetails.getDataSet().get(classValue) : classValue;

			checkState(className != null, "the aliased class name does not exist");

			final Iterable<String> split = Splitter.on(' ')
				.trimResults()
				.omitEmptyStrings()
				.split(element.getAttribute("class"));

			Assert.assertTrue(Iterables.contains(split, className));
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}
}
