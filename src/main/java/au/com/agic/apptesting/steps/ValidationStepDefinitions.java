package au.com.agic.apptesting.steps;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.HttpResponseException;
import au.com.agic.apptesting.exception.ValidationException;
import au.com.agic.apptesting.exception.WebElementException;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.impl.BrowsermobProxyUtilsImpl;

import net.lightbody.bmp.util.HttpMessageInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Contains Gherkin step definitions for checking the current state of the web page.
 *
 * These steps have Atom snipptets that start with the prefix "verify".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class ValidationStepDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(ValidationStepDefinitions.class);
	@Autowired
	private SleepUtils sleepUtils;
	@Autowired
	private AutoAliasUtils autoAliasUtils;
	@Autowired
	private GetBy getBy;
	@Autowired
	private SimpleWebElementInteraction simpleWebElementInteraction;

	/**
	 * Verify the title in the browser
	 *
	 * @param browserTitle Defines what the browser title should be
	 */
	@Then("^(?:I verify that )?the browser title should be \"([^\"]*)\"$")
	public void checkBrowserTitleStep(final String browserTitle) {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		Assert.assertEquals(browserTitle, webDriver.getTitle());
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
	@Then("^(?:I verify(?: that)? )?the element found by( alias)? \"([^\"]*)\" should have a "
		+ "class( alias)? of \"([^\"]*)\"( if it exists)?$")
	public void checkElementClassStep(
		final String selectorAlias,
		final String selectorValue,
		final String classAlias,
		final String classValue,
		final String exists) {
		try {
			final WebElement element = simpleWebElementInteraction.getClickableElementFoundBy(
				StringUtils.isNotBlank(selectorAlias),
				selectorValue,
				State.getFeatureStateForThread());

			final String className = autoAliasUtils.getValue(
				classValue, StringUtils.isNotBlank(classAlias), State.getFeatureStateForThread());

			final Iterable<String> split = Splitter.on(' ')
				.trimResults()
				.omitEmptyStrings()
				.split(element.getAttribute("class"));

			Assert.assertTrue(Iterables.contains(split, className));
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
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
	@Then("^(?:I verify(?: that)? )?the element with the (ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\""
		+ " (?:(?:should have)|(?:has))? a class( alias)? of \"([^\"]*)\"( if it exists)?$")
	public void checkElementClassStep(
		final String selector,
		final String selectorAlias,
		final String selectorValue,
		final String classAlias,
		final String classValue,
		final String exists) {
		try {
			final By by = getBy.getBy(
				selector,
				StringUtils.isNotBlank(selectorAlias),
				selectorValue,
				State.getFeatureStateForThread());
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));

			final String className = autoAliasUtils.getValue(
				classValue, StringUtils.isNotBlank(classAlias), State.getFeatureStateForThread());

			final Iterable<String> split = Splitter.on(' ')
				.trimResults()
				.omitEmptyStrings()
				.split(element.getAttribute("class"));

			Assert.assertTrue(Iterables.contains(split, className));
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Verify that an aliased value a blank string
	 * @param alias The aliased value to check
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" is empty")
	public void verifyBlank(final String alias) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		Assert.assertTrue(StringUtils.isBlank(value));
	}

	/**
	 * Verify that an aliased value is not a blank string
	 * @param alias The aliased value to check
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" is not empty")
	public void verifyNotBlank(final String alias) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		Assert.assertTrue(StringUtils.isNotBlank(value));
	}

	/**
	 * Verify that an aliased value is a number
	 * @param alias The aliased value to check
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" is a number")
	public void verifyIsNumber(final String alias) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		Double.parseDouble(value);
	}

	/**
	 * Verify that an aliased value is not a number
	 * @param alias The aliased value to check
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" is not a number")
	public void verifyIsNotNumber(final String alias) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		try {
			Double.parseDouble(value);
		} catch (final NumberFormatException ex) {
			return;
		}

		throw new AssertionError("Alias " + alias + " value of " + value + " was a number");
	}

	/**
	 * Verify that an aliased value matches the regex
	 * @param alias The aliased value to check
	 * @param regex The regex to match against the aliased value
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" matches the regex \"([^\"]*)\"")
	public void verifyMatchesRegex(final String alias, final String regex) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		Assert.assertTrue("Value " + value + " should match regex " + regex, Pattern.matches(regex, value));
	}

	/**
	 * Verify that an aliased value does not match the regex
	 * @param alias The aliased value to check
	 * @param regex The regex to match against the aliased value
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" does not match the regex \"([^\"]*)\"")
	public void verifyNotMatchesRegex(final String alias, final String regex) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		Assert.assertFalse(
			"Value " + value + " should not match regex " + regex,
			Pattern.matches(regex, value));
	}

	/**
	 * Verify that an aliased value is equal to the supplied string
	 * @param alias The aliased value to check
	 * @param valueAlias Add the word alias to indicate that the expected value is an alias key
	 * @param expectedValue The value that the aliased value is expected to equal
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" is equal to((?: the)? alias)? \"([^\"]*)\"")
	public void verifyIsEqual(final String alias, final String valueAlias, final String expectedValue) {
		final String fixedValue = autoAliasUtils.getValue(
			expectedValue, StringUtils.isNotBlank(valueAlias), State.getFeatureStateForThread());

		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		Assert.assertEquals(fixedValue, value);
	}

	/**
	 * Verify that an aliased value is not equal to the supplied string
	 * @param alias The aliased value to check
	 * @param valueAlias Add the word alias to indicate that the expected value is an alias key
	 * @param expectedValue The value that the aliased value is expected to equal
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" is not equal to((?: the)? alias)? \"([^\"]*)\"")
	public void verifyIsNotEqual(final String alias, final String valueAlias, final String expectedValue) {
		final String fixedValue = autoAliasUtils.getValue(
			expectedValue, StringUtils.isNotBlank(valueAlias), State.getFeatureStateForThread());

		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		Assert.assertNotEquals(fixedValue, value);
	}

	/**
	 * We track response codes in the BrowsermobProxyUtilsImpl class, and if any where in the
	 * range 400 - 599, we output those as an error.
	 */
	@SuppressWarnings("unchecked")
	@Then("(?:I verify(?: that)? )?there were no HTTP errors")
	public void verifyHttpCodes() {
		final Optional<ProxyDetails<?>> browserMob =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		if (browserMob.isPresent()) {
			if (browserMob.get().getProperties().containsKey(BrowsermobProxyUtilsImpl.INVALID_REQUESTS)) {
				final List<HttpMessageInfo> responses =
					(List<HttpMessageInfo>) browserMob.get()
						.getProperties().get(BrowsermobProxyUtilsImpl.INVALID_REQUESTS);

				if (!responses.isEmpty()) {

					final StringBuilder message =
						new StringBuilder("The following URLs returned HTTP errors\n");

					responses.stream().forEach(x -> {
						message.append(x.getOriginalUrl());
						message.append("\n");
					});

					throw new HttpResponseException(message.toString());
				}
			}
		}
	}

	/**
	 * We track response codes in the BrowsermobProxyUtilsImpl class, and if any where in the
	 * range 400 - 599, we report them. Note that we don't throw an error, so the scenario won't
	 * fail
	 */
	@SuppressWarnings("unchecked")
	@Then("I report any HTTP errors")
	public void reportHttpCodes() {
		final Optional<ProxyDetails<?>> browserMob =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		if (browserMob.isPresent()) {
			if (browserMob.get().getProperties().containsKey(BrowsermobProxyUtilsImpl.INVALID_REQUESTS)) {
				final List<HttpMessageInfo> responses =
					(List<HttpMessageInfo>) browserMob.get()
						.getProperties().get(BrowsermobProxyUtilsImpl.INVALID_REQUESTS);

				if (!responses.isEmpty()) {

					final StringBuilder message =
						new StringBuilder("The following URLs returned HTTP errors\n");

					responses.stream().forEach(x -> {
						message.append(x.getOriginalUrl());
						message.append("\n");
					});

					LOGGER.info(message.toString());
				}
			}
		}
	}

	/**
	 * Checks for the presence of some text on the page.
	 * @param alias This text appears if the text is astucally an alias key
	 * @param text The text to find on the page, or the alias to the text
	 */
	@Then("^(?:I verify(?: that)? )?the page contains the text( alias)? \"(.*?)\"")
	public void verifyPageContent(final String alias, final String text) {
		final String fixedtext = autoAliasUtils.getValue(text, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final String pageText =
			webDriver.findElement(By.tagName("body")).getText();

		if (!pageText.contains(fixedtext)) {
			throw new ValidationException("Could not find the text \"" + fixedtext + "\" on the page");
		}
	}

	/**
	 * Checks for the presence of a regex on the page.
	 * @param alias This text appears if the regex is astucally an alias key
	 * @param regex The regex to find on the page, or the alias to the regex
	 */
	@Then("^(?:I verify(?: that)? )?the page contains the regex( alias)? \"(.*?)\"")
	public void verifyPageRegexContent(final String alias, final String regex) {
		final String fixedRegex = autoAliasUtils.getValue(regex, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();

		final Pattern pattern = Pattern.compile(fixedRegex);

		final String pageText =
			webDriver.findElement(By.tagName("body")).getText();

		if (!pattern.matcher(pageText).find()) {
			throw new ValidationException("Could not find the regex \"" + fixedRegex + "\" on the page");
		}
	}

	/**
	 * Checks for the absence of some text on the page.
	 * @param alias This text appears if the text is astucally an alias key
	 * @param text The text to find on the page, or the alias to the text
	 */
	@Then("^(?:I verify(?: that)? )?the page does not contain the text( alias)? \"(.*?)\"")
	public void verifyPageContentAbsent(final String alias, final String text) {
		final String fixedtext = autoAliasUtils.getValue(text, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final String pageText =
			webDriver.findElement(By.tagName("body")).getText();

		if (pageText.contains(fixedtext)) {
			throw new ValidationException("Found the text \"" + fixedtext + "\" on the page");
		}
	}

	/**
	 * Checks for the absence of a regex on the page.
	 * @param alias This text appears if the regex is astucally an alias key
	 * @param regex The regex to find on the page, or the alias to the regex
	 */
	@Then("^(?:I verify(?: that)? )?the page does not contain the regex( alias)? \"(.*?)\"")
	public void verifyPageRegexNotContent(final String alias, final String regex) {
		final String fixedRegex = autoAliasUtils.getValue(regex, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();

		final Pattern pattern = Pattern.compile(fixedRegex);

		final String pageText =
			webDriver.findElement(By.tagName("body")).getText();

		if (pattern.matcher(pageText).find()) {
			throw new ValidationException("Found the regex \"" + fixedRegex + "\" on the page");
		}
	}

	/**
	 * Verify that an aliased value is bigger than another alias value
	 * @param alias1 The aliased value to check
	 * @param valueAlias Add the word alias to indicate that the expected value is an alias key
	 * @param value The second aliased value to compare the first too
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" is larger than((?: the)? alias)? \"([^\"]*)\"")
	public void verifyAliasBigger(final String alias1, final String valueAlias, final String value) {
		final String value1 = State.getFeatureStateForThread().getDataSet().get(alias1);

		final String fixedValue = autoAliasUtils.getValue(
			value, StringUtils.isNotBlank(valueAlias), State.getFeatureStateForThread());

		Assert.assertTrue(
			"Alias " + alias1 + " with value " + Double.parseDouble(value1)
				+ " was not larger than " + fixedValue + " with value " + Double.parseDouble(fixedValue),
			Double.parseDouble(value1) > Double.parseDouble(fixedValue));
	}

	/**
	 * Verify that an aliased value is bigger than or equal to another alias value
	 * @param alias1 The aliased value to check
	 * @param valueAlias Add the word alias to indicate that the expected value is an alias key
	 * @param value The second aliased value to compare the first to
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" is larger than or equal to((?: the)? alias)? \"([^\"]*)\"")
	public void verifyAliasBiggerOrEqual(final String alias1, final String valueAlias, final String value) {
		final String value1 = State.getFeatureStateForThread().getDataSet().get(alias1);

		final String fixedValue = autoAliasUtils.getValue(
			value, StringUtils.isNotBlank(valueAlias), State.getFeatureStateForThread());

		Assert.assertTrue(
			"Alias " + alias1 + " with value " + Double.parseDouble(value1)
				+ " was not larger than or equal to " + fixedValue
				+ " with value " + Double.parseDouble(fixedValue),
			Double.parseDouble(value1) >= Double.parseDouble(fixedValue));
	}

	/**
	 * Verify that an aliased value is smaller than another alias value
	 * @param alias1 The aliased value to check
	 * @param valueAlias Add the word alias to indicate that the expected value is an alias key
	 * @param value The second aliased value to compare the first to
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" is smaller than((?: the)? alias)? \"([^\"]*)\"")
	public void verifyAliasSmaller(final String alias1, final String valueAlias, final String value) {
		final String value1 = State.getFeatureStateForThread().getDataSet().get(alias1);

		final String fixedValue = autoAliasUtils.getValue(
			value, StringUtils.isNotBlank(valueAlias), State.getFeatureStateForThread());

		Assert.assertTrue(
			"Alias " + alias1 + " with value " + Double.parseDouble(value1)
				+ " was not smaller than " + fixedValue + " with value " + Double.parseDouble(fixedValue),
			Double.parseDouble(value1) < Double.parseDouble(fixedValue));
	}

	/**
	 * Verify that an aliased value is smaller than or equal to another alias value
	 * @param alias1 The aliased value to check
	 * @param valueAlias Add the word alias to indicate that the expected value is an alias key
	 * @param value The second aliased value to compare the first to
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" is smaller than or equal to((?: the)? alias)? \"([^\"]*)\"")
	public void verifyAliasSmallerOrEqual(final String alias1, final String valueAlias, final String value) {
		final String value1 = State.getFeatureStateForThread().getDataSet().get(alias1);

		final String fixedValue = autoAliasUtils.getValue(
			value, StringUtils.isNotBlank(valueAlias), State.getFeatureStateForThread());

		Assert.assertTrue(
			"Alias " + alias1 + " with value " + Double.parseDouble(value1)
				+ " was not smaller than or equal to " + fixedValue
				+ " with value " + Double.parseDouble(fixedValue),
			Double.parseDouble(value1) <= Double.parseDouble(fixedValue));
	}

	/**
	 * Validates that an aliased value has the expected length
	 * @param alias The aliased value to check
	 * @param length The expected length of the aliased value
	 */
	@Then("(?:I verify(?: that)? )?(?:the )?alias \"([^\"]*)\" is \"(\\d+)\" characters long")
	public void verifyLength(final String alias, final String length) {
		final String value = State.getFeatureStateForThread().getDataSet().get(alias);
		final Integer lengthInt = Integer.parseInt(length);
		Assert.assertEquals(lengthInt.intValue(), value.length());
	}

	/**
	 * Verifies that the element is displayed (i.e. to be visible) on the page within a given amount of time.
	 * You can use this step to verify that the page is in the correct state before proceeding with the script.
	 *
	 * @param waitDuration    The maximum amount of time to wait for
	 * @param alias           If this word is found in the step, it means the selectorValue is found from the
	 *                        data set.
	 * @param selectorValue   The value used in conjunction with the selector to match the element. If alias
	 *                        was set, this value is found from the data set. Otherwise it is a literal
	 *                        value.
	 * @param ignoringTimeout include this text to continue the script in the event that the element can't be
	 *                        found
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element found by( alias)? \"([^\"]*)\" is displayed"
		+ "(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void displaySimpleWaitStep(
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {

		try {
			simpleWebElementInteraction.getVisibleElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread(),
				NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()));
		} catch (final Exception ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that the element is not displayed (i.e. to be visible) on the page within a given amount of time.
	 * You can use this step to verify that the page is in the correct state before proceeding with the script.
	 *
	 * @param waitDuration    The maximum amount of time to wait for
	 * @param alias           If this word is found in the step, it means the selectorValue is found from the
	 *                        data set.
	 * @param selectorValue   The value used in conjunction with the selector to match the element. If alias
	 *                        was set, this value is found from the data set. Otherwise it is a literal
	 *                        value.
	 * @param ignoringTimeout include this text to continue the script in the event that the element can't be
	 *                        found
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element found by( alias)? \"([^\"]*)\" is not displayed"
		+ "(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void notDisplaySimpleWaitStep(
		final String alias,
		final String selectorValue,
		final String ignoringTimeout,
		final String waitDuration) {

		try {
			simpleWebElementInteraction.getNotVisibleElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread(),
				NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()));
		} catch (final Exception ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that the element is displayed (i.e. to be visible) on the page within a given amount of time.
	 * You can use this step to verify that the page is in the correct state before proceeding with the script.
	 *
	 * @param waitDuration    The maximum amount of time to wait for
	 * @param selector        Either ID, class, xpath, name or css selector
	 * @param alias           If this word is found in the step, it means the selectorValue is found from the
	 *                        data set.
	 * @param selectorValue   The value used in conjunction with the selector to match the element. If alias
	 *                        was set, this value is found from the data set. Otherwise it is a literal
	 *                        value.
	 * @param ignoringTimeout include this text to continue the script in the event that the element can't be
	 *                        found
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element with "
		+ "(?:a|an|the) (ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\" is displayed"
		+ "(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void displayWaitStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final By by = getBy.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, State.getFeatureStateForThread());
		final WebDriverWait wait = new WebDriverWait(
			webDriver,
			NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()),
			Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);

		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (final TimeoutException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that the element is not displayed (i.e. to be visible) on the page within a given amount of time.
	 * You can use this step to verify that the page is in the correct state before proceeding with the script.
	 *
	 * @param waitDuration    The maximum amount of time to wait for
	 * @param selector        Either ID, class, xpath, name or css selector
	 * @param alias           If this word is found in the step, it means the selectorValue is found from the
	 *                        data set.
	 * @param selectorValue   The value used in conjunction with the selector to match the element. If alias
	 *                        was set, this value is found from the data set. Otherwise it is a literal
	 *                        value.
	 * @param ignoringTimeout include this text to continue the script in the event that the element can't be
	 *                        found
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element with "
		+ "(?:a|an|the) (ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\" is not displayed"
		+ "(?: within \"(\\d+)\" seconds?)(,? ignoring timeouts?)?")
	public void notDisplayWaitStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final By by = getBy.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, State.getFeatureStateForThread());
		final WebDriverWait wait = new WebDriverWait(
			webDriver,
			NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()),
			Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);

		try {
			final boolean result = wait.until(
				ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(by)));
			if (!result) {
				throw new TimeoutException(
					"Gave up after waiting " + Integer.parseInt(waitDuration)
						+ " seconds for the element to not be displayed");
			}
		} catch (final TimeoutException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that the element is clickable within a given amount of time.
	 * You can use this step to verify that the page is in the correct state before proceeding with the script.
	 *
	 * @param waitDuration    The maximum amount of time to wait for
	 * @param alias           If this word is found in the step, it means the selectorValue is found from the
	 *                        data set.
	 * @param selectorValue   The value used in conjunction with the selector to match the element. If alias
	 *                        was set, this value is found from the data set. Otherwise it is a literal
	 *                        value.
	 * @param ignoringTimeout include this text to continue the script in the event that the element can't be
	 *                        found
	 */
	@When("^(?:I verify(?: that)? )?(?:a|an|the) element found by"
		+ "( alias)? \"([^\"]*)\" is clickable(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void clickWaitStep(
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {
		try {
			simpleWebElementInteraction.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread(),
				NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()));
		} catch (final WebElementException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that the element is clickable within a given amount of time.
	 * You can use this step to verify that the page is in the correct state before proceeding with the script.
	 *
	 * @param waitDuration    The maximum amount of time to wait for
	 * @param selector        Either ID, class, xpath, name or css selector
	 * @param alias           If this word is found in the step, it means the selectorValue is found from the
	 *                        data set.
	 * @param selectorValue   The value used in conjunction with the selector to match the element. If alias
	 *                        was set, this value is found from the data set. Otherwise it is a literal
	 *                        value.
	 * @param ignoringTimeout include this text to continue the script in the event that the element can't be
	 *                        found
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element with "
		+ "(?:a|an|the) (ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\" is clickable"
		+ "(?: within \"(\\d+)\" seconds?)(,? ignoring timeouts?)?")
	public void clickWaitStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final By by = getBy.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, State.getFeatureStateForThread());
		final WebDriverWait wait = new WebDriverWait(
			webDriver,
			NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()),
			Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);

		try {
			wait.until(ExpectedConditions.elementToBeClickable(by));
		} catch (final TimeoutException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that the element is placed in the DOM within a certain amount of time. Note that the element
	 * does not have to be visible, just present in the HTML.
	 * You can use this step to verify that the page is in the correct state before proceeding with the script.
	 *
	 * @param waitDuration    The maximum amount of time to wait for
	 * @param alias           If this word is found in the step, it means the selectorValue is found from the
	 *                        data set.
	 * @param selectorValue   The value used in conjunction with the selector to match the element. If alias
	 *                        was set, this value is found from the data set. Otherwise it is a literal
	 *                        value.
	 * @param ignoringTimeout Include this text to ignore a timeout while waiting for the element to be
	 *                        present
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element found by( alias)? \"([^\"]*)\" "
		+ "is present(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void presentSimpleWaitStep(
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {

		try {
			simpleWebElementInteraction.getPresenceElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread(),
				NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()));
		} catch (final WebElementException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that the element is not placed in the DOM within a certain amount of time.
	 * You can use this step to verify that the page is in the correct state before proceeding with the script.
	 *
	 * @param waitDuration    The maximum amount of time to wait for
	 * @param alias           If this word is found in the step, it means the selectorValue is found from the
	 *                        data set.
	 * @param selectorValue   The value used in conjunction with the selector to match the element. If alias
	 *                        was set, this value is found from the data set. Otherwise it is a literal
	 *                        value.
	 * @param ignoringTimeout Include this text to ignore a timeout while waiting for the element to be
	 *                        present
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element found by( alias)? \"([^\"]*)\" "
		+ "is not present(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void notPresentSimpleWaitStep(
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {

		try {
			simpleWebElementInteraction.getNotPresenceElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread(),
				NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()));
		} catch (final WebElementException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that the element is placed in the DOM within a certain amount of time. Note that the element
	 * does not have to be visible, just present in the HTML.
	 * You can use this step to verify that the page is in the correct state before proceeding with the script.
	 *
	 * @param waitDuration    The maximum amount of time to wait for
	 * @param selector        Either ID, class, xpath, name or css selector
	 * @param alias           If this word is found in the step, it means the selectorValue is found from the
	 *                        data set.
	 * @param selectorValue   The value used in conjunction with the selector to match the element. If alias
	 *                        was set, this value is found from the data set. Otherwise it is a literal
	 *                        value.
	 * @param ignoringTimeout Include this text to ignore a timeout while waiting for the element to be
	 *                        present
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\" "
		+ "is present(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void presentWaitStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final By by = getBy.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, State.getFeatureStateForThread());
		final WebDriverWait wait = new WebDriverWait(
			webDriver,
			NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()),
			Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated(by));
		} catch (final TimeoutException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that the element is not placed in the DOM within a certain amount of time.
	 * You can use this step to verify that the page is in the correct state before proceeding with the script.
	 *
	 * @param waitDuration    The maximum amount of time to wait for
	 * @param selector        Either ID, class, xpath, name or css selector
	 * @param alias           If this word is found in the step, it means the selectorValue is found from the
	 *                        data set.
	 * @param selectorValue   The value used in conjunction with the selector to match the element. If alias
	 *                        was set, this value is found from the data set. Otherwise it is a literal
	 *                        value.
	 * @param ignoringTimeout Include this text to ignore a timeout while waiting for the element to be
	 *                        present
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\" "
		+ "is not present(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void notPresentWaitStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final By by = getBy.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, State.getFeatureStateForThread());
		final WebDriverWait wait = new WebDriverWait(
			webDriver,
			NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()),
			Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
		try {
			final boolean result = wait.until(
				ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(by)));
			if (!result) {
				throw new TimeoutException(
					"Gave up after waiting " + Integer.parseInt(waitDuration)
						+ " seconds for the element to not be present");
			}
		} catch (final TimeoutException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that a link with the supplied text is placed in the DOM within a certain amount of time.
	 * Note that the element does not have to be visible just present in the HTML.
	 *
	 * @param waitDuration The maximum amount of time to wait for
	 * @param alias           If this word is found in the step, it means the linkContent is found from the
	 *                        data set.
	 * @param linkContent  The text content of the link we are wait for
	 * @param ignoringTimeout The presence of this text indicates that timeouts are ignored
	 */
	@Then("^(?:I verify(?: that)? )?a link with the text content of"
		+ "( alias) \"([^\"]*)\" is present(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void presentLinkStep(
		final String alias,
		final String linkContent,
		final String waitDuration,
		final String ignoringTimeout) {

		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final String content = autoAliasUtils.getValue(
				linkContent, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(content)));
		} catch (final TimeoutException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that a link with the supplied text is not placed in the DOM within a certain amount of time.
	 *
	 * @param waitDuration The maximum amount of time to wait for
	 * @param alias           If this word is found in the step, it means the linkContent is found from the
	 *                        data set.
	 * @param linkContent  The text content of the link we are wait for
	 * @param ignoringTimeout The presence of this text indicates that timeouts are ignored
	 */
	@Then("^(?:I verify(?: that)? )?a link with the text content of"
		+ "( alias) \"([^\"]*)\" is not present(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void notPresentLinkStep(
		final String alias,
		final String linkContent,
		final String waitDuration,
		final String ignoringTimeout) {

		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final String content = autoAliasUtils.getValue(
				linkContent, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final boolean result = wait.until(
				ExpectedConditions.not(
					ExpectedConditions.presenceOfAllElementsLocatedBy(By.linkText(content))));

			if (!result) {
				throw new TimeoutException(
					"Gave up after waiting " + Integer.parseInt(waitDuration)
						+ " seconds for the element to not be present");
			}
		} catch (final TimeoutException ex) {
				/*
					Rethrow if we have not ignored errors
				 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that an element with the supplied attribute and attribute value is displayed
	 * (i.e. to be visible) on the page within a certain amount of time.
	 *
	 * @param waitDuration  The maximum amount of time to wait for
	 * @param attribute     The attribute to use to select the element with
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias
	 *                         was set, this value is found from the data set. Otherwise it is a literal value.
	 * @param ignoringTimeout Include this text to ignore a timeout while waiting for the element to be present
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element with (?:a|an|the) attribute of \"([^\"]*)\" "
		+ "equal to( alias)? \"([^\"]*)\" is displayed(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void displayAttrWait(
		final String attribute,
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {

		final String attributeValue = autoAliasUtils.getValue(
			selectorValue, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());

		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			wait.until(ExpectedConditions.visibilityOfElementLocated(
				By.cssSelector("[" + attribute + "='" + attributeValue + "']")));
		} catch (final TimeoutException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that an element with the supplied attribute and attribute value is not displayed
	 * (i.e. to be visible) on the page within a certain amount of time.
	 *
	 * @param waitDuration  The maximum amount of time to wait for
	 * @param attribute     The attribute to use to select the element with
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias
	 *                         was set, this value is found from the data set. Otherwise it is a literal value.
	 * @param ignoringTimeout Include this text to ignore a timeout while waiting for the element to be present
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element with (?:a|an|the) attribute of \"([^\"]*)\" "
		+ "equal to( alias)? \"([^\"]*)\" is not displayed(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void notDisplayAttrWait(
		final String attribute,
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {

		final String attributeValue = autoAliasUtils.getValue(
			selectorValue, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());

		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final boolean result = wait.until(
				ExpectedConditions.not(ExpectedConditions.visibilityOfAllElementsLocatedBy(
					By.cssSelector("[" + attribute + "='" + attributeValue + "']"))));
			if (!result) {
				throw new TimeoutException(
					"Gave up after waiting " + Integer.parseInt(waitDuration)
						+ " seconds for the element to not be displayed");
			}
		} catch (final TimeoutException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that an element with the supplied attribute and attribute value is present in the DOM
	 * within a certain amount of time.
	 *
	 * @param waitDuration  The maximum amount of time to wait for
	 * @param attribute     The attribute to use to select the element with
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias
	 *                         was set, this value is found from the data set. Otherwise it is a literal value.
	 * @param ignoringTimeout Include this text to ignore a timeout while waiting for the element to be present
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element with (?:a|an|the) attribute of \"([^\"]*)\" "
		+ "equal to( alias)? \"([^\"]*)\" is present(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void presentAttrWait(
		final String attribute,
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {

		final String attributeValue = autoAliasUtils.getValue(
			selectorValue, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());

		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			wait.until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector("[" + attribute + "='" + attributeValue + "']")));
		} catch (final TimeoutException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}

	/**
	 * Verifies that an element with the supplied attribute and attribute value is not present in the DOM
	 * within a certain amount of time.
	 *
	 * @param waitDuration  The maximum amount of time to wait for
	 * @param attribute     The attribute to use to select the element with
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                         data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                         set, this value is found from the data set. Otherwise it is a literal value.
	 * @param ignoringTimeout Include this text to ignore a timeout while waiting for the element to be present
	 */
	@Then("^(?:I verify(?: that)? )?(?:a|an|the) element with (?:a|an|the) attribute of \"([^\"]*)\" "
		+ "equal to( alias)? \"([^\"]*)\" is not present(?: within \"(\\d+)\" seconds?)?(,? ignoring timeouts?)?")
	public void notPresentAttrWait(
		final String attribute,
		final String alias,
		final String selectorValue,
		final String waitDuration,
		final String ignoringTimeout) {

		final String attributeValue = autoAliasUtils.getValue(
			selectorValue, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());

		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				NumberUtils.toLong(waitDuration, State.getFeatureStateForThread().getDefaultWait()),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final boolean result = wait.until(
				ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(
					By.cssSelector("[" + attribute + "='" + attributeValue + "']"))));
			if (!result) {
				throw new TimeoutException(
					"Gave up after waiting " + Integer.parseInt(waitDuration)
						+ " seconds for the element to not be present");
			}
		} catch (final TimeoutException ex) {
			/*
				Rethrow if we have not ignored errors
			 */
			if (StringUtils.isBlank(ignoringTimeout)) {
				throw ex;
			}
		}
	}
}
