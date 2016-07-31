package au.com.agic.apptesting.utils.impl;

import static au.com.agic.apptesting.constants.Constants.PHANTOMJS_LOGGING_LEVEL_SYSTEM_PROPERTY;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.ConfigurationException;
import au.com.agic.apptesting.profiles.configuration.UrlMapping;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.ThreadDetails;
import au.com.agic.apptesting.utils.ThreadWebDriverMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import javaslang.control.Try;

/**
 * A service that generates local web driver instances to test on the local pc. Assumes that Chrome
 * is present and installed in the default location, and that the webdriver.chrome.driver system
 * property has been set, and is pointing to a version of the driver downloaded from
 * http://chromedriver.storage.googleapis.com/index.html
 */
public class LocalThreadWebDriverMapImpl implements ThreadWebDriverMap {

	private static final int PHANTOM_JS_SCREEN_WIDTH = 1280;
	private static final int PHANTOM_JS_SCREEN_HEIGHT = 1024;
	private static final int PHNATOMJS_TIMEOUTS = 30;
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	/**
	 * The mapping between thread ids and the webdrivers that they use for the tests
	 */
	private final Map<String, ThreadDetails> threadIdToCapMap = new HashMap<>();

	/**
	 * The index of the Url we are going to be testing
	 */
	private int currentUrl;

	/**
	 * The index of the data set we are going to be testing
	 */
	private int currentDataset;

	/**
	 * The list of URLs associated with the application we are testing
	 */
	private List<UrlMapping> originalApplicationUrls;

	/**
	 * The directory that holds reports and other test script outputs
	 */
	private String reportDirectory;

	/**
	 * The values that can be input into the app
	 */
	private Map<Integer, Map<String, String>> originalDataSets;

	/**
	 * A list of temp folders to delete once the test is finished
	 */
	private List<File> tempFolders;

	/**
	 * The port for the proxy
	 */
	private List<ProxyDetails<?>> proxies;

	@Override
	public void initialise(
			@NotNull final List<DesiredCapabilities> desiredCapabilities,
			@NotNull final List<UrlMapping> applicationUrls,
			@NotNull final Map<Integer, Map<String, String>> datasets,
			@NotNull final String myReportDirectory,
			@NotNull final List<File> myTempFolders,
			@NotNull final List<ProxyDetails<?>> myProxies) {

		checkNotNull(desiredCapabilities);
		checkNotNull(applicationUrls);
		checkNotNull(datasets);
		checkNotNull(myReportDirectory);
		checkNotNull(myTempFolders);
		checkNotNull(myProxies);

		originalApplicationUrls = new ArrayList<>(applicationUrls);
		originalDataSets = new HashMap<>(datasets);
		reportDirectory = myReportDirectory;
		tempFolders = new ArrayList<>(myTempFolders);
		proxies = new ArrayList<>(myProxies);
	}

	@Override
	public synchronized ThreadDetails getDesiredCapabilitiesForThread(@NotNull final String name) {
		if (threadIdToCapMap.containsKey(name)) {
			return threadIdToCapMap.get(name);
		}

		/*
		  Some validation checking
		*/
		if (originalApplicationUrls.isEmpty()) {
			throw new ConfigurationException(
				"There are no configurations available. "
				+ "Check configuration profiles have the required information in them");
		}

		/*
		  We have allocated our available configurations
		*/
		if (currentUrl >= originalApplicationUrls.size()) {
			throw new ConfigurationException("Configuration pool has been exhausted!");
		}

		/*
		  Get the details that the requesting thread will need
		*/
		final UrlMapping url = originalApplicationUrls.get(currentUrl);

		final Map<String, String> dataSet = originalDataSets.containsKey(currentDataset)
			? new HashMap<>(originalDataSets.get(currentDataset))
			: new HashMap<>();

		/*
			Tick over to the next url when all the capabilities have been consumed
		 */
		++currentDataset;
		if (currentDataset >= getMaxDataSets()) {
			currentDataset = 0;
			++currentUrl;
		}

		/*
			Associate the new details with the thread
		 */
		final WebDriver remoteWebDriver = getWebDriver();

		final ThreadDetails threadDetails = new ThreadDetailsImpl(
			url,
			dataSet,
			reportDirectory,
			proxies,
			remoteWebDriver);

		threadIdToCapMap.put(name, threadDetails);

		return threadDetails;
	}

	private WebDriver getWebDriver() {
		final String browser = SYSTEM_PROPERTY_UTILS.getProperty(
			Constants.TEST_DESTINATION_SYSTEM_PROPERTY);

		/*
			Configure the proxy settings
		 */
		final DesiredCapabilities capabilities = DesiredCapabilities.htmlUnitWithJs();

		final Optional<ProxyDetails<?>> mainProxy = proxies.stream()
			.filter(ProxyDetails::isMainProxy)
			.findFirst();

		if (mainProxy.isPresent()) {
			Proxy proxy = new Proxy();
			proxy.setHttpProxy("localhost:" + mainProxy.get().getPort());
			capabilities.setCapability("proxy", proxy);
		}

		if (Constants.FIREFOX.equalsIgnoreCase(browser)) {
			final String firefoxProfile = SYSTEM_PROPERTY_UTILS.getProperty(
				Constants.FIREFOX_PROFILE_SYSTEM_PROPERTY);

			/*
				If we have not specified a profile via the system properties, go ahead
				and create one here.
			 */
			if (StringUtils.isBlank(firefoxProfile)) {
				final FirefoxProfile profile = new FirefoxProfile();

				/*
					Set the proxy
				 */
				if (mainProxy.isPresent()) {

					profile.setPreference("network.proxy.type", 1);
					profile.setPreference("network.proxy.http", "localhost");
					profile.setPreference("network.proxy.http_port", mainProxy.get().getPort());
					profile.setPreference("network.proxy.ssl", "localhost");
					profile.setPreference("network.proxy.ssl_port", mainProxy.get().getPort());
					profile.setPreference("network.proxy.no_proxies_on", "");
				}

				return new FirefoxDriver(profile);
			}

			return new FirefoxDriver(capabilities);

		}

		if (Constants.SAFARI.equalsIgnoreCase(browser)) {
			return new SafariDriver(capabilities);
		}

		if (Constants.OPERA.equalsIgnoreCase(browser)) {
			return new OperaDriver(capabilities);
		}

		if (Constants.IE.equalsIgnoreCase(browser)) {
			return new InternetExplorerDriver(capabilities);
		}

		if (Constants.EDGE.equalsIgnoreCase(browser)) {
			return new EdgeDriver(capabilities);
		}

		if (Constants.PHANTOMJS.equalsIgnoreCase(browser)) {

			/*
				PhantomJS will often report a lot of unnecessary errors, so by default
				we turn logging off. But you can override this behaviour with a
				system property.
			 */
			final String loggingLevel = StringUtils.defaultIfBlank(
				SYSTEM_PROPERTY_UTILS.getProperty(PHANTOMJS_LOGGING_LEVEL_SYSTEM_PROPERTY),
				Constants.DEFAULT_PHANTOM_JS_LOGGING_LEVEL
			);

			/*
				We need to ignore ssl errors
				https://vaadin.com/forum#!/thread/9200596
			 */
			final String[] cliArgs = {
				"--ignore-ssl-errors=true",
				"--webdriver-loglevel=" + loggingLevel};
			capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgs);

			final PhantomJSDriver retValue = new PhantomJSDriver(capabilities);

			/*
				This is required by PhantomJS
				https://github.com/angular/protractor/issues/585
			 */
			retValue.manage().window().setSize(
				new Dimension(PHANTOM_JS_SCREEN_WIDTH, PHANTOM_JS_SCREEN_HEIGHT));

			/*
				Give the dev servers a large timeout
			 */
			retValue.manage().timeouts()
				.pageLoadTimeout(PHNATOMJS_TIMEOUTS, TimeUnit.SECONDS)
				.implicitlyWait(PHNATOMJS_TIMEOUTS, TimeUnit.SECONDS);

			return retValue;
		}

		return new ChromeDriver(capabilities);
	}

	@Override
	public synchronized int getNumberCapabilities() {
		if (originalApplicationUrls.isEmpty()) {
			throw new ConfigurationException("No application URL specified");
		}
		return originalApplicationUrls.size() * Math.max(getMaxDataSets(), 1);
	}

	@Override
	public List<File> getTempFolders() {
		return tempFolders;
	}

	private Integer getMaxDataSets() {
		try {
			final String maxDataSets =
				SYSTEM_PROPERTY_UTILS.getProperty(Constants.NUMBER_DATA_SETS_SYSTEM_PROPERTY);

			if (StringUtils.isNotBlank(maxDataSets)) {
				final Integer maxDataSetsNumber = Integer.parseInt(
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.NUMBER_DATA_SETS_SYSTEM_PROPERTY));

				return Math.min(originalDataSets.size(), maxDataSetsNumber);
			}
		} catch (final NumberFormatException ignored) {
		  /*
			Input was not a number, so ignore it
		   */
		}

		return originalDataSets.size();
	}

	@Override
	public synchronized void shutdown() {
		for (final ThreadDetails webdriver : threadIdToCapMap.values()) {
			try {
				if (!leaveWindowsOpen()) {
					webdriver.getWebDriver().quit();
				}
			} catch (final Exception ignored) {
				// do nothing and continue closing the other webdrivers
			}
		}

        /*
            Clear the map
         */
		threadIdToCapMap.clear();

		/*
			Attemp to delete all the temp folders
		 */
		getTempFolders().stream()
			.forEach(e -> Try.run(() -> FileUtils.deleteDirectory(e)));

        /*
            Reset the list of available configurations
         */
		currentUrl = 0;
	}

	@Override
	public synchronized void shutdown(@NotNull final String name) {
		checkArgument(StringUtils.isNotBlank(name));

		if (threadIdToCapMap.containsKey(name)) {
			if (!leaveWindowsOpen()) {
				threadIdToCapMap.get(name).getWebDriver().quit();
			}

			threadIdToCapMap.remove(name);
		}
	}

	private boolean leaveWindowsOpen() {
		final String leaveWindowsOpen =
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.LEAVE_WINDOWS_OPEN_SYSTEM_PROPERTY);
		final String browser =
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.TEST_DESTINATION_SYSTEM_PROPERTY);

		/*
			We can't leave the window open for headless browsers
		 */
		return Boolean.parseBoolean(leaveWindowsOpen)
			&& !Constants.PHANTOMJS.equalsIgnoreCase(browser);
	}
}
