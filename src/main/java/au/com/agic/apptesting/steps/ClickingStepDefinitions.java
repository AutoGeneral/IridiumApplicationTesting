package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkState;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.WebElementException;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import au.com.agic.apptesting.utils.BrowserInteropUtils;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.JavaScriptRunner;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import cucumber.api.java.en.When;

/**
 * Gherkin steps used to click elements.
 *
 * These steps have Atom snipptets that start with the prefix "click".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class ClickingStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClickingStepDefinitions.class);
	@Autowired
	private GetBy getBy;
	@Autowired
	private SimpleWebElementInteraction simpleWebElementInteraction;
	@Autowired
	private SleepUtils sleepUtils;
	@Autowired
	private AutoAliasUtils autoAliasUtils;
	@Autowired
	private BrowserInteropUtils browserInteropUtils;
	@Autowired
	private JavaScriptRunner javaScriptRunner;

	/**
	 * A simplified step that will click on an element found by ID attribute, name attribue,
	 * class attribute, xpath or CSS selector. The first element to satisfy any of those
	 * conditions will be the one that the step interacts with. It is up to the caller
	 * to ensure that the selection is unique.
	 *
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param timesAlias    If this word is found in the step, it means the times is found from the
	 *                      data set.
	 * @param times         If this text is set, the click operation will be repeated the specified number of times.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) element found by( alias)? \"([^\"]*)\"(?:( alias)? \".*?\" times)?( if it exists)?$")
	public void clickElementSimpleStep(
		final String alias,
		final String selectorValue,
		final String timesAlias,
		final String times,
		final String exists) {
		try {
			final Integer fixedTimes = NumberUtils.toInt(
				autoAliasUtils.getValue(times, StringUtils.isNotBlank(timesAlias), State.getFeatureStateForThread()),
			1);

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			final WebElement element = simpleWebElementInteraction.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			/*
				Account for PhantomJS issues clicking certain types of elements
			 */
			final boolean treatAsHiddenElement = browserInteropUtils.treatElementAsHidden(
				webDriver, element, js);

			for (int i = 0; i < fixedTimes; ++i) {
				if (treatAsHiddenElement) {
					javaScriptRunner.interactHiddenElementMouseEvent(element, "click", js);
				} else {
					element.click();
				}

				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
			}

		} catch (final WebElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Clicks on an element
	 *
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param timesAlias    If this word is found in the step, it means the times is found from the
	 *                      data set.
	 * @param times         If this text is set, the click operation will be repeated the specified number of times.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) element with (?:a|an|the) (ID|class|xpath|name|css selector)( alias)? "
		+ "of \"([^\"]*)\"(?:( alias)? \".*?\" times)?( if it exists)?$")
	public void clickElementStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String timesAlias,
		final String times,
		final String exists) {
		try {
			final Integer fixedTimes = NumberUtils.toInt(
				autoAliasUtils.getValue(times, StringUtils.isNotBlank(timesAlias), State.getFeatureStateForThread()),
				1);

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final By by = getBy.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			/*
				Account for PhantomJS issues clicking certain types of elements
			 */
			final boolean treatAsHiddenElement = browserInteropUtils.treatElementAsHidden(
				webDriver, element, js);

			for (int i = 0; i < fixedTimes; ++i) {
				if (treatAsHiddenElement) {
					javaScriptRunner.interactHiddenElementMouseEvent(element, "click", js);
				} else {
					element.click();
				}

				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
			}
		} catch (final TimeoutException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Selects an element with simplified selection and clicks on an it regardless of wether is
	 * is or is not be visible on the page
	 *
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param timesAlias    If this word is found in the step, it means the times is found from the
	 *                      data set.
	 * @param times         If this text is set, the click operation will be repeated the specified number of times.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) hidden element found by( alias)? "
		+ "\"([^\"]*)\"(?:( alias)? \".*?\" times)?( if it exists)?$")
	public void clickSimpleHiddenElementStep(
		final String alias,
		final String selectorValue,
		final String timesAlias,
		final String times,
		final String exists) {

		try {
			final Integer fixedTimes = NumberUtils.toInt(
				autoAliasUtils.getValue(times, StringUtils.isNotBlank(timesAlias), State.getFeatureStateForThread()),
				1);

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebElement element = simpleWebElementInteraction.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			/*
				PhantomJS doesn't support the click method, so "element.click()" won't work
				here. We need to dispatch the event instead.
			 */
			for (int i = 0; i < fixedTimes; ++i) {
				javaScriptRunner.interactHiddenElementMouseEvent(element, "click", js);
				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
			}
		} catch (final WebElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Clicks on an element that may or may not be visible on the page
	 *
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param timesAlias    If this word is found in the step, it means the times is found from the
	 *                      data set.
	 * @param times         If this text is set, the click operation will be repeated the specified number of times.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) hidden element with (?:a|an|the) (ID|class|xpath|name|css selector)( alias)? "
		+ "of \"([^\"]*)\"(?:( alias)? \".*?\" times)?( if it exists)?$")
	public void clickHiddenElementStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String timesAlias,
		final String times,
		final String exists) {

		try {
			final Integer fixedTimes = NumberUtils.toInt(
				autoAliasUtils.getValue(times, StringUtils.isNotBlank(timesAlias), State.getFeatureStateForThread()),
				1);

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
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

			/*
				PhantomJS doesn't support the click method, so "element.click()" won't work
				here. We need to dispatch the event instead.
			 */
			for (int i = 0; i < fixedTimes; ++i) {
				javaScriptRunner.interactHiddenElementMouseEvent(element, "click", js);
				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
			}
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Clicks a link on the page
	 *
	 * @param alias       If this word is found in the step, it means the linkContent is found from the data
	 *                    set.
	 * @param linkContent The text content of the link we are clicking
	 * @param timesAlias    If this word is found in the step, it means the times is found from the
	 *                      data set.
	 * @param times         If this text is set, the click operation will be repeated the specified number of times.
	 * @param exists      If this text is set, an error that would be thrown because the element was not found
	 *                    is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) link with the text content of( alias)? \"([^\"]*)\"(?:( alias)? \".*?\" times)?( if it exists)?$")
	public void clickLinkStep(
		final String alias,
		final String linkContent,
		final String timesAlias,
		final String times,
		final String exists) {

		try {
			final Integer fixedTimes = NumberUtils.toInt(
				autoAliasUtils.getValue(times, StringUtils.isNotBlank(timesAlias), State.getFeatureStateForThread()),
				1);


			final String text = autoAliasUtils.getValue(
				linkContent, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());

			checkState(text != null, "the aliased link content does not exist");

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();

			for (int i = 0; i < fixedTimes; ++i) {
				final WebDriverWait wait = new WebDriverWait(
					webDriver,
					State.getFeatureStateForThread().getDefaultWait(),
					Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
				final WebElement element = wait.until(
					ExpectedConditions.presenceOfElementLocated(By.linkText(text)));
				element.click();
				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
			}
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Clicks a link that may or may not be visible on the page
	 *
	 * @param alias       If this word is found in the step, it means the linkContent is found from the data
	 *                    set.
	 * @param linkContent The text content of the link we are clicking
	 * @param timesAlias    If this word is found in the step, it means the times is found from the
	 *                      data set.
	 * @param times         If this text is set, the click operation will be repeated the specified number of times.
	 * @param exists      If this text is set, an error that would be thrown because the element was not found
	 *                    is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) hidden link with the text content( alias)? of \"([^\"]*)\"(?:( alias)? \".*?\" times)?( if it exists)?$")
	public void clickHiddenLinkStep(
		final String alias,
		final String linkContent,
		final String timesAlias,
		final String times,
		final String exists) {

		try {
			final Integer fixedTimes = NumberUtils.toInt(
				autoAliasUtils.getValue(times, StringUtils.isNotBlank(timesAlias), State.getFeatureStateForThread()),
				1);

			final String text = autoAliasUtils.getValue(
				linkContent, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());

			checkState(text != null, "the aliased link content does not exist");

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();

			for (int i = 0; i < fixedTimes; ++i) {
				final WebDriverWait wait = new WebDriverWait(
					webDriver,
					State.getFeatureStateForThread().getDefaultWait(),
					Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
				final WebElement element = wait.until(
					ExpectedConditions.presenceOfElementLocated(By.linkText(text)));
				final JavascriptExecutor js = (JavascriptExecutor) webDriver;
				js.executeScript("arguments[0].click();", element);
				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
			}
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Clicks on an element with a random number
	 *
	 * @param attributeName      Either ID, class, xpath, name or css selector
	 * @param attributeNameAlias If this word is found in the step, it means the selectorValue is found
	 *                              from the data set.
	 * @param randomStartAlias   If this word is found in the step, it means the randomStart is found from the data
	 *                           set.
	 * @param randomStart        The start of the range of random numbers to select from
	 * @param randomEndAlias     If this word is found in the step, it means the randomEnd is found from
	 *                              the data set.
	 * @param randomEnd          The end of the range of random numbers to select from
	 * @param timesAlias    If this word is found in the step, it means the times is found from the
	 *                      data set.
	 * @param times         If this text is set, the click operation will be repeated the specified number of times.
	 * @param exists             If this text is set, an error that would be thrown because the element
	 *                              was not found is ignored. Essentially setting this text makes this
	 *                              an optional statement.
	 */
	@When("^I click (?:a|an|the) element with (?:a|an|the) attribute( alias)? of \"([^\"]*)\" "
		+ "with a random number between( alias)? \"([^\"]*)\" and( alias)? \"([^\"]*)\""
		+ "(?:( alias)? \".*?\" times)?( if it exists)?$")
	public void clickElementWithRandomNumberStep(
		final String attributeNameAlias,
		final String attributeName,
		final String randomStartAlias,
		final String randomStart,
		final String randomEndAlias,
		final String randomEnd,
		final String timesAlias,
		final String times,
		final String exists) {

		try {
			final Integer fixedTimes = NumberUtils.toInt(
				autoAliasUtils.getValue(times, StringUtils.isNotBlank(timesAlias), State.getFeatureStateForThread()),
				1);

			final String attr = autoAliasUtils.getValue(
				attributeName, StringUtils.isNotBlank(attributeNameAlias), State.getFeatureStateForThread());

			final String startValue = autoAliasUtils.getValue(
				randomStart, StringUtils.isNotBlank(randomStartAlias), State.getFeatureStateForThread());
			final String endValue = autoAliasUtils.getValue(
				randomEnd, StringUtils.isNotBlank(randomEndAlias), State.getFeatureStateForThread());

			final Integer int1 = Integer.parseInt(startValue);
			final Integer int2 = Integer.parseInt(endValue);

			for (int i = 0; i < fixedTimes; ++i) {
				final Integer random = SecureRandom.getInstance("SHA1PRNG").nextInt(
					Math.abs(int2 - int1)) + Math.min(int1, int2);

				final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
				final WebDriverWait wait = new WebDriverWait(
					webDriver,
					State.getFeatureStateForThread().getDefaultWait(),
					Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
				final WebElement element = wait.until(
					ExpectedConditions.elementToBeClickable(
						By.cssSelector("[" + attr + "='" + random + "']")));

				element.click();
				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
			}
		} catch (final TimeoutException ex) {
			if (!" if it exists".equals(exists)) {
				throw ex;
			}
		} catch (final NoSuchAlgorithmException ignored) {
			/*
				This shouldn't happen
			 */
		}
	}

	/**
	 * Clicks an element on the page selected via its attributes
	 *
	 * @param attributeNameAlias  If this word is found in the step, it means the attributeName is found
	 *                               from the data set.
	 * @param attributeName       The name of the attribute to match.
	 * @param attributeValueAlias If this word is found in the step, it means the attributeValue is found
	 *                               from the data set.
	 * @param attributeValue      The value of the attribute to match
	 * @param timesAlias    If this word is found in the step, it means the times is found from the
	 *                      data set.
	 * @param times         If this text is set, the click operation will be repeated the specified number of times.
	 * @param exists              If this text is set, an error that would be thrown because the element
	 *                               was not found is ignored. Essentially setting this text makes this an
	 *                               optional statement.
	 */
	@When("^I click (?:a|an|the) element with (?:a|an|the) attribute( alias)? of \"([^\"]*)\" equal to( alias)? "
		+ "\"([^\"]*)\"(?:( alias)? \".*?\" times)?( if it exists)?$")
	public void clickElementWithAttrStep(
		final String attributeNameAlias,
		final String attributeName,
		final String attributeValueAlias,
		final String attributeValue,
		final String timesAlias,
		final String times,
		final String exists) {

		try {
			final Integer fixedTimes = NumberUtils.toInt(
				autoAliasUtils.getValue(times, StringUtils.isNotBlank(timesAlias), State.getFeatureStateForThread()),
				1);

			final String attr = autoAliasUtils.getValue(
				attributeName, StringUtils.isNotBlank(attributeNameAlias), State.getFeatureStateForThread());

			final String value = autoAliasUtils.getValue(
				attributeValue, StringUtils.isNotBlank(attributeValueAlias), State.getFeatureStateForThread());

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();

			for (int i = 0; i < fixedTimes; ++i) {
				final WebDriverWait wait = new WebDriverWait(
					webDriver,
					State.getFeatureStateForThread().getDefaultWait(),
					Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
				final WebElement element = wait.until(
					ExpectedConditions.elementToBeClickable(
						By.cssSelector("[" + attr + "='" + value + "']")));
				element.click();
				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
			}
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (!" if it exists".equals(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Clicks ok on the alert
	 *
	 * @param timesAlias    If this word is found in the step, it means the times is found from the
	 *                      data set.
	 * @param times         If this text is set, the click operation will be repeated the specified number of times.

	 * @param exists      If this text is set, an error that would be thrown because the element was not found
	 *                    is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click \"OK\" on the alert(?:( alias)? \".*?\" times)?( if it exists)?$")
	public void clickOKOnAlert(
		final String timesAlias,
		final String times,
		final String exists) {

		try {
			final Integer fixedTimes = NumberUtils.toInt(
				autoAliasUtils.getValue(times, StringUtils.isNotBlank(timesAlias), State.getFeatureStateForThread()),
				1);

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();

			for (int i = 0; i < fixedTimes; ++i) {
				browserInteropUtils.acceptAlert(webDriver);
				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
			}
		} catch (final TimeoutException | NoAlertPresentException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Clicks cancel on the alert
	 *
	 * @param timesAlias    If this word is found in the step, it means the times is found from the
	 *                      data set.
	 * @param times         If this text is set, the click operation will be repeated the specified number of times.
	 * @param exists      If this text is set, an error that would be thrown because the element was not found
	 *                    is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click \"Cancel\" on the alert(?:( alias)? \".*?\" times)?( if it exists)?$")
	public void clickCancelOnAlert(
		final String timesAlias,
		final String times,
		final String exists) {

		try {
			final Integer fixedTimes = NumberUtils.toInt(
				autoAliasUtils.getValue(times, StringUtils.isNotBlank(timesAlias), State.getFeatureStateForThread()),
				1);

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();

			for (int i = 0; i < fixedTimes; ++i) {
				browserInteropUtils.cancelAlert(webDriver);
				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
			}
		} catch (final TimeoutException | NoAlertPresentException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Clicks within the area of an element. This is useful for UI widgets like sliders.
	 * @param event The kind of mouse event to trigger
	 * @param xAxis The horizontal percentage within the element area to click
	 * @param yAxis The vertical percentage within the element area to click
	 * @param alias Include this to force the selector to reference an alias
	 * @param selectorValue The selector
	 * @param timesAlias    If this word is found in the step, it means the times is found from the
	 *                      data set.
	 * @param times         If this text is set, the click operation will be repeated the specified number of times.
	 * @param exists Include this to ignore errors caused by missing elements
	 */
	@When("^I \"(click|mousedown|mouseup|mouseover|mouseout|mousemove|dblclick)\" \"(\\d+(?:\\.\\d+)?)%\" horizontally and \"(\\d+(?:\\.\\d+)?)%\" vertically within"
		+ " the area of (?:a|an|the) element found by( alias)? \"([^\"]*)\"(?:( alias)? \".*?\" times)?( if it exists)?$")
	public void clickInElement(
			final String event,
			final Float xAxis,
			final Float yAxis,
			final String alias,
			final String selectorValue,
			final String timesAlias,
			final String times,
			final String exists) {

		try {
			final Integer fixedTimes = NumberUtils.toInt(
				autoAliasUtils.getValue(times, StringUtils.isNotBlank(timesAlias), State.getFeatureStateForThread()),
				1);

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			for (int i = 0; i < fixedTimes; ++i) {
				final WebElement element = simpleWebElementInteraction.getClickableElementFoundBy(
					StringUtils.isNotBlank(alias),
					selectorValue,
					State.getFeatureStateForThread());

				final Double width = Double.parseDouble(js.executeScript("return arguments[0].offsetWidth;", element).toString());
				final Double height = Double.parseDouble(js.executeScript("return arguments[0].offsetHeight;", element).toString());

				final int xOffset = (int) (xAxis / 100.0F * width);
				final int yOffset = (int) (yAxis / 100.0F * height);

			/*
				Not really required, but practice safe programming anyway
			 */
				final String fixedEvent = event.replaceAll("'", "\\'");

				final String script = "var ev = document.createEvent('MouseEvent');\n"
					+ "    ev.initMouseEvent(\n"
					+ "        '" + fixedEvent + "',\n"
					+ "        true /* bubble */, true /* cancelable */,\n"
					+ "        window, null,\n"
					+ "        0, 0, " + xOffset + ", " + yOffset + ", /* coordinates */\n"
					+ "        false, false, false, false, /* modifier keys */\n"
					+ "        0 /*left*/, null\n"
					+ "    );\n"
					+ "    arguments[0].dispatchEvent(ev);";

				js.executeScript(script, element);

				/*
					This is the standard way, but is not supported in a lot of webdrivers (marionette
					is known not to support moveto)
				 */
				//final Actions builder = new Actions(webDriver);
				//builder.moveToElement(element, xOffset, yOffset).click().build().perform();
			}
		} catch (final TimeoutException | NoSuchElementException | WebElementException ex) {
			if (StringUtils.isEmpty(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Opens a link in a new tab
	 *
	 * @param alias       If this word is found in the step, it means the linkContent is found from
	 *                    the data set.
	 * @param linkContent The text content of the link we are clicking
	 * @param timesAlias    If this word is found in the step, it means the times is found from the
	 *                      data set.
	 * @param times         If this text is set, the click operation will be repeated the specified number of times.
	 * @param exists      If this text is set, an error that would be thrown because the element was
	 *                    not found is ignored. Essentially setting this text makes this an optional
	 *                    statement.
	 */
	@When("^I open (?:a|an|the) link with the text content of( alias)? \"([^\"]*)\" in a new window(?:( alias)? \".*?\" times)?( if it exists)?$")
	public void openInNewWindow(
		final String alias,
		final String linkContent,
		final String timesAlias,
		final String times,
		final String exists) {

		try {
			final Integer fixedTimes = NumberUtils.toInt(
				autoAliasUtils.getValue(times, StringUtils.isNotBlank(timesAlias), State.getFeatureStateForThread()),
				1);

			final String text = autoAliasUtils.getValue(
				linkContent, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());

			checkState(text != null, "the aliased link content does not exist");

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();

			for (int i = 0; i < fixedTimes; ++i) {
				final WebDriverWait wait = new WebDriverWait(
					webDriver,
					State.getFeatureStateForThread().getDefaultWait(),
					Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
				final WebElement element = wait.until(
					ExpectedConditions.presenceOfElementLocated(By.linkText(text)));
				final JavascriptExecutor js = JavascriptExecutor.class.cast(webDriver);

				js.executeScript("window.open(arguments[0].getAttribute('href'),'_blank');", element);

				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
			}
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

}
