package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.BrowserDetection;
import io.vavr.control.Option;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of the BrowserDetection dervice
 */
@Component
public class BrowserDetectionImpl implements BrowserDetection {

	private static final String EDGE_BROWSER_NAME = "MicrosoftEdge";
	private static final String FIREFOX_BROWSER_NAME = "firefox";
	private static final String CHROME_BROWSER_NAME = "chrome";
	private static final String OPERA_BROWSER_NAME = "opera";
	private static final String SAFARI_BROWSER_NAME = "safari";
	private static final String IPAD_BROWSER_NAME = "iPad";
	private static final String IPHONE_BROWSER_NAME = "iPhone";

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
		return webDriver instanceof PhantomJSDriver;
	}

	@Override
	public boolean isOpera(@NotNull final WebDriver webDriver) {
		checkNotNull(webDriver);

		final boolean isOpera = webDriver instanceof OperaDriver;
		final boolean isRemoteOpera = webDriver instanceof RemoteWebDriver
			&& ((RemoteWebDriver) webDriver).getCapabilities()
			.getBrowserName()
			.equalsIgnoreCase(OPERA_BROWSER_NAME);

		return isOpera || isRemoteOpera;
	}

	@Override
	public boolean isFirefox(@NotNull final WebDriver webDriver) {
		checkNotNull(webDriver);

		final boolean isFirefox = webDriver instanceof FirefoxDriver;
		final boolean isRemoteFirefox = webDriver instanceof RemoteWebDriver
			&& ((RemoteWebDriver) webDriver).getCapabilities()
			.getBrowserName()
			.equalsIgnoreCase(FIREFOX_BROWSER_NAME);

		return isFirefox || isRemoteFirefox;
	}

	@Override
	public boolean isChrome(@NotNull final WebDriver webDriver) {
		checkNotNull(webDriver);

		final boolean isChrome = webDriver instanceof ChromeDriver;
		final boolean isRemotechrome = webDriver instanceof RemoteWebDriver
			&& ((RemoteWebDriver) webDriver).getCapabilities()
			.getBrowserName()
			.equalsIgnoreCase(CHROME_BROWSER_NAME);

		return isChrome || isRemotechrome;
	}

	@Override
	public boolean isSafari(@NotNull final WebDriver webDriver) {
		checkNotNull(webDriver);

		final boolean isSafari = webDriver instanceof SafariDriver;
		final boolean isRemoteSafari = webDriver instanceof RemoteWebDriver
			&& ((RemoteWebDriver) webDriver).getCapabilities()
			.getBrowserName()
			.equalsIgnoreCase(SAFARI_BROWSER_NAME);

		return isSafari || isRemoteSafari;
	}

	@Override
	public boolean isRemote(@NotNull final WebDriver webDriver) {
		checkNotNull(webDriver);
		return webDriver.getClass().equals(RemoteWebDriver.class);
	}

	@Override
	public boolean isAndroid(@NotNull final WebDriver webDriver) {
		final Capabilities remoteWebDriverCapabilities = ((RemoteWebDriver) webDriver).getCapabilities();

		return remoteWebDriverCapabilities.getPlatform().is(Platform.ANDROID)
			// realMobile and Linux means a remote browserstack android device
			|| (remoteWebDriverCapabilities.getPlatform().is(Platform.LINUX)
			&& "true".equals(remoteWebDriverCapabilities.asMap().get("realMobile")));
	}

	@Override
	public boolean isIPad(@NotNull final WebDriver webDriver) {
		return webDriver instanceof RemoteWebDriver
			&& ((RemoteWebDriver) webDriver).getCapabilities()
			.getBrowserName()
			.equalsIgnoreCase(IPAD_BROWSER_NAME);
	}

	@Override
	public boolean isIPhone(@NotNull final WebDriver webDriver) {
		return webDriver instanceof RemoteWebDriver
			&& ((RemoteWebDriver) webDriver).getCapabilities()
			.getBrowserName()
			.equalsIgnoreCase(IPHONE_BROWSER_NAME);
	}
}
