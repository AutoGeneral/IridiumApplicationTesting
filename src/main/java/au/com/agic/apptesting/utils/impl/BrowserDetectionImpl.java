package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.BrowserDetection;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of the BrowserDetection dervice
 */
@Component
public class BrowserDetectionImpl implements BrowserDetection {

	private static final String EDGE_BROWSER_NAME = "MicrosoftEdge";

	@Override
	public boolean isEdge(@NotNull final WebDriver webDriver) {
		checkNotNull(webDriver);

		final boolean isEdgeBrowser = webDriver instanceof EdgeDriver;
		final boolean isRemoteEdgeBrowser = webDriver instanceof RemoteWebDriver
			&& ((RemoteWebDriver) webDriver).getCapabilities()
			.getBrowserName()
			.equalsIgnoreCase(EDGE_BROWSER_NAME);

		return isEdgeBrowser || isRemoteEdgeBrowser;
	}

	@Override
	public boolean isPhantomJS(@NotNull final WebDriver webDriver) {
		checkNotNull(webDriver);
		/*
			TODO: find out the browser name from a remote driver
		 */
		return webDriver instanceof PhantomJSDriver;
	}

	@Override
	public boolean isOpera(@NotNull final WebDriver webDriver) {
		checkNotNull(webDriver);
		/*
			TODO: find out the browser name from a remote driver
		 */
		return webDriver instanceof OperaDriver;
	}

	@Override
	public boolean isFirefox(@NotNull final WebDriver webDriver) {
		checkNotNull(webDriver);
		/*
			TODO: find out the browser name from a remote driver
		 */
		return webDriver instanceof FirefoxDriver;
	}

	@Override
	public boolean isChrome(WebDriver webDriver) {
		checkNotNull(webDriver);
		/*
			TODO: find out the browser name from a remote driver
		 */
		return webDriver instanceof ChromeDriver;
	}
}
