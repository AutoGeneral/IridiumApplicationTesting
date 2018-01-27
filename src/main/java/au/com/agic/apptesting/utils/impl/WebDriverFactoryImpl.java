package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.drivers.PhantomJSFixedDriver;
import au.com.agic.apptesting.exception.DriverException;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.WebDriverFactory;
import com.google.gson.JsonObject;
import io.vavr.control.Try;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static au.com.agic.apptesting.constants.Constants.PHANTOMJS_LOGGING_LEVEL_SYSTEM_PROPERTY;
import static au.com.agic.apptesting.constants.Constants.PHANTON_JS_USER_AGENT_SYSTEM_PROPERTY;

/**
 * An implementation of the web driver factory
 */
@Component
public class WebDriverFactoryImpl implements WebDriverFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverFactoryImpl.class);
	private static final int PHANTOM_JS_SCREEN_WIDTH = 1280;
	private static final int PHANTOM_JS_SCREEN_HEIGHT = 1024;
	private static final int PHANTOMJS_TIMEOUTS = 60;
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	/**
	 * Note that we exit the application here if the driver could not be created. This is because an exception
	 * during the creation of a driver can leave the binary running in the background with no way to clean it up
	 * (this was observed in the Gecko Driver). To allow long running systems a chance to catch this resource leak,
	 * we return with a specific error code.
	 *
	 * @param proxies   The list of proxies that are used when configuring the web driver
	 * @param tempFiles maintains a list of temp files that are deleted once Iridium is closed
	 * @return The web driver for the given browser
	 */
	@Override
	public WebDriver createWebDriver(
		@NotNull final List<ProxyDetails<?>> proxies,
		@NotNull final List<File> tempFiles) {

		final String browser = SYSTEM_PROPERTY_UTILS.getProperty(
			Constants.TEST_DESTINATION_SYSTEM_PROPERTY);

		/*
			Configure the proxy settings
		 */
		final DesiredCapabilities capabilities = new DesiredCapabilities();

		/*
			Don't worry about ssl issues
		 */
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilities.setCapability("acceptInsecureCerts", true);

		/*
			Don't block popups
		 */
		capabilities.setCapability("disable-popup-blocking", true);

		/*
			Find the proxy that the browser should point to
		 */
		final Optional<ProxyDetails<?>> mainProxy = proxies.stream()
			.filter(ProxyDetails::isMainProxy)
			.findFirst();

		/*
			Add that proxy as a capability for browsers other than Firefox and Marionette.
			There is a bug in the geckodriver that prevents us from using capabilities for
			the proxy: https://github.com/mozilla/geckodriver/issues/669
		 */
		if (!Constants.MARIONETTE.equalsIgnoreCase(browser) && !Constants.FIREFOX.equalsIgnoreCase(browser)) {
			mainProxy
				.map(myMainProxy -> {
					final Proxy proxy = new Proxy();
					proxy.setProxyType(Proxy.ProxyType.MANUAL);
					proxy.setHttpProxy("localhost:" + myMainProxy.getPort());
					proxy.setSslProxy("localhost:" + myMainProxy.getPort());
					proxy.setSocksProxy("localhost:" + myMainProxy.getPort());
					proxy.setFtpProxy("localhost:" + myMainProxy.getPort());
					return proxy;
				})
				.ifPresent(proxy -> capabilities.setCapability("proxy", proxy));
		}

		if (Constants.MARIONETTE.equalsIgnoreCase(browser)) {
			return buildFirefox(browser, mainProxy, capabilities, false, false);
		}

		if (Constants.FIREFOX.equalsIgnoreCase(browser)) {
			return buildFirefox(browser, mainProxy, capabilities, false, false);
		}

		if (Constants.FIREFOXHEADLESS.equalsIgnoreCase(browser)) {
			return buildFirefox(browser, mainProxy, capabilities, false, true);
		}

		if (Constants.SAFARI.equalsIgnoreCase(browser)) {
			return Try.of(() -> new SafariDriver(capabilities))
				.onFailure(ex -> exitWithError(browser, ex))
				.getOrElseThrow(ex -> new RuntimeException(ex));
		}

		if (Constants.OPERA.equalsIgnoreCase(browser)) {
			return Try.of(() -> capabilities)
				.mapTry(caps -> new OperaDriver(caps))
				.onFailure(ex -> exitWithError(browser, ex))
				.getOrElseThrow(ex -> new RuntimeException(ex));
		}

		if (Constants.IE.equalsIgnoreCase(browser)) {
			return Try.of(() -> capabilities)
				/*
					IE doesn't support this option.

					org.openqa.selenium.SessionNotCreatedException: Unable to match capability set 0: acceptInsecureCerts was 'true',
					but the IE driver does not allow bypassing insecure (self-signed) SSL certificates
					Build info: version: 'unknown', revision: 'unknown', time: 'unknown'
					System info: host: 'DESKTOP-JVNRAAG', ip: '172.19.255.145', os.name: 'Windows 10', os.arch: 'amd64', os.version: '10.0', java.version: '9'
					Driver info: driver.version: InternetExplorerDriver
				 */
				.andThenTry(caps -> caps.setCapability("acceptInsecureCerts", false))
				.mapTry(caps -> new InternetExplorerDriver(capabilities))
				.onFailure(ex -> exitWithError(browser, ex))
				.getOrElseThrow(ex -> new RuntimeException(ex));
		}

		if (Constants.EDGE.equalsIgnoreCase(browser)) {
			return Try.of(() -> new EdgeDriver(capabilities))
				.onFailure(ex -> exitWithError(browser, ex))
				.getOrElseThrow(ex -> new RuntimeException(ex));
		}

		if (Constants.CHROME_HEADLESS.equalsIgnoreCase(browser)) {
			return buildChrome(browser, mainProxy, capabilities, false, true, false);
		}

		if (Constants.CHROME_HEADLESS_SECURE.equalsIgnoreCase(browser)) {
			return buildChrome(browser, mainProxy, capabilities, true, true, false);
		}

		if (Constants.CHROME_SECURE.equalsIgnoreCase(browser)) {
			return buildChrome(browser, mainProxy, capabilities, true, false, false);
		}

		if (Constants.CHROME_FULLSCREEN.equalsIgnoreCase(browser)) {
			return buildChrome(browser, mainProxy, capabilities, false, false, true);
		}

		if (Constants.CHROME_SECURE_FULLSCREEN.equalsIgnoreCase(browser)) {
			return buildChrome(browser, mainProxy, capabilities, true, false, true);
		}

		if (Constants.PHANTOMJS.equalsIgnoreCase(browser)) {
			return buildPhantomJS(browser, capabilities, tempFiles);
		}

		return buildChrome(browser, mainProxy, capabilities, false, false, false);
	}

	private void exitWithError(final String browser, final Throwable ex) {
		LOGGER.error("WEBAPPTESTER-BUG-0010: Failed to create the " + browser + " WebDriver", ex);
		System.exit(Constants.WEB_DRIVER_FAILURE_EXIT_CODE);
	}

	private ChromeOptions buildChromeOptions() {
		final ChromeOptions options = new ChromeOptions();
		final Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("credentials_enable_service", false);
		prefs.put("password_manager_enabled", false);
		options.setExperimentalOption("prefs", prefs);
		return options;
	}

	private void buildSecureChromeOptions(final ChromeOptions options) {
		options.addArguments("disable-file-system");
		options.addArguments("use-file-for-fake-audio-capture");
		options.addArguments("use-file-for-fake-video-capture");
		options.addArguments("use-fake-device-for-media-stream");
		options.addArguments("use-fake-ui-for-media-stream");
		options.addArguments("disable-sync");
		options.addArguments("disable-tab-for-desktop-share");
		options.addArguments("disable-translate");
		options.addArguments("disable-voice-input");
		options.addArguments("disable-volume-adjust-sound");
		options.addArguments("disable-wake-on-wifi");
	}

	private WebDriver buildChrome(
		final String browser,
		final Optional<ProxyDetails<?>> mainProxy,
		final DesiredCapabilities capabilities,
		final boolean secure,
		final boolean headless,
		final boolean fullscreen) {

		/*
			These options are documented at:
			https://developers.google.com/web/updates/2017/04/headless-chrome
		 */
		final ChromeOptions options = buildChromeOptions();
		if (secure) {
			buildSecureChromeOptions(options);
		}

		if (headless) {
			options.addArguments("headless");
			options.addArguments("disable-gpu");
			options.addArguments("no-sandbox");
		}

		if (fullscreen) {
			options.addArguments("kiosk");
		}

		capabilities.setCapability(ChromeOptions.CAPABILITY, options);

		return Try.of(() -> new ChromeDriver(capabilities))
			.onFailure(ex -> exitWithError(browser, ex))
			.getOrElseThrow(ex -> new RuntimeException(ex));
	}

	/**
	 * @return The binary used to run firefox if it was set via the FIREFOX_BINARY system property,
	 * or null if the FIREFOX_BINARY system property was not defined
	 */
	private FirefoxBinary getFirefoxBinary() {
		final String firefoxBinary = SYSTEM_PROPERTY_UTILS.getProperty(Constants.FIREFOX_BINARY);
		if (firefoxBinary != null) {
			return new FirefoxBinary(new File(firefoxBinary));
		}

		return new FirefoxBinary();
	}

	private WebDriver buildFirefox(
		final String browser,
		final Optional<ProxyDetails<?>> mainProxy,
		final DesiredCapabilities capabilities,
		final boolean setProfile,
		final boolean headless) {

		final FirefoxOptions options = new FirefoxOptions().merge(capabilities);

		/*
			https://github.com/mozilla/geckodriver/issues/669
		 */
		mainProxy.ifPresent(proxy -> {
			JsonObject json = new JsonObject();
			json.addProperty("proxyType", "manual");
			json.addProperty("httpProxy", "localhost");
			json.addProperty("httpProxyPort", proxy.getPort());
			json.addProperty("ftpProxy", "localhost");
			json.addProperty("ftpProxyPort", proxy.getPort());
			json.addProperty("sslProxy", "localhost");
			json.addProperty("sslProxyPort", proxy.getPort());
			capabilities.setCapability(CapabilityType.PROXY, json);
		});

		/*
			Override the firefox binary
		 */
		final FirefoxBinary firefoxBinary = getFirefoxBinary();
		if (headless) {
			firefoxBinary.addCommandLineOptions("--headless");
		}
		options.setBinary(firefoxBinary);

		final String firefoxProfile = SYSTEM_PROPERTY_UTILS.getProperty(
			Constants.FIREFOX_PROFILE_SYSTEM_PROPERTY);

		/*
			If we have not specified a profile via the system properties, go ahead
			and create one here.
		 */
		if (setProfile) {
			if (StringUtils.isBlank(firefoxProfile)) {
				final FirefoxProfile profile = new FirefoxProfile();

				/*
					This is required for the CI unit tests to pass with firefox
				 */
				profile.setAcceptUntrustedCertificates(true);

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

				options.setProfile(profile);
			} else {
				final ProfilesIni profileLoader = new ProfilesIni();
				final FirefoxProfile profile = profileLoader.getProfile(firefoxProfile);
				options.setProfile(profile);
			}
		}

		return Try.of(() -> new FirefoxDriver(options))
			.onFailure(ex -> exitWithError(browser, ex))
			.getOrElseThrow(ex -> new RuntimeException(ex));
	}

	private WebDriver buildPhantomJS(
		final String browser,
		final DesiredCapabilities capabilities,
		final List<File> tempFiles) {

		try {
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
				Create a temp file for cookies and local session
			 */
			final Path cookies = Files.createTempFile("phantomjs-cookies", ".txt");
			final Path session = Files.createTempDirectory("phantomjs-session");
			final Path log = Files.createTempFile("phantomjs", ".log");
			tempFiles.add(cookies.toFile());
			tempFiles.add(session.toFile());
			tempFiles.add(log.toFile());

			/*
				We need to ignore ssl errors
				https://vaadin.com/forum#!/thread/9200596
			 */
			final String[] cliArgs = {
				"--ignore-ssl-errors=true",
				"--webdriver-loglevel=" + loggingLevel,
				"--local-storage-path=" + session.toString(),
				"--cookies-file=" + cookies.toString(),
				"--webdriver-logfile=" + log.toString()};
			capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgs);

			/*
				Configure a custom user agent
			 */
			final String userAgent = SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(
				PHANTON_JS_USER_AGENT_SYSTEM_PROPERTY);

			if (StringUtils.isNotBlank(userAgent)) {
				capabilities.setCapability("phantomjs.page.settings.userAgent", userAgent);
			}

			return Try.of(() -> new PhantomJSFixedDriver(capabilities))
				.andThenTry(driver -> {
					/*
						This is required by PhantomJS
						https://github.com/angular/protractor/issues/585
					 */
					driver.manage().window().setSize(
						new Dimension(PHANTOM_JS_SCREEN_WIDTH, PHANTOM_JS_SCREEN_HEIGHT));

					/*
						Give the dev servers a large timeout
					 */
					driver.manage().timeouts()
						.pageLoadTimeout(PHANTOMJS_TIMEOUTS, TimeUnit.SECONDS);
				})
				.onFailure(ex -> exitWithError(browser, ex))
				.getOrElseThrow(ex -> new RuntimeException(ex));
		} catch (final IOException ex) {
			throw new DriverException("Could not create temp folder or file for PhantomJS cookies and session", ex);
		}
	}
}
