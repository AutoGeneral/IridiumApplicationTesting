package au.com.agic.apptesting;

import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.impl.SystemPropertyUtilsImpl;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	/**
	 * Not all environments support all browsers, so we can define the browsers that are tested
	 * via a system property.
	 */
	@Before
	public void getBrowserList() {
		final String browsersSysProp = SYSTEM_PROPERTY_UTILS.getPropertyEmptyAsNull(TEST_BROWSERS_SYSTEM_PROPERTY);
		if (StringUtils.isBlank(browsersSysProp)) {
			browsers.add("PhantomJS");
			browsers.add("Marionette");
			browsers.add("Chrome");
		} else {
			browsers.addAll(Arrays.asList(browsersSysProp.split(",")));
		}
	}

	@After
	public void cleanUpFiles() {
		globalTempFiles.forEach(File::delete);
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
					System.setProperty("appURLOverride", "https://mcasperson.github.io/iridium/examples/test.html");
					System.setProperty("testSource", this.getClass().getResource("/steptest.feature").toString());
					System.setProperty("testDestination", browser);
					System.setProperty("tagsOverride", "@tag1,@tag2,@tag3,@tag5,@test;~@tag4,@test");
					final int failures = new TestRunner().run(globalTempFiles);
					if (failures == 0) {
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
			System.setProperty("testSource", this.getClass().getResource("/failtestwithzap.feature").getPath());
			System.setProperty("testDestination", "PhantomJS");
			System.setProperty("startInternalProxy", "zap");
			final int failures = new TestRunner().run(globalTempFiles);
			if (failures == 0) {
				return;
			}
		}
	}

	@Test
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

	/*
		This test fails more often than not from Travis CI, so it is disabled
	 */
	//@Test
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

	private void setCommonProperties() {
		System.setProperty("webdriver.chrome.driver", "");
		System.setProperty("webdriver.opera.driver", "");
		System.setProperty("webdriver.ie.driver", "");
		System.setProperty("webdriver.gecko.driver", "");
		System.setProperty("webdriver.edge.driver", "");
		System.setProperty("phantomjs.binary.path", "");
		System.setProperty("enableScenarioScreenshots", "false");
		System.setProperty("saveReportsInHomeDir", "false");
		System.setProperty("phantomJSLoggingLevel", "NONE");
		System.setProperty("startInternalProxy", "");
		System.setProperty("tagsOverride", "");
		System.setProperty("dryRun", "");
		System.setProperty("importBaseUrl", "");
	}
}
