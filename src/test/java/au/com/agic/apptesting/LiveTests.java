package au.com.agic.apptesting;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs examples as unit tests
 */
public class LiveTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(LiveTests.class);
	private static final int RETRY_COUNT = 3;
	private static final int SLEEP = 60000;
	private final List<File> globalTempFiles = new ArrayList<File>();

	@After
	public void cleanUpFiles() {
		globalTempFiles.forEach(File::delete);
	}

	/**
	 * This test runs a feature that uses a wide selection of the steps provided
	 * with Iridium, and serves as a good regression test.
	 */
	@Test
	public void stepTests() {
		for (final String browser : new String[] {"Chrome", "Firefox", "PhantomJS"}) {
			for (int retry = 0; retry < RETRY_COUNT; ++retry) {
				try {
					setCommonProperties();
					System.setProperty("appURLOverride", "https://mcasperson.github.io/iridium/examples/test.html");
					System.setProperty("testSource", this.getClass().getResource("/steptest.feature").getPath());
					System.setProperty("testDestination", browser);
					final int failures = new TestRunner().run(globalTempFiles);
					if (failures == 0) {
						continue;
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
	public void verificationTest22() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			try {
				setCommonProperties();
				System.setProperty("appURLOverride", "http://ticketmonster-jdf.rhcloud.com");
				System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/22.verification/test.feature");
				System.setProperty("testDestination", "Chrome");
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
		System.setProperty("phantomjs.binary.path", "");
		System.setProperty("enableScenarioScreenshots", "false");
		System.setProperty("saveReportsInHomeDir", "false");
		System.setProperty("phantomJSLoggingLevel", "NONE");
		System.setProperty("startInternalProxy", "");
	}
}
