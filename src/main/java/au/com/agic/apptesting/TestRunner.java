package au.com.agic.apptesting;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.FileProfileAccessException;
import au.com.agic.apptesting.exception.RunScriptsException;
import au.com.agic.apptesting.profiles.configuration.UrlMapping;
import au.com.agic.apptesting.utils.*;
import au.com.agic.apptesting.utils.impl.*;
import io.vavr.control.Try;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.threadpool.DefaultThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static au.com.agic.apptesting.constants.Constants.OPEN_REPORT_FILE_SYSTEM_PROPERTY;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Typically Cucumber tests are run as jUnit tests. However, in our configuration we run Cucumber as a standalone
 * application spawned in a thread, and let Cucumber itself save junit report files. <p> In this process, there is
 * little to be gained by running the tests within junit, so we create a standard java command line application.
 */
public class TestRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestRunner.class);
	private static final ExceptionWriter EXCEPTION_WRITER = new ExceptionWriterImpl();
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();
	private static final ApplicationUrlLoader APPLICATION_URL_LOADER = new ApplicationUrlLoaderImpl();
	private static final DesiredCapabilitiesLoader DESIRED_CAPABILITIES_LOADER  = new DesiredCapabilitiesLoaderImpl();
	private static final JUnitReportMerge J_UNIT_REPORT_MERGE = new JUnitReportMergeImpl();

	private static final ScreenCapture SCREEN_CAPTURE = new ScreenCaptureImpl();
	private static final DesktopInteraction DESKTOP_INTERACTION = new DesktopInteractionImpl();
	private static final FileSystemUtils FILE_SYSTEM_UTILS = new FileSystemUtilsImpl();
	private static final WebDriverHandler WEB_DRIVER_HANDLER = new WebDriverHandlerImpl();
	private static final JarDownloader JAR_DOWNLOADER = new JarDownloaderImpl();
	private static final LoggingConfiguration LOGGING_CONFIGURATION = new LogbackConfiguration();
	private static final ProxyManager PROXY_MANAGER = new ProxyManagerImpl();
	private static final String HTML_EXTENSION = ".html";
	private static final CleanupUtils CLEANUP_UTILS = new CleanupUtilsImpl();
	private static final RemoteTestsUtils REMOTE_TESTS_UTILS = new RemoteTestsUtilsImpl();

	private static final TagAnalyser TAG_ANALYSER = new TagAnalyserImpl();

	/**
	 * How long to offset the initial launch of the cucumber tests
	 */
	private static final long DELAY_CONCURRENT_START = 2000;
	private static final long THREAD_COMPLETE_SLEEP = 1000;
	private final DefaultThreadPool threadPool = new DefaultThreadPool(NumberUtils
		.toInt(SYSTEM_PROPERTY_UTILS.getProperty(Constants.NUMBER_THREADS_SYSTEM_PROPERTY), 2));
	/**
	 * Used to count the number of scripts that have completed
	 */
	private int completed = 0;
	/**
	 * Used to count the number of scripts that completed with errors
	 */
	private int failure = 0;

	public int run(@NotNull final List<File> globalTempFiles) {
		checkNotNull(globalTempFiles);

		/*
		  This is the directory that will hold our reports
		*/
		final String reportOutput = FILE_SYSTEM_UTILS.buildReportDirectoryName() + File.separator;

		/*
			(re)initialise the pojos loaded from config files
		 */
		APPLICATION_URL_LOADER.initialise();

		/*
			(re)initialise the pojos loaded from config files
		 */
		DESIRED_CAPABILITIES_LOADER.initialise();

		/*
			Configure the logging
		*/
		LOGGING_CONFIGURATION.configureLogging(reportOutput + "/log.txt");

		/*
			Log the version of the tool for future debugging
		 */
		LOGGING_CONFIGURATION.logVersion();

		/*
			A list of files to clean up one the test is complete
		 */
		final List<File> tempFiles = new ArrayList<>();

		/*
			A collection of proxies configured for the run of this test
		 */
		List<ProxyDetails<?>> proxies = null;

		try {

			JAR_DOWNLOADER.downloadJar(tempFiles);
			SYSTEM_PROPERTY_UTILS.copyDependentSystemProperties();
			WEB_DRIVER_HANDLER.configureWebDriver(globalTempFiles);
			proxies = PROXY_MANAGER.configureProxies(globalTempFiles, tempFiles);
			CLEANUP_UTILS.cleanupOldReports();
			init(reportOutput, tempFiles, proxies);

			/*
				Now run the tests
			 */
			try {
				runScripts(reportOutput, globalTempFiles);
				mergeReports(reportOutput);
			} catch (final FileProfileAccessException ex) {
				LOGGER.error("WEBAPPTESTER-BUG-0003: There was an exception thrown while trying to run"
					+ " the test scripts. This is most likely because of an invalid URL or path to "
					+ " the feature scripts. The details of the error are shown below.", ex);

				throw ex;
			} catch (final Exception ex) {
				LOGGER.error("WEBAPPTESTER-BUG-0004: There was an exception thrown while trying to run"
					+ " the test scripts. The details of the error are shown below.", ex);

				throw ex;
			}
		} catch (final Exception ex) {
			EXCEPTION_WRITER.saveException(reportOutput, ex);
			throw ex;
		} finally {
			threadPool.stop();

			/*
				Clean up temp files
			 */
			tempFiles.forEach(FileUtils::deleteQuietly);

			/*
				Gracefully shutdown the proxies
			 */
			if (proxies != null) {
				PROXY_MANAGER.stopProxies(proxies, reportOutput);
			}
		}

		return failure;
	}

	/**
	 * Initialise the global state of the application
	 */
	private void init(
		final String reportOutput,
		final List<File> tempFiles,
		final List<ProxyDetails<?>> proxies) {
		final String appName =
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.FEATURE_GROUP_SYSTEM_PROPERTY);

		State.initialise();

		State.getThreadDesiredCapabilityMap().initialise(
			DESIRED_CAPABILITIES_LOADER.getCapabilities(),
			APPLICATION_URL_LOADER.getAppUrls(appName),
			APPLICATION_URL_LOADER.getDatasets(),
			reportOutput,
			tempFiles,
			proxies);
	}

	/**
	 * Spawn threads to run Cucumber scripts, and wait until they are all finished
	 */
	@SuppressWarnings("BusyWait")
	private void runScripts(@NotNull final String reportDirectory, @NotNull final List<File> globalTempFiles) {
		checkNotNull(globalTempFiles);
		checkArgument(StringUtils.isNotBlank(reportDirectory));

		final String appName = SYSTEM_PROPERTY_UTILS.getProperty(
			Constants.FEATURE_GROUP_SYSTEM_PROPERTY);

		final String destination = SYSTEM_PROPERTY_UTILS.getProperty(
			Constants.TEST_DESTINATION_SYSTEM_PROPERTY);

		final boolean
			videoCapture = Boolean.parseBoolean(
			SYSTEM_PROPERTY_UTILS.getProperty(
				Constants.VIDEO_CAPTURE_SYSTEM_PROPERTY))
			&& !Constants.REMOTE_TESTS.equalsIgnoreCase(destination)
			&& !Constants.PHANTOMJS.equalsIgnoreCase(SYSTEM_PROPERTY_UTILS.getProperty(
			Constants.TEST_DESTINATION_SYSTEM_PROPERTY));

		File testPath = null;

		try {
			/*
				Start the video capture
			*/
			if (videoCapture) {
				SCREEN_CAPTURE.start(reportDirectory);
			}

			/*
				Select a feature loader
			*/
			final FeatureLoader featureLoader = getFeatureLoader();

			/*
				Get the file system path that holds the feature scripts. This path
				is a generated temp dir, and needs to be cleaned up after the test
				is done.
			*/
			testPath = featureLoader.loadFeatures("", appName);

			/*
				For each combination of browser and url run a test
			*/
			LOGGER.info("Running " + State.getThreadDesiredCapabilityMap().getNumberCapabilities() + " test combinations");
			for (int i = 0; i < State.getThreadDesiredCapabilityMap().getNumberCapabilities(); ++i) {
				/*
					For those first few threads that are execute immediately, add a small offset.
					Obviously this doesn't have any impact as the thread pool is used up,
					but that is fine because in theory new threads will start with some
					kind of offset anyway as the time it takes to process the preceeding
					ones is random enough.
				*/
				try {
					Thread.sleep(i * DELAY_CONCURRENT_START);
				} catch (final Exception ignored) {
					/*
						Ignore this
					 */
				}
				threadPool.invokeLater(new CucumberThread(reportDirectory, testPath.toString()));
			}

			/*
				Wait for the thread to finish
			*/
			while (completed != State.getThreadDesiredCapabilityMap().getNumberCapabilities()) {
				try {
					Thread.sleep(THREAD_COMPLETE_SLEEP);
				} catch (final Exception ignored) {
					/*
						ignored
					 */
				}
			}

			/*
				Doh! Some scripts failed, so print a warning
			*/
			if (failure != 0) {
				LOGGER.error("Some of the cucumber tests failed. Check the logs for more details.");
			}

			LOGGER.info("Report files can be found in {}", reportDirectory);
		} finally {
			State.getThreadDesiredCapabilityMap().shutdown();
			FileUtils.deleteQuietly(testPath);
			SCREEN_CAPTURE.stop();
		}
	}

	private void mergeReports(@NotNull final String reportDirectory) {
		checkArgument(StringUtils.isNotBlank(reportDirectory));

		try {
			final List<String> reports = new ArrayList<>();
			final Iterator<File> iterator =
				FileUtils.iterateFiles(new File(reportDirectory), new String[]{"xml"}, false);
			while (iterator.hasNext()) {
				final File file = iterator.next();
				reports.add(file.getAbsolutePath());
			}
			final Optional<String> mergedReport = J_UNIT_REPORT_MERGE.mergeReports(reports);
			if (mergedReport.isPresent()) {
				FileUtils.write(new File(reportDirectory + Constants.MERGED_REPORT), mergedReport.get());
			}
		} catch (final Exception ex) {
			LOGGER.error("WEBAPPTESTER-BUG-0002: Could not save merged report", ex);
		}
	}

	/**
	 * @return The feature loader that is to be used for the tests
	 */
	private FeatureLoader getFeatureLoader() {
		final String testSourceFilePath =
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.TEST_SOURCE_SYSTEM_PROPERTY);
		if (StringUtils.isBlank(testSourceFilePath)) {
			throw new RunScriptsException("Test sources directory not specified");
		}
		return new LocalPathFeatureLoaderImpl();
	}

	/**
	 * We run each Cucumber test in a new thread. This improves performance, but also allows us to use a different
	 * configuration for each thread, which means we can connect to BrowserStack and test against a different
	 * browser.
	 */
	private class CucumberThread implements Runnable {

		private final String featuresLocation;
		private final String reportDirectory;

		CucumberThread(@NotNull final String reportDirectory, final String featuresLocation) {
			checkArgument(StringUtils.isNotBlank(reportDirectory));

			this.featuresLocation = featuresLocation;
			this.reportDirectory = reportDirectory;
		}

		@Override
		public void run() {
			LOGGER.info("CucumberThread.run()");

			try {
				/*
					Threads might be reused, so the id is shared, but we can set the name to
					something new each time.
				*/
				Thread.currentThread().setName(Constants.THREAD_NAME_PREFIX + Main.THREAD_COUNT.incrementAndGet());

				/*
					Get the details for this thread
				*/
				final FeatureState featureState =
					State.getThreadDesiredCapabilityMap().getDesiredCapabilitiesForThread(
						Thread.currentThread().getName());

				/*
					These are the arguments we'll pass to the cucumber runner
				*/
				final List<String> args = new ArrayList<>();

				args.add("--strict");

				/*
					HTML report is enabled by default
				 */
				if (SYSTEM_PROPERTY_UTILS.getPropertyAsBoolean(Constants.JUNIT_REPORT_FILE, true)) {
					args.add("--plugin");
					args.add("junit:" + reportDirectory + Thread.currentThread().getName() + ".xml");
				}

				/*
					JUnit report is enabled by default
				 */
				if (SYSTEM_PROPERTY_UTILS.getPropertyAsBoolean(Constants.HTML_REPORT_FILE, true)) {
					args.add("--plugin");
					args.add("html:" + reportDirectory + Thread.currentThread().getName() + ".html");
				}

				/*
					JSON report can be manually enabled
				 */
				if (SYSTEM_PROPERTY_UTILS.getPropertyAsBoolean(Constants.JSON_REPORT_FILE, false)) {
					args.add("--plugin");
					args.add("json:" + reportDirectory + Thread.currentThread().getName() + ".json");
				}

				/*
					Text report can be manually enabled
				 */
				if (SYSTEM_PROPERTY_UTILS.getPropertyAsBoolean(Constants.TXT_REPORT_FILE, false)) {
					args.add("--plugin");
					args.add("pretty:" + reportDirectory + Thread.currentThread().getName() + ".txt");
				}

				args.add("--plugin");
				args.add("pretty");

				args.add("--glue");
				args.add("au.com.agic.apptesting.steps");
				args.add("--glue");
				args.add("au.com.agic.apptestingext.steps");

				/*
					See if the dry run system property was set
				 */
				if (SYSTEM_PROPERTY_UTILS.getPropertyAsBoolean(Constants.DRY_RUN, false)) {
					args.add("--dry-run");
				}

				/*
					Set the monochrome output option
				 */
				if (SYSTEM_PROPERTY_UTILS.getPropertyAsBoolean(Constants.MONOCHROME_OUTPUT, true)) {
					args.add("--monochrome");
				}

				addTags(args, featureState);

				args.add(featuresLocation);

				LOGGER.info("Executing Cucumber CLI with arguments {}", args);

				/*
					Specify the glue command line argument so the Cucumber application can find
					the steps definitions.
				*/
				final byte retValue = cucumber.api.cli.Main.run(
					args.toArray(new String[args.size()]),
					Thread.currentThread().getContextClassLoader());

				if (retValue != 0) {
					++failure;
				}

				/*
					Open the report files
				 */
				openReportFiles(reportDirectory);
			} catch (final Exception ex) {
				++failure;
				LOGGER.error("Failed to run Cucumber test", ex);
				EXCEPTION_WRITER.saveException(reportDirectory, ex);
			} finally {
				testFinalActions();

				/*
					Clean up this web driver so we don't hold windows open
				*/
				State.getThreadDesiredCapabilityMap().shutdown(Thread.currentThread().getName());

				++completed;
			}
		}

		private void testFinalActions() {
			/*
				Get the session ID, because this will be null after the state is
				shutdown in the next l
			 */
			REMOTE_TESTS_UTILS.getSessionID()
				.map(sessionId ->
					Try.of(() -> State.getFeatureStateForThread().getReportDirectory())
						.andThenTry(reportDir -> REMOTE_TESTS_UTILS.saveVideoRecording(sessionId, reportDir))
						.onFailure(ex -> LOGGER.error("WEBAPPTESTER-BUG-0011: Failed to save Browserstack video.", ex)));
		}

		/**
		 * Scans the supplied directory for html files, which are then opened
		 */
		private void openReportFiles(@NotNull final String reportDir) {
			final boolean htmlReportEnabled = SYSTEM_PROPERTY_UTILS.getPropertyAsBoolean(Constants.HTML_REPORT_FILE, true);
			final boolean openReportFile = SYSTEM_PROPERTY_UTILS.getPropertyAsBoolean(OPEN_REPORT_FILE_SYSTEM_PROPERTY, false);

			if (htmlReportEnabled && openReportFile) {

				final List<File> files = (List<File>) FileUtils.listFiles(
					new File(reportDir), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

				files.stream()
					.filter(e -> e.getName().endsWith(HTML_EXTENSION))
					.forEach(e -> DESKTOP_INTERACTION.openWebpage(e.toURI()));
			}
		}

		/**
		 * Tags can be defined against the URL, or overridden with a system property
		 */
		private void addTags(
				@NotNull final List<String> args,
				@NotNull final FeatureState featureState) {
			checkNotNull(args);
			checkNotNull(featureState);

			final String tagOverride =
				SYSTEM_PROPERTY_UTILS.getProperty(Constants.TAGS_OVERRIDE_SYSTEM_PROPERTY);

			final String tagSetToUse = StringUtils.isNotBlank(tagOverride)
				? tagOverride
				: featureState.getUrlDetails()
					.map(UrlMapping::getTags)
					.orElse("");

			final List<String> tags = TAG_ANALYSER.convertTagsToList(tagSetToUse);

			/*
				@tag1,@tag2;@tag3

				means (@tag1 or @tag2) and @tag3

				which maps to (https://github.com/cucumber/cucumber/wiki/Tags)

				--tags @tag1,@tag2 --tags @tag3
			*/

			for (final String tag : tags) {
				args.add("--tags");
				args.add(tag);
			}
		}
	}
}
