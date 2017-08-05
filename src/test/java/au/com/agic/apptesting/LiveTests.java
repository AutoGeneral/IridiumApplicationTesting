package au.com.agic.apptesting;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.impl.SystemPropertyUtilsImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Runs examples as unit tests
 */
public class LiveTests {
	private static final String TEST_BROWSERS_SYSTEM_PROPERTY = "testBrowsers";
	private static final Logger LOGGER = LoggerFactory.getLogger(LiveTests.class);
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();
	private static final int RETRY_COUNT = 3;
	private static final int SLEEP = 60000;
	private final List<File> globalTempFiles = new ArrayList<File>();
	private final List<String> browsers = new ArrayList<String>();

	private File[] getFailureScreenshots() {
		return new File(".").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				if(name.contains(Constants.FAILURE_SCREENSHOT_SUFFIX) && name.endsWith(".png")) {
					LOGGER.info("Found screenshot file file: " + name);
					return true;
				}

				return false;
			}
		});
	}

	private File[] getScreenshots() {
		return new File(".").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				if(name.endsWith(".png")) {
					LOGGER.info("Found screenshot file file: " + name);
					return true;
				}

				return false;
			}
		});
	}

	private File[] getHarFiles() {
		return new File(".").listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				if(name.endsWith(".har")) {
					LOGGER.info("Found HAR file: " + name);
					return true;
				}

				return false;
			}
		});
	}

	/**
	 * Not all environments support all browsers, so we can define the browsers that are tested
	 * via a system property.
	 */
	@Before
	public void getBrowserList() {
		final String browsersSysProp = SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(TEST_BROWSERS_SYSTEM_PROPERTY);
		if (StringUtils.isBlank(browsersSysProp)) {
			browsers.add("ChromeSecure");
			browsers.add("Marionette");
			browsers.add("PhantomJS");
		} else {
			browsers.addAll(Arrays.asList(browsersSysProp.split(",")));
		}
	}

	@After
	public void cleanUpFiles() {
		globalTempFiles.forEach(FileUtils::deleteQuietly);
	}

	@Test(expected=Exception.class)
	public void testInvalidURL() {
		setCommonProperties();
		System.setProperty("testSource", "http://example.org/thisdoesnotexist.feature");
		new TestRunner().run(globalTempFiles);
	}

	/**
	 * Test that a screenshot is taken
	 */
	@Test
	public void testScreenshot() {

		/*
			Clean up any existing files
		 */
		Arrays.stream(getFailureScreenshots()).forEach(FileUtils::deleteQuietly);

		setCommonProperties();
		System.setProperty("testSource", this.getClass().getResource("/screenshot.feature").toString());
		System.setProperty("testDestination", "PhantomJS");
		final int failures = new TestRunner().run(globalTempFiles);

		/*
			We expect the feature to fail
		 */
		Assert.assertTrue(failures == 0);

		/*
			Try and find a failure screenshot
		 */
		Assert.assertTrue(Arrays.stream(getScreenshots()).anyMatch(f -> f.getName().contains("testscreenshot")));
	}

	/**
	 * Test that a screenshot is taken on failure with enableScreenshotOnError set
	 * to true
	 */
	@Test
	public void testScreenshotOnFailure() {

		/*
			Clean up any existing files
		 */
		Arrays.stream(getFailureScreenshots()).forEach(FileUtils::deleteQuietly);

		setCommonProperties();
		System.setProperty("testSource", this.getClass().getResource("/screenshotonfailure.feature").toString());
		System.setProperty("enableScreenshotOnError", "true");
		System.setProperty("testDestination", "PhantomJS");
		final int failures = new TestRunner().run(globalTempFiles);

		/*
			We expect the feature to fail
		 */
		Assert.assertTrue(failures > 0);

		/*
			Try and find a failure screenshot
		 */
		Assert.assertTrue(getFailureScreenshots().length == 1);
	}

	/**
	 * Test that report files are saved in a custom dir when specified
	 */
	@Test
	public void testCustomReportDir() throws IOException {
		final Path tempDir = Files.createTempDirectory("test");

		try {
			setCommonProperties();
			System.setProperty(Constants.REPORTS_DIRECTORY, tempDir.toString());
			System.setProperty("testSource", this.getClass().getResource("/screenshotonfailure.feature").toString());
			System.setProperty("enableScreenshotOnError", "true");
			System.setProperty("testDestination", "PhantomJS");
			new TestRunner().run(globalTempFiles);

			Assert.assertTrue(tempDir.toFile().listFiles().length != 0);
		} finally {
			FileUtils.deleteQuietly(tempDir.toFile());
		}
	}

	/**
	 * https://github.com/AutoGeneral/IridiumApplicationTesting/issues/66
	 * This is a test of loading feature files hosted by a web server, and
	 * importing nested feature files by stripping out the feature lines.
	 */
	@Test
	public void testFeatureImport() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			try {
				setCommonProperties();
				System.setProperty("appURLOverride", "https://mcasperson.github.io/iridium/examples/test.html");
				System.setProperty("testSource", "parent.feature");
				System.setProperty("testDestination", "PhantomJS");
				System.setProperty("importBaseUrl", "https://mcasperson.github.io/iridium/features/");
				final int failures = new TestRunner().run(globalTempFiles);
				if (failures == 0) {
					return;
				}
				Thread.sleep(SLEEP);
			} catch (final Exception ignored) {
				/*
					Ignored
				 */
			}
		}

		Assert.fail("Failed the tests!");
	}

	/**
	 * https://github.com/AutoGeneral/IridiumApplicationTesting/issues/66
	 * This is a test of loading feature files hosted by a web server, and
	 * importing nested feature files by stripping out the feature lines.
	 */
	@Test
	public void testFeatureImport2() {
		setCommonProperties();
		System.setProperty("appURLOverride", "https://mcasperson.github.io/iridium/examples/test.html");
		System.setProperty("testSource", "parent-fragment.feature");
		System.setProperty("testDestination", "PhantomJS");
		System.setProperty("importBaseUrl", "https://mcasperson.github.io/iridium/features/");
		final int failures = new TestRunner().run(globalTempFiles);
		Assert.assertEquals(failures, 0);
	}

	/**
	 * This test runs a feature that uses a wide selection of the steps provided
	 * with Iridium, and serves as a good regression test.
	 */
	@Test
	public void stepTests() {
		browserLoop: for (final String browser : browsers) {
			for (int retry = 0; retry < RETRY_COUNT; ++retry) {
				try {
					setCommonProperties();

					/*
						Clean up the HAR files
					 */
					Arrays.stream(getHarFiles()).forEach(FileUtils::deleteQuietly);

					System.setProperty("appURLOverride", "https://mcasperson.github.io/iridium/examples/test.html");
					System.setProperty("testSource", this.getClass().getResource("/steptest.feature").toString());
					System.setProperty("testDestination", browser);
					System.setProperty("configuration", this.getClass().getResource("/config.xml").toString());
					System.setProperty("browserStackUsername", System.getenv("browserStackUsername"));
					System.setProperty("browserStackAccessToken", System.getenv("browserStackAccessToken"));
					System.setProperty("tagsOverride", "@tag1,@tag2,@tag3,@tag5,@test;~@tag4,@test");
					final int failures = new TestRunner().run(globalTempFiles);

					/*
						We always expect to find the browsermob<date>.har file, regardless of the
						success or failure of the test.
		 			*/
					LOGGER.info("Testing for har file presence");
					Assert.assertTrue(getHarFiles().length != 0);
					Assert.assertTrue(Stream.of(getHarFiles())
						.anyMatch(file -> file.getName().matches(
						Constants.HAR_FILE_NAME_PREFIX
							+ "\\d{17}\\."
							+ Constants.HAR_FILE_NAME_EXTENSION)));

					if (failures == 0) {
						/*
							We expect to have a manually dumped har file
						 */
						Assert.assertTrue(Stream.of(getHarFiles())
							.anyMatch(file -> file.getName().matches("test\\d{17}\\.har")));

						continue browserLoop;
					}
					Thread.sleep(SLEEP);
				} catch (final Exception ignored) {
					/*
						Ignored
					 */
				}
			}

			Assert.fail("Browser " + browser + " failed the tests!");
		}
	}

	/**
	 * This tests a scenario that we know must fail
	 */
	@Test
	public void failWhenClosingOnlyWindow() {
		/*
			Firefox actually allows you to close the final window, so we don't test
			it here.
		 */
		final List<String> closeFailBrowsers = browsers.stream()
				.filter(browser -> !"Marionette".equalsIgnoreCase(browser))
				.collect(Collectors.toList());

		for (final String browser : closeFailBrowsers) {
			setCommonProperties();
			System.setProperty("appURLOverride", "https://mcasperson.github.io/iridium/examples/test.html");
			System.setProperty("testSource", this.getClass().getResource("/steptest.feature").toString());
			System.setProperty("testDestination", browser);
			System.setProperty("tagsOverride", "@fail-with-one-window");
			final int failures = new TestRunner().run(globalTempFiles);
			Assert.assertEquals(1, failures);
		}
	}

	/**
	 * This test does a dry run, which run each step and immediately returns.
	 * We don't set any tag overrides, which would normally cause the test to
	 * fail, but the dry run will never fail any steps.
	 */
	@Test
	public void dryRun() throws InterruptedException {

		setCommonProperties();
		System.setProperty("appURLOverride", "https://mcasperson.github.io/iridium/examples/test.html");
		System.setProperty("testSource", this.getClass().getResource("/steptest.feature").toString());
		System.setProperty("testDestination", "PhantomJS");
		System.setProperty("dryRun", "true");
		final int failures = new TestRunner().run(globalTempFiles);
		Assert.assertEquals(failures, 0);
	}

	/**
	 * This test is designed to simulate the starting and restarting of the ZAP proxy
	 */
	@Test
	public void restartZap() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			setCommonProperties();
			System.setProperty("appURLOverride", "https://example.org");
			System.setProperty("testSource", this.getClass().getResource("/failtestwithzap.feature").toString());
			System.setProperty("testDestination", "PhantomJS");
			System.setProperty("startInternalProxy", "zap");
			final int failures = new TestRunner().run(globalTempFiles);
			if (failures == 0) {
				return;
			}
		}
	}

	@Test
	@Ignore("These tests are better run individually in a CI system")
	public void modifyRequests19() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			try {
				setCommonProperties();
				System.setProperty("appURLOverride", "https://dzone.com");
				System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/19.modifyrequests/test.feature");
				System.setProperty("testDestination", "PhantomJS");
				final int failures = new TestRunner().run(globalTempFiles);
				if (failures == 0) {
					return;
				}
				Thread.sleep(SLEEP);
			} catch (final Exception ignored) {
				/*
					Ignored
				 */
			}
		}

		Assert.fail();
	}

	@Test
	@Ignore("These tests are better run individually in a CI system")
	public void passiveSecurity20() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			try {
				setCommonProperties();
				System.setProperty("appURLOverride", "https://dzone.com");
				System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/20.passivesecurity/test-nofail.feature");
				System.setProperty("testDestination", "PhantomJS");
				System.setProperty("startInternalProxy", "zap");
				final int failures = new TestRunner().run(globalTempFiles);
				if (failures == 0) {
					return;
				}
				Thread.sleep(SLEEP);
			} catch (final Exception ignored) {
				/*
					Ignored
				 */
			}
		}

		Assert.fail();
	}

	@Test
	@Ignore("These tests are better run individually in a CI system")
	public void gherkinExamples21() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			try {
				setCommonProperties();
				System.setProperty("appURLOverride", "https://dzone.com");
				System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/21.gherkinexamples/test.feature");
				System.setProperty("testDestination", "PhantomJS");
				final int failures = new TestRunner().run(globalTempFiles);
				if (failures == 0) {
					return;
				}
				Thread.sleep(SLEEP);
			} catch (final Exception ignored) {
				/*
					Ignored
				 */
			}
		}

		Assert.fail();
	}

	@Test
	@Ignore("These tests are better run individually in a CI system")
	public void deadLinkCheck23() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			try {
				setCommonProperties();
				System.setProperty("appURLOverride", "https://autogeneral.gitbooks.io/iridiumapplicationtesting-gettingstartedguide/content/tips_and_tricks.html");
				System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/23.deadlinkcheck/test-nofail.feature");
				System.setProperty("testDestination", "PhantomJS");
				final int failures = new TestRunner().run(globalTempFiles);
				if (failures == 0) {
					return;
				}
				Thread.sleep(SLEEP);
			} catch (final Exception ignored) {
				/*
					Ignored
				 */
			}
		}

		Assert.fail();
	}

	@Test
	@Ignore("These tests are better run individually in a CI system")
	public void acceptanceTest24() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			try {
				setCommonProperties();
				System.setProperty("appURLOverride", "https://mcasperson.github.io/iridium/examples/app.html");
				System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/24.acceptancetests/test-populated.feature");
				System.setProperty("testDestination", "PhantomJS");
				final int failures = new TestRunner().run(globalTempFiles);
				if (failures == 0) {
					return;
				}
				Thread.sleep(SLEEP);
			} catch (final Exception ignored) {
				/*
					Ignored
				 */
			}
		}

		Assert.fail();
	}

	@Test
	@Ignore("These tests are better run individually in a CI system")
	public void driverPerScenario25() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			try {
				setCommonProperties();
				System.setProperty("appURLOverride", "https://dzone.com");
				System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/25.driverperscenario/test.feature");
				System.setProperty("testDestination", "PhantomJS");
				final int failures = new TestRunner().run(globalTempFiles);
				if (failures == 0) {
					return;
				}
				Thread.sleep(SLEEP);
			} catch (final Exception ignored) {
				/*
					Ignored
				 */
			}
		}

		Assert.fail();
	}

	@Test
	public void negativeClickTests() {
		final String feature = "/negativeclicktests.feature";
		final String tagPrefix = "@neg-click-";
		runNegativeTest(feature, tagPrefix);
	}

	@Test
	public void negativeEventTests() {
		final String feature = "/negativeeventtests.feature";
		final String tagPrefix = "@neg-event-";
		runNegativeTest(feature, tagPrefix);
	}

	@Test
	public void negativeExtractTests() {
		final String feature = "/negativeextracttests.feature";
		final String tagPrefix = "@neg-extract-";
		runNegativeTest(feature, tagPrefix);
	}

	@Test
	public void negativeDropDownTests() {
		final String feature = "/negativedropdowntests.feature";
		final String tagPrefix = "@neg-dropdown-";
		runNegativeTest(feature, tagPrefix);
	}

	@Test
	public void negativeFocusTests() {
		final String feature = "/negativefocustests.feature";
		final String tagPrefix = "@neg-focus-";
		runNegativeTest(feature, tagPrefix);
	}

	@Test
	public void negativeOpenTests() {
		final String feature = "/negativeopentests.feature";
		final String tagPrefix = "@neg-open-";
		runNegativeTest(feature, tagPrefix);
	}

	@Test
	public void negativeTabTests() {
		final String feature = "/negativetabtests.feature";
		final String tagPrefix = "@neg-tab-";
		runNegativeTest(feature, tagPrefix);
	}

	@Test
	public void negativePopulateTests() {
		final String feature = "/negativepopulatetests.feature";
		final String tagPrefix = "@neg-populate-";
		runNegativeTest(feature, tagPrefix);
	}

	@Test
	public void negativeVerifyTests() {
		final String feature = "/negativevalidationtests.feature";
		final String tagPrefix = "@neg-verify-";
		runNegativeTest(feature, tagPrefix);
	}

	@Test
	public void negativeWaitTests() {
		final String feature = "/negativewaittests.feature";
		final String tagPrefix = "@neg-wait-";
		runNegativeTest(feature, tagPrefix);
	}

	@Test
	public void runXmlDatasetTest() {
			setCommonProperties();
			System.setProperty("testSource", this.getClass().getResource("/datasettest.feature").toString());
			System.setProperty("dataset", this.getClass().getResource("/dataset.xml").toString());
			System.setProperty("configuration", this.getClass().getResource("/config.xml").toString());
			System.setProperty("featureGroupName", "Google");
			System.setProperty("testDestination", "PhantomJS");
			final int failures = new TestRunner().run(globalTempFiles);
			Assert.assertEquals(0, failures);
	}

	@Test
	public void runCsvDatasetTest() {
		setCommonProperties();
		System.setProperty("testSource", this.getClass().getResource("/datasettest.feature").toString());
		System.setProperty("dataset", this.getClass().getResource("/dataset.csv").toString());
		System.setProperty("configuration", this.getClass().getResource("/config.xml").toString());
		System.setProperty("featureGroupName", "Google");
		System.setProperty("testDestination", "PhantomJS");
		final int failures = new TestRunner().run(globalTempFiles);
		Assert.assertEquals(0, failures);
	}

	private void runNegativeTest(final String feature, final String tagPrefix) {
		final int maxTags = findHighestTag(feature, tagPrefix);
		for (int i = 1; i <= maxTags; ++i) {
			setCommonProperties();
			System.setProperty("testSource", this.getClass().getResource(feature).toString());
			System.setProperty("testDestination", "PhantomJS");
			System.setProperty("tagsOverride", tagPrefix + i);
			final int failures = new TestRunner().run(globalTempFiles);
			Assert.assertEquals(1, failures);
		}
	}

	private void setCommonProperties() {
		System.setProperty(Constants.REPORTS_DIRECTORY, "");
		System.setProperty("webdriver.chrome.driver", "");
		System.setProperty("webdriver.opera.driver", "");
		System.setProperty("webdriver.ie.driver", "");
		System.setProperty("webdriver.gecko.driver", "");
		System.setProperty("webdriver.edge.driver", "");
		System.setProperty("phantomjs.binary.path", "");
		System.setProperty("appURLOverride", "");
		System.setProperty("featureGroupName", "");
		System.setProperty("testRetryCount", "1");
		System.setProperty("enableScenarioScreenshots", "false");
		System.setProperty("saveReportsInHomeDir", "false");
		System.setProperty("phantomJSLoggingLevel", "NONE");
		System.setProperty("startInternalProxy", "");
		System.setProperty("tagsOverride", "");
		System.setProperty("dryRun", "");
		System.setProperty("importBaseUrl", "");
		System.setProperty("enableScreenshotOnError", "false");
		System.setProperty("monochromeOutput", "false");
		System.setProperty("dataset", "");
		System.setProperty("configuration", "");
		System.setProperty("browserStackUsername", "");
		System.setProperty("browserStackAccessToken", "");
	}

	/**
	 *
	 * @param file The feature file
	 * @param tagPrefix The prefix for the tags to count
	 * @return The highest tag number
	 */
	private int findHighestTag(final String file, final String tagPrefix) {
		try {
			final int maxTags = 1000;
			final String contents = IOUtils.toString(this.getClass().getResource(file));
			for (int count = 1; count < maxTags; ++count) {
				if (!contents.contains(tagPrefix + count)) {
					return count - 1;
				}
			}

			return maxTags;
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
