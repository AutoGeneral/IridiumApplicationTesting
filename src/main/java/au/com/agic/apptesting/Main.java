package au.com.agic.apptesting;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.WebDriverFactory;
import au.com.agic.apptesting.utils.impl.SystemPropertyUtilsImpl;
import au.com.agic.apptesting.utils.impl.WebDriverFactoryImpl;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	private Main() {
	}

	public static void main(final String... args) {
		final List<File> globalTempFiles = new ArrayList<>();

		try {
			/*
				This is required to run ZAP from webstart
			 */
			System.setSecurityManager(null);

			/*
			 	Get the retry count
			 */
			final int retryCount = NumberUtils.toInt(
				SYSTEM_PROPERTY_UTILS.getProperty(Constants.TEST_RETRY_COUNT),
				1
			);

			/*
				Execute the tests
			 */
			int lastFailures = 0;
			for (int retry = 0; retry < retryCount; ++retry) {

				LOGGER.info("WEBAPPTESTER-INFO-0009: Running test attempt {} of {}", retry + 1, retryCount);

				lastFailures = new TestRunner().run(globalTempFiles);

				/*
					Write some output to let the caller know how many failures there were
				 */
				LOGGER.info("WEBAPPTESTER-INFO-0008: TestRunner experienced {} failures", lastFailures);

				if (lastFailures == 0) {
					break;
				}
			}

			System.exit(lastFailures);
		} catch (final Exception ex) {
			LOGGER.error(
				"WEBAPPTESTER-BUG-0007: "
				+ "An exception was raised while attempting to run the Cucumber test scripts", ex);
			System.exit(-1);
		} finally {
			try {
				globalTempFiles.forEach(File::delete);
			} catch (final Exception ex) {
				LOGGER.error(
					"WEBAPPTESTER-BUG-0008: Failed to remove global temp file", ex);
			}
		}
	}
}
