package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.exception.WebElementException;
import au.com.agic.apptesting.utils.*;
import au.com.agic.apptesting.utils.impl.*;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkState;

/**
 * Gherkin steps used to click elements.
 *
 * These steps have Atom snipptets that start with the prefix "click".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class ClickingStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClickingStepDefinitions.class);
	private static final GetBy GET_BY = new GetByImpl();
	private static final SimpleWebElementInteraction SIMPLE_WEB_ELEMENT_INTERACTION =
		new SimpleWebElementInteractionImpl();
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();
	private static final BrowserInteropUtils BROWSER_INTEROP_UTILS = new BrowserInteropUtilsImpl();
	private static final JavaScriptRunner JAVA_SCRIPT_RUNNER = new JavaScriptRunnerImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final FeatureState featureState =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

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
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) element found by( alias)? \"([^\"]*)\"( if it exists)?$")
	public void clickElementSimpleStep(
		final String alias,
		final String selectorValue,
		final String exists) throws ExecutionException, InterruptedException {
		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);

			/*
				Account for PhantomJS issues clicking certain types of elements
			 */
			final boolean treatAsHiddenElement = BROWSER_INTEROP_UTILS.treatElementAsHidden(
				webDriver, element, js);

			if (treatAsHiddenElement) {
				JAVA_SCRIPT_RUNNER.interactHiddenElementMouseEvent(element, "click", js);
			} else {
				element.click();
			}

			SLEEP_UTILS.sleep(featureState.getDefaultSleep());

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
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) element with (?:a|an|the) (ID|class|xpath|name|css selector)( alias)? "
		+ "of \"([^\"]*)\"( if it exists)?$")
	public void clickElementStep(
		final String selector,
		final String alias,
		final String selectorValue,
		final String exists) {
		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);
			final WebDriverWait wait = new WebDriverWait(webDriver, featureState.getDefaultWait());
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			/*
				Account for PhantomJS issues clicking certain types of elements
			 */
			final boolean treatAsHiddenElement = BROWSER_INTEROP_UTILS.treatElementAsHidden(
				webDriver, element, js);

			if (treatAsHiddenElement) {
				JAVA_SCRIPT_RUNNER.interactHiddenElementMouseEvent(element, "click", js);
			} else {
				element.click();
			}

			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
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
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) hidden element found by( alias)? "
		+ "\"([^\"]*)\"( if it exists)?$")
	public void clickSimpleHiddenElementStep(
		final String alias,
		final String selectorValue,
		final String exists) throws ExecutionException, InterruptedException {

		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);

			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			/*
				PhantomJS doesn't support the click method, so "element.click()" won't work
				here. We need to dispatch the event instead.
			 */
			JAVA_SCRIPT_RUNNER.interactHiddenElementMouseEvent(element, "click", js);
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
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
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) hidden element with (?:a|an|the) (ID|class|xpath|name|css selector)( alias)? "
		+ "of \"([^\"]*)\"( if it exists)?$")
	public void clickHiddenElementStep(
		final String selector,
		final String alias,
		final String selectorValue,
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

			/*
				PhantomJS doesn't support the click method, so "element.click()" won't work
				here. We need to dispatch the event instead.
			 */
			JAVA_SCRIPT_RUNNER.interactHiddenElementMouseEvent(element, "click", js);
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
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
	 * @param exists      If this text is set, an error that would be thrown because the element was not found
	 *                    is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) link with the text content of( alias)? \"([^\"]*)\"( if it exists)?$")
	public void clickLinkStep(
		final String alias,
		final String linkContent,
		final String exists) {

		try {
			final String text = StringUtils.isNotBlank(alias)
				? featureState.getDataSet().get(linkContent) : linkContent;

			checkState(text != null, "the aliased link content does not exist");

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(webDriver, featureState.getDefaultWait());
			final WebElement element = wait.until(
				ExpectedConditions.presenceOfElementLocated(By.linkText(text)));
			element.click();
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
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
	 * @param exists      If this text is set, an error that would be thrown because the element was not found
	 *                    is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) hidden link with the text content( alias)? of \"([^\"]*)\"( if it exists)?$")
	public void clickHiddenLinkStep(
		final String alias,
		final String linkContent,
		final String exists) {

		try {
			final String text = StringUtils.isNotBlank(alias)
				? featureState.getDataSet().get(linkContent) : linkContent;

			checkState(text != null, "the aliased link content does not exist");

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(webDriver, featureState.getDefaultWait());
			final WebElement element = wait.until(
				ExpectedConditions.presenceOfElementLocated(By.linkText(text)));
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript("arguments[0].click();", element);
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
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
	 * @param attributeNameAlias If this word is found in the step, it means the selectorValue is found from the data
	 *                           set.
	 * @param randomStartAlias   If this word is found in the step, it means the randomStart is found from the data
	 *                           set.
	 * @param randomStart        The start of the range of random numbers to select from
	 * @param randomEndAlias     If this word is found in the step, it means the randomEnd is found from the data set.
	 * @param randomEnd          The end of the range of random numbers to select from
	 * @param exists             If this text is set, an error that would be thrown because the element was not found is
	 *                           ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) element with (?:a|an|the) attribute( alias)? of \"([^\"]*)\" "
		+ "with a random number between( alias)? \"([^\"]*)\" and( alias)? \"([^\"]*)\""
		+ "( if it exists)?$")
	public void clickElementWithRandomNumberStep(
		final String attributeNameAlias,
		final String attributeName,
		final String randomStartAlias,
		final String randomStart,
		final String randomEndAlias,
		final String randomEnd,
		final String exists) {

		try {
			final String attr = " alias".equals(attributeNameAlias)
				? featureState.getDataSet().get(attributeName) : attributeName;
			checkState(attr != null, "the aliased attribute name does not exist");

			final String startValue = " alias".equals(randomStartAlias)
				? featureState.getDataSet().get(randomStart) : randomStart;
			final String endValue = " alias".equals(randomEndAlias)
				? featureState.getDataSet().get(randomEnd) : randomEnd;

			checkState(startValue != null, "the aliased start value does not exist");
			checkState(endValue != null, "the aliased end value does not exist");

			final Integer int1 = Integer.parseInt(startValue);
			final Integer int2 = Integer.parseInt(endValue);
			final Integer random = SecureRandom.getInstance("SHA1PRNG").nextInt(
				Math.abs(int2 - int1)) + Math.min(int1, int2);

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(webDriver, featureState.getDefaultWait());
			final WebElement element = wait.until(
				ExpectedConditions.elementToBeClickable(
					By.cssSelector("[" + attr + "='" + random + "']")));

			element.click();
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
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
	 * @param attributeNameAlias  If this word is found in the step, it means the attributeName is found from the data
	 *                            set.
	 * @param attributeName       The name of the attribute to match.
	 * @param attributeValueAlias If this word is found in the step, it means the attributeValue is found from the data
	 *                            set.
	 * @param attributeValue      The value of the attribute to match
	 * @param exists              If this text is set, an error that would be thrown because the element was not found
	 *                            is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I click (?:a|an|the) element with (?:a|an|the) attribute( alias)? of \"([^\"]*)\" equal to( alias)? "
		+ "\"([^\"]*)\"( if it exists)?$")
	public void clickElementWithAttrStep(
		final String attributeNameAlias,
		final String attributeName,
		final String attributeValueAlias,
		final String attributeValue,
		final String exists) {

		try {
			final String attr = " alias".equals(attributeNameAlias)
				? featureState.getDataSet().get(attributeName) : attributeName;
			final String value = " alias".equals(attributeValueAlias)
				? featureState.getDataSet().get(attributeValue) : attributeValue;

			checkState(attr != null, "the aliased attribute name does not exist");
			checkState(value != null, "the aliased attribute value does not exist");

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(webDriver, featureState.getDefaultWait());
			final WebElement element = wait.until(
				ExpectedConditions.elementToBeClickable(
					By.cssSelector("[" + attr + "='" + value + "']")));
			element.click();
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (!" if it exists".equals(exists)) {
				throw ex;
			}
		}
	}
}
