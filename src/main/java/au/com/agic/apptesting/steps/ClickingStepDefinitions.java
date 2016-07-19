package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.WebElementException;
import au.com.agic.apptesting.utils.BrowserInteropUtils;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.ThreadDetails;
import au.com.agic.apptesting.utils.impl.BrowserInteropUtilsImpl;
import au.com.agic.apptesting.utils.impl.GetByImpl;
import au.com.agic.apptesting.utils.impl.SimpleWebElementInteractionImpl;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

import cucumber.api.java.en.When;

/**
 * Gherkin steps used to click elements
 */
public class ClickingStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClickingStepDefinitions.class);
	private static final GetBy GET_BY = new GetByImpl();
	private static final SimpleWebElementInteraction SIMPLE_WEB_ELEMENT_INTERACTION =
		new SimpleWebElementInteractionImpl();
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();
	private static final BrowserInteropUtils BROWSER_INTEROP_UTILS = new BrowserInteropUtilsImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread(
			Thread.currentThread().getName());

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
			final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();

			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails).get();

			/*
				Account for PhantomJS issues clicking certain types of elements
			 */
			final boolean treatAsHiddenElement = BROWSER_INTEROP_UTILS.treatElementAsHidden(
				threadDetails.getWebDriver(), element, js);

			if (treatAsHiddenElement) {
				interactHiddenElement(element, "click", js);
			} else {
				element.click();
			}

			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());

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
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails);
			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
			final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();

			/*
				Account for PhantomJS issues clicking certain types of elements
			 */
			final boolean treatAsHiddenElement = BROWSER_INTEROP_UTILS.treatElementAsHidden(
				threadDetails.getWebDriver(), element, js);

			if (treatAsHiddenElement) {
				interactHiddenElement(element, "click", js);
			} else {
				element.click();
			}

			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
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
			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails).get();

			final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();

			/*
				PhantomJS doesn't support the click method, so "element.click()" won't work
				here. We need to dispatch the event instead.
			 */
			interactHiddenElement(element, "click", js);
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
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
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails);
			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
			final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();

			/*
				PhantomJS doesn't support the click method, so "element.click()" won't work
				here. We need to dispatch the event instead.
			 */
			interactHiddenElement(element, "click", js);
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
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
				? threadDetails.getDataSet().get(linkContent) : linkContent;

			checkState(text != null, "the aliased link content does not exist");

			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(
				ExpectedConditions.presenceOfElementLocated(By.linkText(text)));
			element.click();
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
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
				? threadDetails.getDataSet().get(linkContent) : linkContent;

			checkState(text != null, "the aliased link content does not exist");

			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(
				ExpectedConditions.presenceOfElementLocated(By.linkText(text)));
			final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();
			js.executeScript("arguments[0].click();", element);
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Some applications use mouse events instead of clicks, and PhantomJS will often need us to supply these
	 * events manually. This step uses simple selection.
	 *
	 * @param event         The mouse event we want to generate (mousedown, mouseup etc)
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I \"(.*?)\" on (?:a|an|the) hidden element found by( alias)? \"([^\"]*)\"( if it exists)?$")
	public void mouseEventSimpleHiddenElementStep(
		final String event,
		final String alias,
		final String selectorValue,
		final String exists) throws ExecutionException, InterruptedException {

		try {
			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails).get();
			final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();

			/*
				Just like the click, sometimes we need to trigger mousedown events manually
			 */
			interactHiddenElement(element, event, js);
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Some applications use mouse events instead of clicks, and PhantomJS will often need us to supply these
	 * events manually.
	 *
	 * @param event         The mouse event we want to generate (mousedown, mouseup etc)
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I \"(.*?)\" on (?:a|an|the) hidden element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\"( if it exists)?$")
	public void mouseEventHiddenElementStep(
		final String event,
		final String selector,
		final String alias,
		final String selectorValue,
		final String exists) {

		try {
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				threadDetails);
			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
			final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();

			/*
				Just like the click, sometimes we need to trigger mousedown events manually
			 */
			interactHiddenElement(element, event, js);
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * use JavaScript to simulate a click on an element
	 *
	 * @param element The element to click
	 * @param event   The type of event to simulate
	 * @param js      The JavaScript executor
	 */
	private void interactHiddenElement(
		final WebElement element,
		final String event,
		final JavascriptExecutor js) {
		checkNotNull(element);
		checkNotNull(js);

		/*
			PhantomJS doesn't support the click method, so "element.click()" won't work
			here. We need to dispatch the event instead.
		 */
		js.executeScript("var ev = document.createEvent('MouseEvent');"
			+ "    ev.initMouseEvent("
			+ "        '" + event + "',"
			+ "        true /* bubble */, true /* cancelable */,"
			+ "        window, null,"
			+ "        0, 0, 0, 0, /* coordinates */"
			+ "        false, false, false, false, /* modifier keys */"
			+ "        0 /*left*/, null"
			+ "    );"
			+ "    arguments[0].dispatchEvent(ev);", element);
	}
}
