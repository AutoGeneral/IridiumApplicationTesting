package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.utils.BrowserInteropUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.stereotype.Component;

import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * Implementation of checks required to maintain compatibility between browsers
 */
@Component
public class BrowserInteropUtilsImpl implements BrowserInteropUtils {

	private static final int SLEEP_TIME = 1000;

	@Override
	public boolean treatElementAsHidden(
		@NotNull final WebDriver webDriver,
		@NotNull final WebElement element,
		@NotNull final JavascriptExecutor js) {
		checkNotNull(webDriver);
		checkNotNull(element);
		checkNotNull(js);

		/*
			Note that we check for the presence of a # in the url, and not just that
			the url starts with the # sign, because we'll always get a absolute
			url when we get the href attribute.
		 */
		final boolean isHiddenElement = webDriver instanceof PhantomJSDriver
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
}

