package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.exception.WebElementException;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.webdriver.WebDriverWaitEx;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the SimpleWebElementInteraction service
 */
public class SimpleWebElementInteractionImpl implements SimpleWebElementInteraction {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleWebElementInteractionImpl.class);
	private static final GetBy GET_BY = new GetByImpl();
	/**
	 * Because the web driver is not thread safe, we need to do a running loop over
	 * each of the different element location methods in short time slices to emulate
	 * a parallel search.
     */
	private static final int TIME_SLICE = 100;
	private static final int MILLISECONDS_PER_SECOND = 1000;
	private static final List<String> LOCATION_METHODS = Arrays.asList(
		GetBy.ID,
		GetBy.CLASS,
		GetBy.CSS_SELECTOR,
		GetBy.NAME,
		GetBy.TEXT,
		GetBy.VALUE,
		GetBy.XPATH);

	@Override
	public WebElement getClickableElementFoundBy(
			final boolean valueAlias,
			final String value,
			final FeatureState featureState) {

		return getClickableElementFoundBy(valueAlias, value, featureState, featureState.getDefaultWait());
	}

	@Override
	public WebElement getClickableElementFoundBy(
			final boolean valueAlias,
			final String value,
			final FeatureState featureState,
			final long waitTime) {

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		long time = 0;

		while (time < waitTime * MILLISECONDS_PER_SECOND) {
			for (final String locationMethod : LOCATION_METHODS) {
				try {
					final By by = GET_BY.getBy(locationMethod, valueAlias, value, featureState);
					final WebDriverWaitEx wait = new WebDriverWaitEx(
						webDriver,
						TIME_SLICE,
						TimeUnit.MILLISECONDS);
					final ExpectedCondition<WebElement> condition =
						ExpectedConditions.elementToBeClickable(by);

					final WebElement element = wait.until(condition);
					return element;
				} catch (final Exception ignored) {
					/*
						Do nothing
					 */
				}

				time += TIME_SLICE;
			}
		}

		throw new WebElementException("All attempts to find element failed");
	}

	@Override
	public WebElement getVisibleElementFoundBy(
			final boolean valueAlias,
			final String value,
			final FeatureState featureState) {

		return getVisibleElementFoundBy(valueAlias, value, featureState, featureState.getDefaultWait());
	}

	@Override
	public WebElement getVisibleElementFoundBy(
			final boolean valueAlias,
			final String value,
			final FeatureState featureState,
			final long waitTime) {

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		long time = 0;

		while (time < waitTime * MILLISECONDS_PER_SECOND) {
			for (final String locationMethod : LOCATION_METHODS) {
				try {
					final By by = GET_BY.getBy(locationMethod, valueAlias, value, featureState);
					final WebDriverWaitEx wait = new WebDriverWaitEx(
						webDriver,
						TIME_SLICE,
						TimeUnit.MILLISECONDS);
					final ExpectedCondition<WebElement> condition =
						ExpectedConditions.visibilityOfElementLocated(by);

					final WebElement element = wait.until(condition);
					return element;
				} catch (final Exception ignored) {
					/*
						Do nothing
					 */
				}

				time += TIME_SLICE;
			}
		}

		throw new WebElementException("All attempts to find element failed");
	}

	@Override
	public WebElement getPresenceElementFoundBy(
			final boolean valueAlias,
			final String value,
			final FeatureState featureState) {
		return getPresenceElementFoundBy(valueAlias, value, featureState, featureState.getDefaultWait());
	}

	@Override
	public WebElement getPresenceElementFoundBy(
			final boolean valueAlias,
			final String value,
			final FeatureState featureState,
			final long waitTime) {


		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		long time = 0;

		while (time < waitTime * MILLISECONDS_PER_SECOND) {
			for (final String locationMethod : LOCATION_METHODS) {
				try {
					final By by = GET_BY.getBy(locationMethod, valueAlias, value, featureState);
					final WebDriverWaitEx wait = new WebDriverWaitEx(
						webDriver,
						TIME_SLICE,
						TimeUnit.MILLISECONDS);
					final ExpectedCondition<WebElement> condition =
						ExpectedConditions.presenceOfElementLocated(by);

					final WebElement element = wait.until(condition);
					return element;
				} catch (final Exception ignored) {
					/*
						Do nothing
					 */
				}

				time += TIME_SLICE;
			}
		}

		throw new WebElementException("All attempts to find element failed");
	}
}
