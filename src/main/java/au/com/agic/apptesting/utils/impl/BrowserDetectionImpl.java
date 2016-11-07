package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.BrowserDetection;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
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
	public boolean isEdge(@NotNull WebDriver webDriver) {
		final boolean isEdgeBrowser = webDriver instanceof EdgeDriver;
		final boolean isRemoteEdgeBrowser = webDriver instanceof RemoteWebDriver &&
			((RemoteWebDriver)webDriver).getCapabilities().getBrowserName().equalsIgnoreCase(EDGE_BROWSER_NAME);

		return isEdgeBrowser || isRemoteEdgeBrowser;
	}
}
