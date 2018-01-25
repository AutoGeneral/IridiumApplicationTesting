package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.ConfigurationException;
import au.com.agic.apptesting.exception.DriverException;
import au.com.agic.apptesting.profiles.configuration.UrlMapping;
import au.com.agic.apptesting.utils.*;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class RemoteThreadWebDriverMapImpl implements ThreadWebDriverMap {

	/**
	 * The end of the URL we use to connect remotely to browserstack
	 */
	private static final String URL = "@hub.browserstack.com/wd/hub";
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteThreadWebDriverMapImpl.class);
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();
	private static final RemoteTestsUtils REMOTE_TESTS_UTILS = new RemoteTestsUtilsImpl();

	/**
	 * The mapping between thread ids and the webdrivers that they use for the tests
	 */
	private final Map<String, FeatureState> threadIdToCapMap = new HashMap<>();
	/**
	 * The mapping between thread ids and the webdrivers that they use for the tests
	 */
	private final Map<String, WebDriver> threadIdToDriverMap = new HashMap<>();
	/**
	 * The browser stack username loaded from configuration
	 */
	private String browserStackUsername;
	/**
	 * The browser stack access token loaded from configuration
	 */
	private String browserStackAccessToken;
	/**
	 * The index of the data set we are going to be testing
	 */
	private int currentDataset;
	/**
	 * The index of the Url we are going to be testing
	 */
	private int currentUrl;
	/**
	 * The index of the capability we are going to be testing
	 */
	private int currentCapability;
	/**
	 * The original list of configurations
	 */
	private List<DesiredCapabilities> originalDesiredCapabilities;
	/**
	 * The list of URLs associated with the application we are testing
	 */
	private List<UrlMapping> originalApplicationUrls;
	/**
	 * The values that can be input into the app
	 */
	private Map<Integer, Map<String, String>> originalDataSets;

	private String reportDirectory;

	public RemoteThreadWebDriverMapImpl() {
		loadBrowserStackSettings();
	}

	/**
	 * Load the browserstack details from configuration
	 */
	private void loadBrowserStackSettings() {
		final Option<Tuple2<String, String>> credentials = REMOTE_TESTS_UTILS.getCredentials();
		if (credentials.isDefined()) {
			browserStackUsername = credentials.get()._1();
			browserStackAccessToken = credentials.get()._2();
		} else {
			/*
					Log an error because there were no details
				 */
			LOGGER.error("Could not load browserstack config");
		}
	}

	@Override
	public void initialise(
		@NotNull final List<DesiredCapabilities> desiredCapabilities,
		@NotNull final List<UrlMapping> applicationUrls,
		@NotNull final Map<Integer, Map<String, String>> datasets,
		@NotNull final String myReportDirectory,
		@NotNull final List<File> myTempFolders,
		@NotNull final List<ProxyDetails<?>> myProxies) {

		originalDesiredCapabilities = new ArrayList<>(desiredCapabilities);
		originalApplicationUrls = new ArrayList<>(applicationUrls);
		originalDataSets = new HashMap<>(datasets);
		reportDirectory = myReportDirectory;

		/*
			myProxyPort is ignored, because we can't setup proxies when running in browserstack
		 */
	}

	@NotNull
	@Override
	public synchronized FeatureState getDesiredCapabilitiesForThread(@NotNull final String name) {
		try {
			/*
				Return the previous generated details if they exist
			 */
			if (threadIdToCapMap.containsKey(name)) {
				return threadIdToCapMap.get(name);
			}

			/*
				Some validation checking
			 */
			if (originalDesiredCapabilities.isEmpty()) {
				throw new ConfigurationException("There are no desired capabilities defined. "
					+ "Check the configuration profiles have the required information in them");
			}

			/*
				We have allocated our available configurations
			 */
			final int urlCount = Math.max(originalApplicationUrls.size(), 1);
			if (currentUrl >= urlCount) {
				throw new ConfigurationException("Configuration pool has been exhausted!");
			}

			/*
				Get the details that the requesting thread will need
			 */
			final DesiredCapabilities desiredCapabilities =
				originalDesiredCapabilities.get(currentCapability);
			final UrlMapping url = originalApplicationUrls.isEmpty()
				? null : originalApplicationUrls.get(currentUrl);
			final Map<String, String> dataSet = originalDataSets.containsKey(currentDataset)
				? new HashMap<>(originalDataSets.get(currentDataset)) : new HashMap<>();

			/*
				Disable popup blocker
			 */
			desiredCapabilities.setCapability("disable-popup-blocking", true);

			/*
				Tick over to the next url when all the capabilities have been consumed
			 */
			++currentCapability;
			if (currentCapability >= originalDesiredCapabilities.size()) {

				++currentDataset;
				if (currentDataset >= getMaxDataSets()) {
					currentDataset = 0;
					currentCapability = 0;
					++currentUrl;
				}
			}

			/*
				Associate the new details with the thread
			 */
			final String remoteAddress =
				"http://" + browserStackUsername + ":" + browserStackAccessToken + URL;

			final WebDriver webDriver = new RemoteWebDriver(new URL(remoteAddress), desiredCapabilities);

			threadIdToDriverMap.put(name, webDriver);

			final FeatureState featureState = new FeatureStateImpl(
				url, dataSet, reportDirectory, new ArrayList<>());

			threadIdToCapMap.put(name, featureState);

			return featureState;
		} catch (final MalformedURLException ex) {
			/*
				This shouldn't happen
			 */
			throw new ConfigurationException(
				"The url that was built to contact BrowserStack was invalid", ex);
		}
	}

	@NotNull
	public synchronized WebDriver getWebDriverForThread(@NotNull final String name, final boolean createIfMissing) {
		checkArgument(StringUtils.isNotEmpty(name));

		if (threadIdToDriverMap.containsKey(name)) {
			return threadIdToDriverMap.get(name);
		}

		throw new DriverException("Could not find the web driver for the thread " + name);
	}

	@Override
	public synchronized void clearWebDriverForThread(@NotNull final String name, final boolean quitDriver) {
		checkArgument(StringUtils.isNotEmpty(name));

		if (threadIdToDriverMap.containsKey(name)) {
			if (quitDriver) {
				threadIdToDriverMap.get(name).quit();
			}
			threadIdToDriverMap.remove(name);
		}
	}

	@Override
	public synchronized int getNumberCapabilities() {
		/*
			Each application is run against each capability
		 */
		return originalDesiredCapabilities.size()
			* Math.max(1, originalApplicationUrls.size())
			* Math.max(1, getMaxDataSets());
	}

	@Override
	public List<File> getTempFolders() {
		return null;
	}

	private Integer getMaxDataSets() {
		try {
			final String maxDataSets = SYSTEM_PROPERTY_UTILS.getProperty(
				Constants.NUMBER_DATA_SETS_SYSTEM_PROPERTY);

			if (StringUtils.isNotBlank(maxDataSets)) {
				final Integer maxDataSetsNumber = Integer.parseInt(
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.NUMBER_DATA_SETS_SYSTEM_PROPERTY));

				return Math.min(originalDataSets.size(), maxDataSetsNumber);
			}
		} catch (final NumberFormatException ignored) {
			/*
				Invalid input that we ignore
			 */
		}

		return originalDataSets.size();
	}

	@Override
	public synchronized void shutdown() {
		for (final WebDriver webDriver : threadIdToDriverMap.values()) {
			try {
				webDriver.quit();
			} catch (final Exception ignored) {
				// do nothing and continue closing the other webdrivers
			}
		}

		/*
			Clear the map
		 */
		threadIdToCapMap.clear();

		/*
			Reset the list of available configurations
		 */
		currentCapability = 0;
		currentUrl = 0;
	}

	@Override
	public synchronized void shutdown(@NotNull final String name) {
		checkArgument(StringUtils.isNotBlank(name));

		if (threadIdToCapMap.containsKey(name)) {
			threadIdToCapMap.remove(name);
		}

		if (threadIdToDriverMap.containsKey(name)) {
			try {
				threadIdToDriverMap.get(name).quit();
			} catch (final Exception ignored) {
				// do nothing and continue closing the other webdrivers
			}
		}
	}
}
