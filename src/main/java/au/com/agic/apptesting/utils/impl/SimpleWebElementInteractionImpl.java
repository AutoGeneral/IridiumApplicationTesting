package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.WebElementException;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

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
	private static final int MILLIS_IN_SECOND = 1000;
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

		return getClickableElementFoundBy(valueAlias, value, featureState, Constants.WAIT);
	}

	@Override
	public WebElement getClickableElementFoundBy(
			final boolean valueAlias,
			final String value,
			final FeatureState featureState,
			final long waitTime) {

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		long time = 0;

		while (time < waitTime * MILLIS_IN_SECOND) {
			for (final String locationMethod : LOCATION_METHODS) {
				try {
					final By by = GET_BY.getBy(locationMethod, valueAlias, value, featureState);
					final WebDriverWait wait = new WebDriverWait(webDriver, 0, TIME_SLICE);
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

		return getVisibleElementFoundBy(valueAlias, value, featureState, Constants.WAIT);
	}

	@Override
	public WebElement getVisibleElementFoundBy(
			final boolean valueAlias,
			final String value,
			final FeatureState featureState,
			final long waitTime) {

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		long time = 0;

		while (time < waitTime * MILLIS_IN_SECOND) {
			for (final String locationMethod : LOCATION_METHODS) {
				try {
					final By by = GET_BY.getBy(locationMethod, valueAlias, value, featureState);
					final WebDriverWait wait = new WebDriverWait(webDriver, 0, TIME_SLICE);
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
		return getPresenceElementFoundBy(valueAlias, value, featureState, Constants.WAIT);
	}

	@Override
	public WebElement getPresenceElementFoundBy(
			final boolean valueAlias,
			final String value,
			final FeatureState featureState,
			final long waitTime) {


		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		long time = 0;

		while (time < waitTime * MILLIS_IN_SECOND) {
			for (final String locationMethod : LOCATION_METHODS) {
				try {
					final By by = GET_BY.getBy(locationMethod, valueAlias, value, featureState);
					final WebDriverWait wait = new WebDriverWait(webDriver, 0, TIME_SLICE);
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
