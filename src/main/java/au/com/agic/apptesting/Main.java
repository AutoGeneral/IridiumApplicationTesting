package au.com.agic.apptesting;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.impl.SystemPropertyUtilsImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkState;

public final class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();
	/**
	 * Used to name threads that might be reused
	 */
	public static final AtomicInteger THREAD_COUNT = new AtomicInteger(0);
	private static ScheduledExecutorService terminator;
	private static Future<?> terminatorFuture;

	private Main() {
	}

	public static void main(final String... args) {
		try {
			System.exit(run());
		} catch (final Exception ex) {
			LOGGER.error(
				"WEBAPPTESTER-BUG-0007: "
				+ "An exception was raised while attempting to run the Cucumber test scripts", ex);
			System.exit(-1);
		}
	}

	/**
	 * Run the test script
	 * @return The number of failures returned by the test script
	 */
	public static int run() {
		final List<File> globalTempFiles = new ArrayList<>();

		try {
			/*
				This is required to run ZAP from webstart
			 */
			System.setSecurityManager(null);

			createShutdownTimer();

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

			return lastFailures;
		} finally {
			THREAD_COUNT.set(0);
			globalTempFiles.forEach(FileUtils::deleteQuietly);
			cancelShutdownTimer();
		}
	}

	private static void cancelShutdownTimer() {
		checkState(terminatorFuture != null);

		terminatorFuture.cancel(false);
		terminator.shutdown();

		terminatorFuture = null;
		terminator = null;
	}

	/**
	 * Creates a thread that will shutdown the application after a certain amount of time has passed.
	 */
	private static void createShutdownTimer() {
		checkState(terminatorFuture == null);
		checkState(terminator == null);

		final int maxExecutionTime = NumberUtils.toInt(
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.MAX_EXECUTION_TIME)
		);

		terminator = Executors.newSingleThreadScheduledExecutor();
		terminatorFuture = terminator.schedule(() -> {
			try {
				LOGGER.error(
					"WEBAPPTESTER-INFO-0011: "
						+ "Iridium was shut down because it ran longer than the maximum execution time of " + maxExecutionTime + " milliseconds");
				System.exit(-2);
			} catch (final Exception ex) {
				LOGGER.error(
					"WEBAPPTESTER-BUG-0009: The shutdown timer threw an exception", ex);
			}
		}, maxExecutionTime, TimeUnit.SECONDS);
	}
}
