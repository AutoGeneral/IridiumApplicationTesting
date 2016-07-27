package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.HttpResponseException;
import au.com.agic.apptesting.exception.ValidationException;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.ThreadDetails;
import au.com.agic.apptesting.utils.impl.BrowsermobProxyUtilsImpl;
import au.com.agic.apptesting.utils.impl.GetByImpl;
import au.com.agic.apptesting.utils.impl.SimpleWebElementInteractionImpl;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;

import net.lightbody.bmp.util.HttpMessageInfo;

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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import cucumber.api.java.en.Then;

/**
 * Contains Gherkin step definitions for checking the current state of the web page.
 *
 * These steps have Atom snipptets that start with the prefix "verify".
 * See https://github.com/mcasperson/iridium-snippets for more details.
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
	@Then("^(?:I verify that )?the browser title should be \"([^\"]*)\"$")
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
	@Then("^(?:I verify that )?the element found by( alias)? \"([^\"]*)\" should have a "
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
	@Then("^(?:I verify that )?the element with the (ID|class|xpath|name|css selector)( alias)? \"([^\"]*)\" should have a "
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

	/**
	 * Verify that an aliased value a blank string
	 * @param alias The aliased value to check
	 */
	@Then("I verify that the alias \"([^\"]*)\" is empty")
	public void verifyBlank(final String alias) {
		final String value = threadDetails.getDataSet().get(alias);
		Assert.assertTrue(StringUtils.isBlank(value));
	}

	/**
	 * Verify that an aliased value is not a blank string
	 * @param alias The aliased value to check
	 */
	@Then("I verify that the alias \"([^\"]*)\" is not empty")
	public void verifyNotBlank(final String alias) {
		final String value = threadDetails.getDataSet().get(alias);
		Assert.assertTrue(StringUtils.isNotBlank(value));
	}

	/**
	 * Verify that an aliased value is a number
	 * @param alias The aliased value to check
	 */
	@Then("I verify that the alias \"([^\"]*)\" is a number")
	public void verifyIsNumber(final String alias) {
		final String value = threadDetails.getDataSet().get(alias);
		Double.parseDouble(value);
	}

	/**
	 * Verify that an aliased value is not a number
	 * @param alias The aliased value to check
	 */
	@Then("I verify that the alias \"([^\"]*)\" is not a number")
	public void verifyIsNotNumber(final String alias) {
		final String value = threadDetails.getDataSet().get(alias);
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
	@Then("I verify that the alias \"([^\"]*)\" matches the regex \"([^\"]*)\"")
	public void verifyMatchesRegex(final String alias, final String regex) {
		final String value = threadDetails.getDataSet().get(alias);
		Assert.assertTrue(Pattern.matches(regex, value));
	}

	/**
	 * Verify that an aliased value does not match the regex
	 * @param alias The aliased value to check
	 * @param regex The regex to match against the aliased value
	 */
	@Then("I verify that the alias \"([^\"]*)\" does not match the regex \"([^\"]*)\"")
	public void verifyNotMatchesRegex(final String alias, final String regex) {
		final String value = threadDetails.getDataSet().get(alias);
		final Pattern pattern = Pattern.compile(regex);
		Assert.assertFalse(Pattern.matches(regex, value));
	}

	/**
	 * Verify that an aliased value is equal to the supplied string
	 * @param alias The aliased value to check
	 * @param expectedValue The value that the aliased value is expected to equal
	 */
	@Then("I verify that the alias \"([^\"]*)\" is equal to \"([^\"]*)\"")
	public void verifyIsEqual(final String alias, final String expectedValue) {
		final String value = threadDetails.getDataSet().get(alias);
		Assert.assertEquals(expectedValue, value);
	}

	/**
	 * Verify that an aliased value is not equal to the supplied string
	 * @param alias The aliased value to check
	 * @param expectedValue The value that the aliased value is expected to equal
	 */
	@Then("I verify that the alias \"([^\"]*)\" is not equal to \"([^\"]*)\"")
	public void verifyIsNotEqual(final String alias, final String expectedValue) {
		final String value = threadDetails.getDataSet().get(alias);
		Assert.assertNotEquals(expectedValue, value);
	}

	/**
	 * We track response codes in the BrowsermobProxyUtilsImpl class, and if any where in the
	 * range 400 - 599, we output those as an error.
	 */
	@SuppressWarnings("unchecked")
	@Then("I verify that there were no HTTP errors")
	public void verifyHttpCodes() {
		final Optional<ProxyDetails<?>> browserMob =
			threadDetails.getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		if (browserMob.isPresent()) {
			if (browserMob.get().getProperties().containsKey(BrowsermobProxyUtilsImpl.INVALID_REQUESTS)) {
				final List<HttpMessageInfo> responses =
					(List<HttpMessageInfo>)browserMob.get()
						.getProperties().get(BrowsermobProxyUtilsImpl.INVALID_REQUESTS);

				if (!responses.isEmpty()) {

					final StringBuilder message =
						new StringBuilder("The following URLs returned HTTP errors\n");

					responses.stream().forEach(x -> message.append(x.getOriginalUrl() + "\n"));

					throw new HttpResponseException(message.toString());
				}
			}
		}
	}

	/**
	 * Checks for the presence of some text on the page.
	 * @param alias This text appears if the text is astucally an alias key
	 * @param text The text to find on the page, or the alias to the text
	 */
	@Then("^I verify that the page contains the text( alias)? \"(.*?)\"")
	public void verifyPageContent(final String alias, final String text) {
		final String fixedtext = StringUtils.isNotBlank(alias)
			? threadDetails.getDataSet().get(text) : text;

		final String pageText =
			threadDetails.getWebDriver().findElement(By.tagName("body")).getText();

		if (!pageText.contains(fixedtext)) {
			throw new ValidationException("Could not find the text \"" + fixedtext + "\" on the page");
		}
	}
}
