package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.utils.BrowserDetection;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.MarionetteDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

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
		final boolean isRemoteEdgeBrowser = webDriver instanceof RemoteWebDriver &&
			((RemoteWebDriver)webDriver).getCapabilities().getBrowserName().equalsIgnoreCase(EDGE_BROWSER_NAME);

		return isEdgeBrowser || isRemoteEdgeBrowser;
	}

	@Override
	public boolean isPhantomJS(@NotNull final WebDriver webDriver) {
		checkNotNull(webDriver);

		return webDriver instanceof PhantomJSDriver;
	}

	@Override
	public boolean isMarionette(@NotNull WebDriver webDriver) {
		checkNotNull(webDriver);

		return webDriver instanceof MarionetteDriver;
	}

	@Override
	public boolean isFirefox(@NotNull WebDriver webDriver) {
		checkNotNull(webDriver);

		return webDriver instanceof FirefoxDriver;
	}
}
