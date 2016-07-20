package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkState;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.ThreadDetails;
import au.com.agic.apptesting.utils.impl.GetByImpl;
import au.com.agic.apptesting.utils.impl.SimpleWebElementInteractionImpl;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.ExecutionException;

import cucumber.api.java.en.When;

/**
 * This class contains Gherkin steps that define wait conditions.
 *
 * These steps have Atom snipptets that start with the prefix "wait".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class WaitStepDefinitions {

	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();
	private static final GetBy GET_BY = new GetByImpl();
	private static final SimpleWebElementInteraction SIMPLE_WEB_ELEMENT_INTERACTION =
		new SimpleWebElementInteractionImpl();

	private static final long MILLISECONDS_PER_SECOND = 1000;

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread(
			Thread.currentThread().getName());

	/**
	 * Pauses the execution of the test script for the given number of seconds
	 *
	 * @param sleepDuration The number of seconds to pause the script for
	 */
	@When("^I (?:wait|sleep) for \"(\\d+)\" second(?:s?)$")
	public void sleepStep(final String sleepDuration) {
		SLEEP_UTILS.sleep(Integer.parseInt(sleepDuration) * MILLISECONDS_PER_SECOND);
	}

	/**
	 * Waits the given amount of time for an element to be displayed (i.e. to be visible) on the page. <p>
	 * This is most useful when waiting for a page to load completely. You can use this step to pause the
	 * script until some known element is visible, which is a good indication that the page has loaded
	 * completely.
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
	@When("^I wait \"(\\d+)\" seconds for (?:a|an|the) element found by( alias)? \"([^\"]*)\" to be displayed"
		+ "(,? ignoring timeouts?)?")
	public void displaySimpleWaitStep(
		final String waitDuration,
		final String alias,
		final String selectorValue,
		final String ignoringTimeout) throws ExecutionException, InterruptedException {

		try {
			SIMPLE_WEB_ELEMENT_INTERACTION.getVisibleElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails,
				Long.parseLong(waitDuration)).get();
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
	 * Waits the given amount of time for an element to be displayed (i.e. to be visible) on the page. <p>
	 * This is most useful when waiting for a page to load completely. You can use this step to pause the
	 * script until some known element is visible, which is a good indication that the page has loaded
	 * completely.
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
	@When("^I wait \"(\\d+)\" seconds for (?:a|an|the) element with "
		+ "(?:a|an|the) (ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\" to be displayed"
		+ "(,? ignoring timeouts?)?")
	public void displayWaitStep(
		final String waitDuration,
		final String selector,
		final String alias,
		final String selectorValue,
		final String ignoringTimeout) {
		final By by = GET_BY.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, threadDetails);
		final WebDriverWait wait = new WebDriverWait(
			threadDetails.getWebDriver(), Integer.parseInt(waitDuration));

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
	 * Waits the given amount of time for an element to be clickable on the page. <p> This is most
	 * useful when waiting for an element to be in a state where it can be interacted with.
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
	@When("^I wait \"(\\d+)\" seconds for (?:a|an|the) element found by (ID|class|xpath|name|css selector)"
		+ "( alias)? \"([^\"]*)\" to be clickable(,? ignoring timeouts?)?")
	public void clickWaitStep(
		final String waitDuration,
		final String alias,
		final String selectorValue,
		final String ignoringTimeout) throws ExecutionException, InterruptedException {
		try {
			SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails,
				Long.parseLong(waitDuration)).get();
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
	 * Waits the given amount of time for an element to be clickable on the page. <p> This is most
	 * useful when waiting for an element to be in a state where it can be interacted with.
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
	@When("^I wait \"(\\d+)\" seconds for (?:a|an|the) element with "
		+ "(?:a|an|the) (ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\" to be clickable"
		+ "(,? ignoring timeouts?)?")
	public void clickWaitStep(
		final String waitDuration,
		final String selector,
		final String alias,
		final String selectorValue,
		final String ignoringTimeout) {
		final By by = GET_BY.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, threadDetails);
		final WebDriverWait wait = new WebDriverWait(
			threadDetails.getWebDriver(), Integer.parseInt(waitDuration));

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
	 * Waits the given amount of time for an element to be placed in the DOM. Note that the element does not
	 * have to be visible, just present in the HTML. <p> This is most useful when waiting for a page to load
	 * completely. You can use this step to pause the script until some known element is visible, which is a
	 * good indication that the page has loaded completely.
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
	@When("^I wait \"(\\d+)\" seconds for (?:a|an|the) element found by( alias)? \"([^\"]*)\" "
		+ "to be present(,? ignoring timeouts?)?")
	public void presentSimpleWaitStep(
		final String waitDuration,
		final String alias,
		final String selectorValue,
		final String ignoringTimeout) throws ExecutionException, InterruptedException {

		try {
			SIMPLE_WEB_ELEMENT_INTERACTION.getPresenceElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails,
				Long.parseLong(waitDuration)).get();
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
	 * Waits the given amount of time for an element to be placed in the DOM. Note that the element does not
	 * have to be visible, just present in the HTML. <p> This is most useful when waiting for a page to load
	 * completely. You can use this step to pause the script until some known element is visible, which is a
	 * good indication that the page has loaded completely.
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
	@When("^I wait \"(\\d+)\" seconds for (?:a|an|the) element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\" "
		+ "to be present(,? ignoring timeouts?)?")
	public void presentWaitStep(
		final String waitDuration,
		final String selector,
		final String alias,
		final String selectorValue,
		final String ignoringTimeout) {
		final By by = GET_BY.getBy(selector, StringUtils.isNotBlank(alias), selectorValue, threadDetails);
		final WebDriverWait wait = new WebDriverWait(
			threadDetails.getWebDriver(), Integer.parseInt(waitDuration));
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
	 * Waits the given amount of time for a link with the supplied text to be placed in the DOM. Note that the
	 * element does not have to be visible just present in the HTML.
	 *
	 * @param waitDuration The maximum amount of time to wait for
	 * @param alias           If this word is found in the step, it means the linkContent is found from the
	 *                        data set.
	 * @param linkContent  The text content of the link we are wait for
	 */
	@When("^I wait \"(\\d+)\" seconds for a link with the text content of( alias) \"([^\"]*)\" to be present")
	public void presentLinkStep(final String waitDuration, final String alias, final String linkContent) {
		final String content = StringUtils.isNotBlank(alias)
			? threadDetails.getDataSet().get(linkContent) : linkContent;
		final WebDriverWait wait = new WebDriverWait(
			threadDetails.getWebDriver(), Integer.parseInt(waitDuration));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(content)));
	}
}
