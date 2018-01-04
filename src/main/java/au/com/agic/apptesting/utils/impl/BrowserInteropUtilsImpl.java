package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.*;
import cucumber.api.java.Before;
import io.vavr.control.Try;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of checks required to maintain compatibility between browsers
 */
@Component
public class BrowserInteropUtilsImpl implements BrowserInteropUtils {

	private static final int SLEEP_TIME = 1000;
	private static final Logger LOGGER = LoggerFactory.getLogger(BrowserInteropUtilsImpl.class);

	@Autowired
	private BrowserDetection browserDetection;

	@Autowired
	private SystemPropertyUtils systemPropertyUtils;

	@Autowired
	private RetryService retryService;

	@Autowired
	private SleepUtils sleepUtils;

	private boolean disableInterop() {
		return systemPropertyUtils.getPropertyAsBoolean(Constants.DISABLE_INTEROP, false);
	}

	@Override
	public boolean treatElementAsHidden(
		@NotNull final WebDriver webDriver,
		@NotNull final WebElement element,
		@NotNull final JavascriptExecutor js) {
		checkNotNull(webDriver);
		checkNotNull(element);
		checkNotNull(js);

		if (disableInterop()) {
			return false;
		}

		/*
			Note that we check for the presence of a # in the url, and not just that
			the url starts with the # sign, because we'll always get a absolute
			url when we get the href attribute.
		 */
		final boolean isHiddenElement = browserDetection.isPhantomJS(webDriver)
			&& Optional.of(element)
			.filter(e -> "a".equalsIgnoreCase(e.getTagName()))
			.filter(e -> StringUtils.isNotBlank(e.getAttribute("href")))
			.map(e -> StringUtils.trim(e.getAttribute("href")))
			.filter(e -> e.contains("#") || e.startsWith("javascript:"))
			.isPresent();

		/*
			I have noticed that these elements need some additional time before
			they can be clicked.
		 */
		if (isHiddenElement) {
			Try.run(() -> Thread.sleep(SLEEP_TIME));
		}

		return isHiddenElement;
	}

	@Override
	public void selectFromDropDownList(
		@NotNull final WebDriver webDriver,
		@NotNull final WebElement element,
		@NotNull final String selectElement) {

		checkNotNull(webDriver);
		checkNotNull(element);
		checkNotNull(selectElement);

		final boolean isEdge = browserDetection.isEdge(webDriver);
		final boolean isFirefox = browserDetection.isFirefox(webDriver);

		if (!disableInterop() && (isEdge || isFirefox)) {
			LOGGER.info("WEBAPPTESTER-INFO-0010: Detected Edge or Firefox Marionette driver. "
				+ "Applying drop down list selection workaround.");
			/*
				Edge doesn't trigger the change event using the selectByVisibleText() method.
				Firefox Marionette doesn't select anything at all.
				So select the item in javascript and manually trigger the onchange event.
			 */

			final JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript(
				"for (var i = 0; i < arguments[0].options.length; i++) {"
					+ "    if (arguments[0].options[i].text === '" + selectElement.replaceAll("'", "\\'") + "') {"
					+ "        arguments[0].selectedIndex = i;"
					+ "        break;"
					+ "    }"
					+ "}"
					+ "if ('createEvent' in document) {"
					+ "    var evt = document.createEvent('HTMLEvents');"
					+ "    evt.initEvent('change', true, true);"
					+ "    arguments[0].dispatchEvent(evt);"
					+ "}"
					+ "else {"
					+ "    arguments[0].fireEvent('onchange');"
					+ "}", element);

		} else {
			final Select select = new Select(element);
			select.selectByVisibleText(selectElement);
		}
	}

	@Override
	public void focusOnElement(@NotNull final WebDriver webDriver, @NotNull final WebElement element) {
		checkNotNull(webDriver);
		checkNotNull(element);

		final boolean isFirefox = browserDetection.isFirefox(webDriver);

		final JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("arguments[0].focus();", element);

		if (!disableInterop() && isFirefox) {
			LOGGER.info("WEBAPPTESTER-INFO-0010: Detected Firefox Marionette driver. "
				+ "Applying element focus workaround.");

			js.executeScript(
				"arguments[0].focus();"
					+ "if ('createEvent' in document) {"
					+ "    var evt = document.createEvent('HTMLEvents');"
					+ "    evt.initEvent('focus', true, true);"
					+ "    arguments[0].dispatchEvent(evt);"
					+ "}"
					+ "else {"
					+ "    arguments[0].fireEvent('onfocus');"
					+ "}", element);
		} else {
			js.executeScript("arguments[0].focus();", element);
		}
	}

	@Override
	public WebElement getLinkByText(@NotNull final WebDriver webDriver, @NotNull final String text) {
		checkNotNull(webDriver);
		checkNotNull(text);

		final boolean isFirefox = browserDetection.isFirefox(webDriver);

		if (isFirefox) {
			LOGGER.info("WEBAPPTESTER-INFO-0010: Detected Firefox Marionette driver. "
				+ "Applying find link by text workaround.");

			/*
				Firefox will often fail to find a link by its text content, so we manually
				create the equivalent xpath and find it via JavaScript.
			 */
			final String xpath = "//a[text()[normalize-space(.)='" + text.replaceAll("'", "''") + "']]";
			final String clickLink = "return document.evaluate(\"" + xpath.replace("\"", "\\\"") + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;";
			final WebElement element = (WebElement) ((JavascriptExecutor) webDriver).executeScript(clickLink);
			if (element == null) {
				throw new NoSuchElementException("Cannot locate an element using xpath " + xpath);
			}

			return element;
		} else {
			/*
				Use the standard webdriver api to find the link
			 */
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			return wait.until(
				ExpectedConditions.presenceOfElementLocated(By.linkText(text)));
		}
	}

	@Override
	public void maximizeWindow() {
		final WebDriver webDriver = State.threadDesiredCapabilityMap.getWebDriverForThread();
		final boolean isChrome = browserDetection.isChrome(webDriver);
		final boolean isAndroid = browserDetection.isAndroid(webDriver);
		final boolean isIPad = browserDetection.isIPad(webDriver);
		final boolean isIPhone = browserDetection.isIPhone(webDriver);

		if (!disableInterop()) {
			if (isAndroid || isIPad || isIPhone) {
				LOGGER.info("WEBAPPTESTER-INFO-0010: Detected an Android, iPhone or iPad browser. "
					+ "Maximizing the window on these browsers is not supported.");
			} else if (isChrome) {
				LOGGER.info("WEBAPPTESTER-INFO-0010: Detected Chrome driver."
					+ " Disabling window maximization, due to the bug"
					+ " https://bugs.chromium.org/p/chromedriver/issues/detail?id=1901");
			}
		} else {
			webDriver.manage().window().maximize();
		}
	}

	@Override
	public void setWindowSize(final int width, final int height) {
		final WebDriver webDriver = State.threadDesiredCapabilityMap.getWebDriverForThread();

		final boolean isChrome = browserDetection.isChrome(webDriver);
		final boolean isAndroid = browserDetection.isAndroid(webDriver);
		final boolean isIPad = browserDetection.isIPad(webDriver);
		final boolean isIPhone = browserDetection.isIPhone(webDriver);

		if (!disableInterop()) {
			if (isAndroid || isIPad || isIPhone) {
				LOGGER.info("WEBAPPTESTER-INFO-0010: Detected an Android, iPhone or iPad browser. "
					+ "Setting the window size on these browsers is not supported.");
			} else if (isChrome) {
				/*
					This step will sometimes fail in Chrome, so retry a few times in the event of an error
					because it doesn't matter if we resize a few times.
					https://github.com/SeleniumHQ/selenium/issues/1853
				  */
				final RetryTemplate template = retryService.getRetryTemplate();
				template.execute(context -> {
					webDriver.manage().window().setPosition(new Point(0, 0));
					webDriver.manage().window().setSize(new Dimension(width, height));
					return null;
				});
			}
		} else {
			webDriver.manage().window().setSize(new Dimension(width, height));
		}
	}

	/**
	 * https://github.com/detro/ghostdriver/issues/20
	 * Replace window.alert and window.confirm for PhantomJS
	 */
	@Before
	public void setup() {
		final WebDriver webDriver = State.threadDesiredCapabilityMap.getWebDriverForThread();
		final boolean isPhantomJS = browserDetection.isPhantomJS(webDriver);
		final boolean isOpera = browserDetection.isOpera(webDriver);
		final boolean isFirefox = browserDetection.isFirefox(webDriver);

		if (!disableInterop() && (isPhantomJS || isOpera || isFirefox)) {
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;
			js.executeScript("window.confirm = function(){return true;}");
			js.executeScript("window.alert = function(){}");
		}
	}

	@Override
	public void waitForAlert(@NotNull final WebDriver webDriver, final int waitDuration) {
		checkNotNull(webDriver);

		final boolean isPhantomJS = browserDetection.isPhantomJS(webDriver);
		final boolean isOpera = browserDetection.isOpera(webDriver);
		final boolean isFirefox = browserDetection.isFirefox(webDriver);

		if (!disableInterop() && (isPhantomJS || isOpera || isFirefox)) {
			/*
				This kind of wait is not supported by Phantom JS or Opera
			 */
			LOGGER.info("WEBAPPTESTER-INFO-0010: Detected PhantomJS, Firefox or Opera driver."
				+ " Disabling alert wait. This step will always pass, regardless of"
				+ " whether there is an alert or not.");
		} else {
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				waitDuration,
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);
			wait.until(ExpectedConditions.alertIsPresent());
		}
	}

	@Override
	public void acceptAlert(@NotNull final WebDriver webDriver) {
		checkNotNull(webDriver);

		final boolean isPhantomJS = browserDetection.isPhantomJS(webDriver);
		final boolean isOpera = browserDetection.isOpera(webDriver);
		final boolean isFirefox = browserDetection.isFirefox(webDriver);

		if (!disableInterop() && (isOpera || isPhantomJS || isFirefox)) {
			/*
				Do nothing because we have already redefined the alert
				method.
			 */
			LOGGER.info("WEBAPPTESTER-INFO-0010: Detected PhantomJS, Firefox or Opera driver."
				+ " Disabling alert accept. This step will always pass, regardless of"
				+ " whether there is an alert or not.");
		} else {
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);

			wait.until(ExpectedConditions.alertIsPresent());

			final Alert alert = webDriver.switchTo().alert();
			alert.accept();
		}
	}

	@Override
	public void cancelAlert(@NotNull final WebDriver webDriver) {
		checkNotNull(webDriver);

		final boolean isPhantomJS = browserDetection.isPhantomJS(webDriver);
		final boolean isOpera = browserDetection.isOpera(webDriver);
		final boolean isFirefox = browserDetection.isFirefox(webDriver);

		if (!disableInterop() && (isOpera || isPhantomJS || isFirefox)) {
			/*
				Do nothing because we have already redefined the alert
				method.
			 */
			LOGGER.info("WEBAPPTESTER-INFO-0010: Detected PhantomJS, Firefox or Opera driver."
				+ " Disabling alert dismiss. This step will always pass, regardless of"
				+ " whether there is an alert or not.");
		} else {
			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);

			wait.until(ExpectedConditions.alertIsPresent());

			final Alert alert = webDriver.switchTo().alert();
			alert.dismiss();
		}
	}

	@Override
	public void populateElement(
		@NotNull final WebDriver webDriver,
		@NotNull final WebElement element,
		@NotNull final String value) {

		checkNotNull(webDriver);
		checkNotNull(element);
		checkNotNull(value);

		if (State.getFeatureStateForThread().getDefaultKeyStrokeDelay() == 0) {
			/*
				If there is no delay, just send the text
			 */
			element.sendKeys(value);
		} else {
			/*
				Otherwise delay each keystroke
			 */
			for (final Character character : value.toCharArray()) {
				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultKeyStrokeDelay());
				element.sendKeys(character.toString());
			}
		}
	}

}

