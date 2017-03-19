package au.com.agic.apptesting.utils.impl;

import static au.com.agic.apptesting.constants.Constants.PHANTOMJS_LOGGING_LEVEL_SYSTEM_PROPERTY;
import static au.com.agic.apptesting.constants.Constants.PHANTON_JS_USER_AGENT_SYSTEM_PROPERTY;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.DriverException;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.WebDriverFactory;

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
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

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

	@Override
	public WebDriver createWebDriver(
		@NotNull final List<ProxyDetails<?>> proxies,
		@NotNull final List<File> tempFiles) {

		final String browser = SYSTEM_PROPERTY_UTILS.getProperty(
			Constants.TEST_DESTINATION_SYSTEM_PROPERTY);

		/*
			Configure the proxy settings
		 */
		final DesiredCapabilities capabilities = DesiredCapabilities.htmlUnitWithJs();

		/*
			Don't worry about ssl issues
		 */
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

		final Optional<ProxyDetails<?>> mainProxy = proxies.stream()
			.filter(ProxyDetails::isMainProxy)
			.findFirst();

		if (mainProxy.isPresent()) {
			Proxy proxy = new Proxy();
			proxy.setProxyType(Proxy.ProxyType.MANUAL);
			proxy.setHttpProxy("localhost:" + mainProxy.get().getPort());
			proxy.setSslProxy("localhost:" + mainProxy.get().getPort());
			capabilities.setCapability("proxy", proxy);
		}

		if (Constants.FIREFOX.equalsIgnoreCase(browser) || Constants.MARIONETTE.equalsIgnoreCase(browser)) {
			return buildFirefox(mainProxy, capabilities);
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
			return buildPhantomJS(capabilities, tempFiles);
		}

		return buildChrome(mainProxy, capabilities);
	}

	private WebDriver buildChrome(final Optional<ProxyDetails<?>> mainProxy,
		final DesiredCapabilities capabilities) {

		return new ChromeDriver(capabilities);
	}

	private WebDriver buildFirefox(
		final Optional<ProxyDetails<?>> mainProxy,
		final DesiredCapabilities capabilities) {

		final String firefoxProfile = SYSTEM_PROPERTY_UTILS.getProperty(
			Constants.FIREFOX_PROFILE_SYSTEM_PROPERTY);

			/*
				If we have not specified a profile via the system properties, go ahead
				and create one here.
			 */
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

			return new FirefoxDriver(profile);
		}

		return new FirefoxDriver(capabilities);
	}

	private WebDriver buildPhantomJS(final DesiredCapabilities capabilities, final List<File> tempFiles) {
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
			tempFiles.add(cookies.toFile());
			tempFiles.add(session.toFile());

			/*
				We need to ignore ssl errors
				https://vaadin.com/forum#!/thread/9200596
			 */
			final String[] cliArgs = {
				"--ignore-ssl-errors=true",
				"--webdriver-loglevel=" + loggingLevel,
				"--local-storage-path=" + session.toString(),
				"--cookies-file=" + cookies.toString()};
			capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgs);

			/*
				Configure a custom user agent
			 */
			final String userAgent = SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(
				PHANTON_JS_USER_AGENT_SYSTEM_PROPERTY);

			if (StringUtils.isNotBlank(userAgent)) {
				capabilities.setCapability("phantomjs.page.settings.userAgent", userAgent);
			}

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
				.pageLoadTimeout(PHANTOMJS_TIMEOUTS, TimeUnit.SECONDS);

			return retValue;
		} catch (final IOException ex) {
			throw new DriverException("Could not create temp folder or file for PhantomJS cookies and session", ex);
		}
	}
}
