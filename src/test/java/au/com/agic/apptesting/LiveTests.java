package au.com.agic.apptesting;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs examples as unit tests
 */
public class LiveTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(LiveTests.class);
	private static final int RETRY_COUNT = 3;

	@Test
	public void modifyRequests19() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			setCommonProperties();
			System.setProperty("appURLOverride", "https://dzone.com");
			System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/19.modifyrequests/test.feature");
			System.setProperty("testDestination", "PhantomJS");
			final int failures = new TestRunner().run();
			if (failures == 0) {
				return;
			}
		}

		Assert.fail();
	}

	@Test
	public void passiveSecurity20() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			setCommonProperties();
			System.setProperty("appURLOverride", "https://dzone.com");
			System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/20.passivesecurity/test-nofail.feature");
			System.setProperty("testDestination", "PhantomJS");
			System.setProperty("startInternalProxy", "zap");
			final int failures = new TestRunner().run();
			if (failures == 0) {
				return;
			}
		}

		Assert.fail();
	}

	@Test
	public void gherkinExamples21() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			setCommonProperties();
			System.setProperty("appURLOverride", "https://dzone.com");
			System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/21.gherkinexamples/test.feature");
			System.setProperty("testDestination", "PhantomJS");
			final int failures = new TestRunner().run();
			if (failures == 0) {
				return;
			}
		}

		Assert.fail();
	}

	@Test
	public void verificationTest22() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			setCommonProperties();
			System.setProperty("appURLOverride", "http://ticketmonster-jdf.rhcloud.com");
			System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/22.verification/test.feature");
			System.setProperty("testDestination", "Chrome");
			final int failures = new TestRunner().run();
			if (failures == 0) {
				return;
			}
		}

		Assert.fail();
	}

	@Test
	public void deadLinkCheck23() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			setCommonProperties();
			System.setProperty("appURLOverride", "https://autogeneral.gitbooks.io/iridiumapplicationtesting-gettingstartedguide/content/tips_and_tricks.html");
			System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/23.deadlinkcheck/test-nofail.feature");
			System.setProperty("testDestination", "PhantomJS");
			final int failures = new TestRunner().run();
			if (failures == 0) {
				return;
			}
		}

		Assert.fail();
	}

	@Test
	public void acceptanceTest24() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			setCommonProperties();
			System.setProperty("appURLOverride", "https://mcasperson.github.io/iridium/examples/app.html");
			System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/24.acceptancetests/test-populated.feature");
			System.setProperty("testDestination", "PhantomJS");
			final int failures = new TestRunner().run();
			if (failures == 0) {
				return;
			}
		}

		Assert.fail();
	}

	@Test
	public void driverPerScenario25() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			setCommonProperties();
			System.setProperty("appURLOverride", "https://dzone.com");
			System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/25.driverperscenario/test.feature");
			System.setProperty("testDestination", "PhantomJS");
			final int failures = new TestRunner().run();
			if (failures == 0) {
				return;
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