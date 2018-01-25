package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.DriverException;
import au.com.agic.apptesting.utils.OSDetection;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.WebDriverHandler;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A servoce that detects the OS, extracts the drivers and configures then as system properies
 */
public class WebDriverHandlerImpl implements WebDriverHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverHandlerImpl.class);
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();
	private static final OSDetection OS_DETECTION = new OSDetectionImpl();

	@Override
	public void configureWebDriver(@NotNull final List<File> tempFiles) {
		checkNotNull(tempFiles);

		final boolean useSuppliedWebDrivers =
			SYSTEM_PROPERTY_UTILS.getPropertyAsBoolean(
				Constants.USE_SUPPLIED_WEBDRIVERS, true);

		final boolean is64BitOS = OS_DETECTION.is64BitOS();

		if (SystemUtils.IS_OS_WINDOWS) {
			configureWindows(tempFiles, is64BitOS, useSuppliedWebDrivers);
		} else if (SystemUtils.IS_OS_MAC) {
			configureMac(tempFiles, is64BitOS, useSuppliedWebDrivers);
		} else if (SystemUtils.IS_OS_LINUX) {
			configureLinux(tempFiles, is64BitOS, useSuppliedWebDrivers);
		}

		/*
			Log some details about the diver locations
		 */
		Stream.of(Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
			Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
			Constants.IE_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
			Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY,
			GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY,
			Constants.EDGE_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY)
			.forEach(x -> LOGGER.info(
				"WEBAPPTESTER-INFO-0004: System property {}: {}",
				x,
				System.getProperty(x)));
	}

	private void configureWindows(
		@NotNull final List<File> tempFiles,
		final boolean is64BitOS,
		final boolean useSuppliedWebDrivers) {

		try {
			final boolean marionetteWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY));

			if (useSuppliedWebDrivers && !marionetteWebDriverSet) {
				System.setProperty(
					GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY,
					extractZipDriver(
						"/drivers/win64/marionette/geckodriver.exe.tar.gz",
						"geckodriver.exe",
						tempFiles));
			}

			final boolean chromeWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (useSuppliedWebDrivers && !chromeWebDriverSet) {
				System.setProperty(
					Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver(
						"/drivers/win32/chrome/chromedriver.exe",
						"chrome.exe",
						tempFiles));
			}

			final boolean operaWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (useSuppliedWebDrivers && !operaWebDriverSet) {
				System.setProperty(
					Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver(
						"/drivers/win" + (is64BitOS ? "64" : "32") + "/opera/operadriver.exe",
						"opera.exe",
						tempFiles));
			}

			final boolean edgeWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						Constants.EDGE_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (useSuppliedWebDrivers && !edgeWebDriverSet) {
				System.setProperty(
					Constants.EDGE_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver(
						"/drivers/win32/edge/MicrosoftWebDriver.exe",
						"MicrosoftWebDriver.exe",
						tempFiles));
			}

			final boolean ieWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						Constants.IE_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (useSuppliedWebDrivers && !ieWebDriverSet) {
				System.setProperty(
					Constants.IE_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver(
						"/drivers/win" + (is64BitOS ? "64" : "32") + "/ie/IEDriverServer.exe",
						"ie.exe",
						tempFiles));
			}

			final boolean phantomWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY));

			if (useSuppliedWebDrivers && !phantomWebDriverSet) {
				System.setProperty(
					Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY,
					extractZipDriver(
						"/drivers/win32/phantomjs/phantomjs.exe.tar.gz",
						"phantomjs.exe",
						tempFiles));
			}

		} catch (final Exception ex) {
			throw new DriverException(ex);
		}
	}

	private void configureMac(
		@NotNull final List<File> tempFiles,
		final boolean is64BitOS,
		final boolean useSuppliedWebDrivers) {
		try {
			final boolean marionetteWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY));

			if (useSuppliedWebDrivers && !marionetteWebDriverSet) {
				System.setProperty(
					GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY,
					extractZipDriver(
						"/drivers/mac64/marionette/geckodriver.tar.gz",
						"geckodriver",
						tempFiles));
			}

			final boolean chromeWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (useSuppliedWebDrivers && !chromeWebDriverSet) {
				System.setProperty(
					Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver(
						"/drivers/mac64/chrome/chromedriver",
						"chrome",
						tempFiles));
			}

			final boolean operaWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (useSuppliedWebDrivers && !operaWebDriverSet) {
				System.setProperty(
					Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver(
						"/drivers/mac64/opera/operadriver",
						"opera",
						tempFiles));
			}

			final boolean phantomWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY));

			if (useSuppliedWebDrivers && !phantomWebDriverSet) {
				System.setProperty(
					Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY,
					extractZipDriver(
						"/drivers/mac64/phantomjs/phantomjs.tar.gz",
						"phantomjs",
						tempFiles));
			}

		} catch (final Exception ex) {
			throw new DriverException(ex);
		}
	}

	private void configureLinux(@NotNull final List<File> tempFiles,
								final boolean is64BitOS,
								final boolean useSuppliedWebDrivers) {
		try {
			final boolean marionetteWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY));

			if (useSuppliedWebDrivers && !marionetteWebDriverSet) {
				System.setProperty(
					GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY,
					extractZipDriver(
						"/drivers/linux64/marionette/geckodriver.tar.gz",
						"geckodriver",
						tempFiles));
			}

			final boolean chromeWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (useSuppliedWebDrivers && !chromeWebDriverSet) {
				System.setProperty(
					Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver(
						"/drivers/linux64/chrome/chromedriver",
						"chrome",
						tempFiles));
			}

			final boolean operaWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY));

			if (useSuppliedWebDrivers && !operaWebDriverSet) {
				System.setProperty(
					Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY,
					extractDriver(
						"/drivers/linux64/opera/operadriver",
						"opera",
						tempFiles));
			}

			final boolean phantomWebDriverSet =
				StringUtils.isNotBlank(
					SYSTEM_PROPERTY_UTILS.getProperty(
						Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY));

			if (useSuppliedWebDrivers && !phantomWebDriverSet) {
				System.setProperty(
					Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY,
					extractZipDriver(
						"/drivers/linux64/phantomjs/phantomjs.tar.gz",
						"phantomjs",
						tempFiles));
			}

		} catch (final DriverException ex) {
			throw ex;
		} catch (final Exception ex) {
			throw new DriverException(ex);
		}
	}

	private String extractDriver(
		@NotNull final String driver,
		@NotNull final String name,
		@NotNull final List<File> tempFiles) throws IOException {
		checkNotNull(driver);
		checkArgument(StringUtils.isNotBlank(name));

		final InputStream driverURL = getClass().getResourceAsStream(driver);

		/*
			The driver may not be bundled
		 */
		if (driverURL == null) {
			throw new DriverException("The driver resource does not exist.");
		}

		return copyDriver(driverURL, name, tempFiles);
	}

	private String extractZipDriver(
		@NotNull final String driver,
		@NotNull final String name,
		@NotNull final List<File> tempFiles) throws IOException, CompressorException {
		checkNotNull(driver);
		checkArgument(StringUtils.isNotBlank(name));

		final InputStream driverURL = getClass().getResourceAsStream(driver);

		/*
			The driver may not be bundled
		 */
		if (driverURL == null) {
			throw new DriverException("The driver " + driver + " resource does not exist.");
		}

		final CompressorInputStream input = new CompressorStreamFactory()
			.createCompressorInputStream(CompressorStreamFactory.GZIP, driverURL);

		final TarArchiveInputStream tarInput = new TarArchiveInputStream(input);

		/*
			Sometimes tar files contain a "." directory, which we want to ignore.
			So loop until we get a file that isn't in a directory.
		 */
		TarArchiveEntry tarArchiveEntry = tarInput.getNextTarEntry();
		while (tarArchiveEntry.getName().contains("/")) {
			tarArchiveEntry = tarInput.getNextTarEntry();
		}

		return copyDriver(tarInput, name, tempFiles);
	}

	private String copyDriver(
		@NotNull final InputStream stream,
		@NotNull final String name,
		@NotNull final List<File> tempFiles) throws IOException {
		checkNotNull(stream);
		checkArgument(StringUtils.isNotBlank(name));

		final Path driverTemp = Files.createTempFile("driver", name);
		FileUtils.copyToFile(stream, driverTemp.toFile());
		driverTemp.toFile().setExecutable(true);

		tempFiles.add(driverTemp.toFile());

		final String driverPath = driverTemp.toAbsolutePath().toString();

		LOGGER.info("Saving driver file {} to {}", name, driverPath);

		return driverPath;
	}
}
