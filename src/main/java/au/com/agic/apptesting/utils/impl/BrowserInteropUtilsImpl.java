package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.BrowserDetection;
import au.com.agic.apptesting.utils.BrowserInteropUtils;
import au.com.agic.apptesting.utils.SystemPropertyUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import javax.validation.constraints.NotNull;

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

	@Override
	public boolean treatElementAsHidden(
		@NotNull final WebDriver webDriver,
		@NotNull final WebElement element,
		@NotNull final JavascriptExecutor js) {
		checkNotNull(webDriver);
		checkNotNull(element);
		checkNotNull(js);

		if (systemPropertyUtils.getPropertyAsBoolean(Constants.DISABLE_INTEROP, false)) {
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
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException ignored) {
				/*
					Nothing to do here
				 */
			}
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

		final boolean disableInterop = systemPropertyUtils.getPropertyAsBoolean(Constants.DISABLE_INTEROP, false);
		final boolean isEdge = browserDetection.isEdge(webDriver);
		final boolean isMarionette = browserDetection.isMarionette(webDriver);
		final boolean isFirefox = browserDetection.isFirefox(webDriver);

		if (!disableInterop && (isMarionette || isEdge || isFirefox)) {
			LOGGER.info("WEBAPPTESTER-INFO-0010: Detected Edge or Firefox Marionette driver. Applying drop down list selection workaround.");
			/*
				Edge doesn't trigger the change event using the selectByVisibleText() method.
				Firefox Marionette does select anything at all.
				So select the item by pressing the keys in sequence.
			 */
			element.sendKeys(selectElement);
		} else {
			final Select select = new Select(element);
			select.selectByVisibleText(selectElement);
		}
	}

}

