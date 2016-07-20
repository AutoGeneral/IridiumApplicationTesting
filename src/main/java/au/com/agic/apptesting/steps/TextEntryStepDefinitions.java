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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import cucumber.api.java.en.When;

/**
 * Contains Gherkin steps for enterting text.
 *
 * These steps have Atom snipptets that start with the prefix "populate".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class TextEntryStepDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(TextEntryStepDefinitions.class);
	private static final GetBy GET_BY = new GetByImpl();
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();
	private static final SimpleWebElementInteraction SIMPLE_WEB_ELEMENT_INTERACTION =
		new SimpleWebElementInteractionImpl();

	/**
	 * How long to delay when entering each character into a text box
	 */
	private static final int KEY_STROKE_DELAY = 300;

	private static final Pattern BLANK_OR_MASKED_RE = Pattern.compile("^(_|\\s)+$");
	private static final Pattern SINGLE_QUOTE_RE = Pattern.compile("'");

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread(
			Thread.currentThread().getName());

	/**
	 * Clears the contents of an element using simple selection
	 *
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 */
	@When("^I clear (?:a|an|the) element found by( alias)? \"([^\"]*)\"")
	public void clearElement(
		final String alias,
		final String selectorValue) throws ExecutionException, InterruptedException {
		final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getPresenceElementFoundBy(
			StringUtils.isNotBlank(alias),
			selectorValue,
			threadDetails).get();
		element.clear();
		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
	}

	/**
	 * Clears the contents of an element
	 *
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 */
	@When("^I clear (?:a|an|the) element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\"")
	public void clearElement(final String selector, final String alias, final String selectorValue) {
		final By by = GET_BY.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, threadDetails);
		final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
		final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		element.clear();
		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
	}

	/**
	 * Clears the contents of a hidden element using simple selection
	 *
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 */
	@When("^I clear (?:a|an|the) hidden element found by( alias)? \"([^\"]*)\"")
	public void clearHiddenElement(
		final String alias,
		final String selectorValue) throws ExecutionException, InterruptedException {

		final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getPresenceElementFoundBy(
			StringUtils.isNotBlank(alias),
			selectorValue,
			threadDetails).get();

		final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();
		js.executeScript("arguments[0].value='';", element);
		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
	}

	/**
	 * Clears the contents of an element
	 *
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 */
	@When("^I clear (?:a|an|the) hidden element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\"")
	public void clearHiddenElement(final String selector, final String alias, final String selectorValue) {
		final By by = GET_BY.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, threadDetails);
		final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
		final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();
		js.executeScript("arguments[0].value='';", element);
		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
	}



	/**
	 * Populate an element with some text, and submits it.
	 *
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param contentAlias  If this word is found in the step, it means the content is found from the data
	 *                      set.
	 * @param content       The content to populate the element with. If contentAlias was set, this value is
	 *                      found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I populate (?:a|an|the) element found by( alias)? "
		+ "\"([^\"]*)\" with( alias)? \"([^\"]*)\" and submit( if it exists)?$")
	public void populateSimpleElementAndSubmitStep(
		final String alias,
		final String selectorValue,
		final String contentAlias,
		final String content,
		final String exists) throws ExecutionException, InterruptedException {
		try {
			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails).get();

			// Simulate key presses
			final String value = " alias".equals(contentAlias)
				? threadDetails.getDataSet().get(content) : content;

			checkState(value != null, "the aliased content value does not exist");

			for (final Character character : value.toCharArray()) {
				SLEEP_UTILS.sleep(KEY_STROKE_DELAY);
				element.sendKeys(character.toString());
			}

			SLEEP_UTILS.sleep(KEY_STROKE_DELAY);
			element.submit();
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Populate an element with some text, and submits it.
	 *
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param contentAlias  If this word is found in the step, it means the content is found from the data
	 *                      set.
	 * @param content       The content to populate the element with. If contentAlias was set, this value is
	 *                      found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I populate (?:a|an|the) element with (?:a|an|the) (ID|class|xpath|name|css selector)( alias)? "
		+ "of \"([^\"]*)\" with( alias)? \"([^\"]*)\" and submit( if it exists)?$")
	public void populateElementAndSubmitStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String contentAlias,
		final String content,
		final String exists) {
		try {
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails);
			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

			// Simulate key presses
			final String value = " alias".equals(contentAlias)
				? threadDetails.getDataSet().get(content) : content;

			checkState(value != null, "the aliased content value does not exist");

			for (final Character character : value.toCharArray()) {
				SLEEP_UTILS.sleep(KEY_STROKE_DELAY);
				element.sendKeys(character.toString());
			}

			SLEEP_UTILS.sleep(KEY_STROKE_DELAY);
			element.submit();
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Populate an element with some text with simple selectiom
	 *
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, ' this value is found from the data set. Otherwise it is a literal value.
	 * @param contentAlias  If this word is found in the step, it means the content is found from the data
	 *                      set.
	 * @param content       The content to populate the element with. If contentAlias was set, this value is
	 *                      found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 * @param empty         Skips the step if the element is not empty
	 * @param delay         An optional value that defines how long to wait before each simulated keypress.
	 *                      This is useful for setting a longer delay fields that perform ajax request in
	 *                      response to key pressed.
	 */
	@SuppressWarnings("checkstyle:parameternumber")
	@When("^I populate (?:a|an|the) element found by( alias)? "
		+ "\"([^\"]*)\" with( alias)? \"([^\"]*)\"( if it exists)?( if it is empty)?"
		+ "(?: with a keystroke delay of \"(\\d+)\" milliseconds)?$")
	public void populateElementStep(
		final String alias,
		final String selectorValue,
		final String contentAlias,
		final String content,
		final String exists,
		final String empty,
		final Integer delay) throws ExecutionException, InterruptedException {
		try {
			final Integer fixedDelay = delay == null ? KEY_STROKE_DELAY : delay;

			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails).get();

			/*
				See if the element is blank, or contains only underscores (as you might find in
				an empty phone number field for example
			 */

			final boolean processElement = !" if it is empty".equals(empty)
				|| StringUtils.isBlank(element.getAttribute("value"))
				|| BLANK_OR_MASKED_RE.matcher(element.getAttribute("value")).matches();

			if (processElement) {
				// Simulate key presses
				final String textValue = " alias".equals(contentAlias)
					? threadDetails.getDataSet().get(content) : content;

				checkState(textValue != null, "the aliased text value does not exist");

				for (final Character character : textValue.toCharArray()) {
					SLEEP_UTILS.sleep(fixedDelay);
					element.sendKeys(character.toString());
				}
				SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
			}
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Populate an element with some text
	 *
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, ' this value is found from the data set. Otherwise it is a literal value.
	 * @param contentAlias  If this word is found in the step, it means the content is found from the data
	 *                      set.
	 * @param content       The content to populate the element with. If contentAlias was set, this value is
	 *                      found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 * @param empty         Skips the step if the element is not empty
	 * @param delay         An optional value that defines how long to wait before each simulated keypress.
	 *                      This is useful for setting a longer delay fields that perform ajax request in
	 *                      response to key pressed.
	 */
	@SuppressWarnings("checkstyle:parameternumber")
	@When("^I populate (?:a|an|the) element with (?:a|an|the) (ID|class|xpath|name|css selector)( alias)? of "
		+ "\"([^\"]*)\" with( alias)? \"([^\"]*)\"( if it exists)?( if it is empty)?"
		+ "(?: with a keystroke delay of \"(\\d+)\" milliseconds)?$")
	public void populateElementStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String contentAlias,
		final String content,
		final String exists,
		final String empty,
		final Integer delay) {
		try {
			final Integer fixedDelay = delay == null ? KEY_STROKE_DELAY : delay;

			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails);
			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

			/*
				See if the element is blank, or contains only underscores (as you might find in
				an empty phone number field for example
			 */

			final boolean processElement = !" if it is empty".equals(empty)
				|| StringUtils.isBlank(element.getAttribute("value"))
				|| BLANK_OR_MASKED_RE.matcher(element.getAttribute("value")).matches();

			if (processElement) {
				// Simulate key presses
				final String textValue = " alias".equals(contentAlias)
					? threadDetails.getDataSet().get(content) : content;

				checkState(textValue != null, "the aliased text value does not exist");

				for (final Character character : textValue.toCharArray()) {
					SLEEP_UTILS.sleep(fixedDelay);
					element.sendKeys(character.toString());
				}
				SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
			}
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Populates an element with a random number
	 *
	 * @param selector         Either ID, class, xpath, name or css selector
	 * @param alias            If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue    The value used in conjunction with the selector to match the element. If alias
	 *                         was set, this value is found from the data set. Otherwise it is a literal
	 *                         value.
	 * @param randomStartAlias If this word is found in the step, it means the randomStart is found from the
	 *                         data set.
	 * @param randomStart      The start of the range of random numbers to select from
	 * @param randomEndAlias   If this word is found in the step, it means the randomEnd is found from the
	 *                         data set.
	 * @param randomEnd        The end of the range of random numbers to select from
	 * @param exists           If this text is set, an error that would be thrown because the element was not
	 *                         found is ignored. Essentially setting this text makes this an optional
	 *                         statement.
	 */
	@SuppressWarnings("checkstyle:parameternumber")
	@When("^I populate (?:a|an|the) element found by( alias)? "
		+ "\"([^\"]*)\" with a random number between( alias)? \"([^\"]*)\" and( alias)? "
		+ "\"([^\"]*)\"( if it exists)?$")
	public void populateElementWithRandomNumberStep(
		final String alias,
		final String selectorValue,
		final String randomStartAlias,
		final String randomStart,
		final String randomEndAlias,
		final String randomEnd,
		final String exists) throws ExecutionException, InterruptedException {
		try {
			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails).get();

			final String startValue = " alias".equals(randomStartAlias)
				? threadDetails.getDataSet().get(randomStart) : randomStart;
			final String endValue = " alias".equals(randomEndAlias)
				? threadDetails.getDataSet().get(randomEnd) : randomEnd;

			checkState(startValue != null, "the aliased start value does not exist");
			checkState(endValue != null, "the aliased end value does not exist");

			final Integer int1 = Integer.parseInt(startValue);
			final Integer int2 = Integer.parseInt(endValue);
			final Integer random = SecureRandom.getInstance("SHA1PRNG")
				.nextInt(Math.abs(int2 - int1)) + Math.min(int1, int2);

			// Simulate key presses
			for (final Character character : random.toString().toCharArray()) {
				SLEEP_UTILS.sleep(KEY_STROKE_DELAY);
				element.sendKeys(character.toString());
			}
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		} catch (final NoSuchAlgorithmException ex) {
			/*
				This shouldn't happen
			 */
			LOGGER.error("Exception thrown when trying to create a SecureRandom instance", ex);
		}
	}

	/**
	 * Populates an element with a random number
	 *
	 * @param selector         Either ID, class, xpath, name or css selector
	 * @param alias            If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue    The value used in conjunction with the selector to match the element. If alias
	 *                         was set, this value is found from the data set. Otherwise it is a literal
	 *                         value.
	 * @param randomStartAlias If this word is found in the step, it means the randomStart is found from the
	 *                         data set.
	 * @param randomStart      The start of the range of random numbers to select from
	 * @param randomEndAlias   If this word is found in the step, it means the randomEnd is found from the
	 *                         data set.
	 * @param randomEnd        The end of the range of random numbers to select from
	 * @param exists           If this text is set, an error that would be thrown because the element was not
	 *                         found is ignored. Essentially setting this text makes this an optional
	 *                         statement.
	 */
	@SuppressWarnings("checkstyle:parameternumber")
	@When("^I populate (?:a|an|the) element with (?:a|an|the) (ID|class|xpath|name|css selector)( alias)? "
		+ "of \"([^\"]*)\" with a random number between( alias)? \"([^\"]*)\" and( alias)? "
		+ "\"([^\"]*)\"( if it exists)?$")
	public void populateElementWithRandomNumberStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String randomStartAlias,
		final String randomStart,
		final String randomEndAlias,
		final String randomEnd,
		final String exists) {
		try {
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails);

			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

			final String startValue = " alias".equals(randomStartAlias)
				? threadDetails.getDataSet().get(randomStart) : randomStart;
			final String endValue = " alias".equals(randomEndAlias)
				? threadDetails.getDataSet().get(randomEnd) : randomEnd;

			checkState(startValue != null, "the aliased start value does not exist");
			checkState(endValue != null, "the aliased end value does not exist");

			final Integer int1 = Integer.parseInt(startValue);
			final Integer int2 = Integer.parseInt(endValue);
			final Integer random = SecureRandom.getInstance("SHA1PRNG")
				.nextInt(Math.abs(int2 - int1)) + Math.min(int1, int2);

			// Simulate key presses
			for (final Character character : random.toString().toCharArray()) {
				SLEEP_UTILS.sleep(KEY_STROKE_DELAY);
				element.sendKeys(character.toString());
			}
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		} catch (final NoSuchAlgorithmException ex) {
			/*
				This shouldn't happen
			 */
			LOGGER.error("Exception thrown when trying to create a SecureRandom instance", ex);
		}
	}

	/**
	 * Populate an element with some text, and submits it.
	 *
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, ' this value is found from the data set. Otherwise it is a literal value.
	 * @param contentAlias  If this word is found in the step, it means the content is found from the data
	 *                      set.
	 * @param content       The content to populate the element with. If contentAlias was set, this value is
	 *                      found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I populate (?:a|an|the) hidden element found by( alias)? "
		+ "\"([^\"]*)\" with( alias)? \"([^\"]*)\"( if it exists)?$")
	public void populateHiddenElementAndSubmitStep(
		final String alias,
		final String selectorValue,
		final String contentAlias,
		final String content,
		final String exists) throws ExecutionException, InterruptedException {
		try {
			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getPresenceElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails).get();

			final String textValue = " alias".equals(contentAlias)
				? threadDetails.getDataSet().get(content) : content;

			checkState(textValue != null, "the aliased text value does not exist");

			final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();
			js.executeScript(
				"arguments[0].value = '"
					+ SINGLE_QUOTE_RE.matcher(textValue).replaceAll("\\'")
					+ "';", element);

			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Populate an element with some text, and submits it.
	 *
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, ' this value is found from the data set. Otherwise it is a literal value.
	 * @param contentAlias  If this word is found in the step, it means the content is found from the data
	 *                      set.
	 * @param content       The content to populate the element with. If contentAlias was set, this value is
	 *                      found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I populate (?:a|an|the) hidden element with (?:a|an|the) (ID|class|xpath|name|css selector)( alias)? "
		+ "of \"([^\"]*)\" with( alias)? \"([^\"]*)\"( if it exists)?$")
	public void populateHiddenElementAndSubmitStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String contentAlias,
		final String content,
		final String exists) {
		try {
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails);
			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));

			final String textValue = " alias".equals(contentAlias)
				? threadDetails.getDataSet().get(content) : content;

			checkState(textValue != null, "the aliased text value does not exist");

			final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();
			js.executeScript(
				"arguments[0].value = '"
					+ SINGLE_QUOTE_RE.matcher(textValue).replaceAll("\\'")
					+ "';", element);

			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}
}
