package au.com.agic.apptesting;

import org.junit.Assert;
import org.junit.Test;

/**
 * Runs examples as unit tests
 */
public class LiveTests {
	private static final int RETRY_COUNT = 3;

	@Test
	public void launchAcceptanceTest() {
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
	public void launchVerificationTest() {
		for (int retry = 0; retry < RETRY_COUNT; ++retry) {
			setCommonProperties();
			System.setProperty("appURLOverride", "http://ticketmonster-jdf.rhcloud.com");
			System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/22.verification/test.feature");
			System.setProperty("testDestination", "Firefox");
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
	}
}
