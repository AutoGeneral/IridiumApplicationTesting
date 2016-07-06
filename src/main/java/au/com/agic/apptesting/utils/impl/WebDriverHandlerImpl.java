package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.DriverException;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.WebDriverHandler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A servoce that detects the OS, extracts the drivers and configures then as system properies
 */
public class WebDriverHandlerImpl implements WebDriverHandler {

	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	@Override
	public void configureWebDriver() {
		if (SystemUtils.IS_OS_WINDOWS) {
			configureWindows();
		} else if (SystemUtils.IS_OS_MAC) {
			configureMac();
		} else if (SystemUtils.IS_OS_LINUX) {
			configureLinux();
		}
	}

	private void configureWindows() {
		try {
			final boolean chromeWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (!chromeWebDriverSet) {
				System.setProperty(
					Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver("/drivers/win32/chrome/chromedriver.exe", "chrome.exe"));
			}

			final boolean operaWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (!operaWebDriverSet) {
				System.setProperty(
					Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver("/drivers/win64/opera/operadriver.exe", "opera.exe"));
			}

			final boolean ieWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.IE_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (!operaWebDriverSet) {
				System.setProperty(
					Constants.IE_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver("/drivers/win64/ie/IEDriverServer.exe", "ie.exe"));
			}

			final boolean phantomWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY));

			if (!phantomWebDriverSet) {
				System.setProperty(
					Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY,
					extractDriver("/drivers/win32/phantomjs/phantomjs.exe", "phantomjs.exe"));
			}

		} catch (final Exception ex) {
			throw new DriverException(ex);
		}
	}

	private void configureMac() {
		try {
			final boolean chromeWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (!chromeWebDriverSet) {
				System.setProperty(
					Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver("/drivers/mac32/chrome/chromedriver", "chrome"));
			}

			final boolean operaWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (!operaWebDriverSet) {
				System.setProperty(
					Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver("/drivers/mac64/opera/operadriver", "opera"));
			}

			final boolean phantomWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY));

			if (!phantomWebDriverSet) {
				System.setProperty(
					Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY,
					extractDriver("/drivers/mac64/phantomjs/phantomjs", "phantomjs"));
			}

		} catch (final Exception ex) {
			throw new DriverException(ex);
		}
	}

	private void configureLinux() {
		try {
			final boolean chromeWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (!chromeWebDriverSet) {
				System.setProperty(
					Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver("/drivers/linux64/chrome/chromedriver", "chrome"));
			}

			final boolean operaWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (!operaWebDriverSet) {
				System.setProperty(
					Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver("/drivers/linux64/opera/operadriver", "opera"));
			}

			final boolean phantomWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY));

			if (!phantomWebDriverSet) {
				System.setProperty(
					Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY,
					extractDriver("/drivers/linux64/phantomjs/phantomjs", "phantomjs"));
			}

		} catch (final DriverException ex) {
			throw ex;
		} catch (final Exception ex) {
			throw new DriverException(ex);
		}
	}

	private String extractDriver(@NotNull final String driver, @NotNull final String name) throws IOException {
		checkNotNull(driver);
		checkArgument(StringUtils.isNotBlank(name));

		final InputStream driverURL = getClass().getResourceAsStream(driver);

		/*
			The driver may not be bundled
		 */
		if (driverURL == null) {
			throw new DriverException("The driver resource does not exist.");
		}

		final Path driverTemp = Files.createTempFile("driver", name);
		FileUtils.copyToFile(driverURL, driverTemp.toFile());
		driverTemp.toFile().setExecutable(true);
		return driverTemp.toAbsolutePath().toString();
	}
}
