package au.com.agic.apptesting.utils;

import au.com.agic.apptesting.profiles.configuration.UrlMapping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * This class maintains a mapping between a thread id and the Selenium desired capabilities that
 * will be used by the web driver created inside that thread. <p> This is necessary because Cucumber
 * doesn't really support the notion of running tests in multiple threads with multiple
 * configurations (http://www.drobsonconsulting.co.uk/cucumber-and-concurrency/). But this is
 * exactly what we want to do when running against BrowserStack. <p> So this class provides a lookup
 * that is linked to a thread id. This means that when cucumber is creating instances of the Step
 * definitions, the actual configuration of the web driver can be retrieved against a specific
 * thread. In this way we can have multiple threads each creating webdrivers that are consistent for
 * that thread, but different to the other threads.
 */
public interface ThreadWebDriverMap {

	/**
	 * Initialise any internal structures with the list of loaded capabilities
	 *
	 * @param desiredCapabilities the list of capabilities that were loaded
	 * @param applicationUrls     the list of urls assocaited with the application we are testing
	 * @param datasets            the datasets that can be used by a test script
	 * @param myReportDirectory     The directory that holds reports and other test script outputs
	 * @param myTempFolders	      A list of directories that need to be deleted once the test is complete
	 * @param myProxies	  		  A list of the proxies that have been configured
	 */
	void initialise(
		@NotNull List<DesiredCapabilities> desiredCapabilities,
		@NotNull List<UrlMapping> applicationUrls,
		@NotNull Map<Integer, Map<String, String>> datasets,
		@NotNull String myReportDirectory,
		@NotNull List<File> myTempFolders,
		@NotNull List<ProxyDetails<?>> myProxies);

	/**
	 * @param name The name of the currently executing thread
	 * @return The state of the currently executing feature
	 */
	@NotNull
	FeatureState getDesiredCapabilitiesForThread(@NotNull String name);

	/**
	 * @return The web driver and url associated with the current thread
	 */
	@NotNull
	default FeatureState getDesiredCapabilitiesForThread() {
		return getDesiredCapabilitiesForThread(Thread.currentThread().getName());
	}

	/**
	 *
	 * @param name The name of the currently executing thread
	 * @param  createIfMissing set to true to create a web driver if one doesn't exist
	 * @return The web driver and url associated with the thread
     */
	@NotNull
	WebDriver getWebDriverForThread(@NotNull String name, boolean createIfMissing);

	/**
	 * @param  createIfMissing set to true to create a web driver if one doesn't exist
	 * @return The web driver for the current thread
	 */
	@NotNull
	default WebDriver getWebDriverForThread(final boolean createIfMissing) {
		return getWebDriverForThread(Thread.currentThread().getName(), createIfMissing);
	}

	/**
	 *
	 * @return The web driver for the current thread
	 */
	@NotNull
	default WebDriver getWebDriverForThread() {
		return getWebDriverForThread(Thread.currentThread().getName(), true);
	}

	/**
	 * Clears the web driver assigned to a thread.
	 * @param name The name of the thread
	 * @param quitDriver true if the driver should quit before it is cleared
	 */
	void clearWebDriverForThread(@NotNull String name, boolean quitDriver);

	/**
	 *
	 * Clears the web driver assigned to the current thread.
	 * @param quitDriver true if the driver should quit before it is cleared
     */
	default void clearWebDriverForThread(final boolean quitDriver) {
		clearWebDriverForThread(Thread.currentThread().getName(), quitDriver);
	}

	/**
	 * @return The number of threads that should be run in order to conver all the defined
	 * configurations
	 */
	int getNumberCapabilities();

	/**
	 *
	 * @return The list of temporary folders to be cleaned up when shutdown is called
	 */
	List<File> getTempFolders();

	/**
	 * Cleans up the web drivers
	 */
	void shutdown();

	/**
	 * Cleans up the web drivers for an individual thread
	 *
	 * @param name The name of the thread that should have its associated resources cleaned up
	 */
	void shutdown(@NotNull String name);


}
