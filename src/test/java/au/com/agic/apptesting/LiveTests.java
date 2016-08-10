package au.com.agic.apptesting;

import org.junit.Assert;
import org.junit.Test;

/**
 * Runs examples as unit tests
 */
public class LiveTests {
	@Test
	public void launchAcceptanceTest() {
		try {
			System.setProperty("appURLOverride", "https://mcasperson.github.io/iridium/examples/app.html");
			System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/24.acceptancetests/test-populated.feature");
			System.setProperty("testDestination", "PhantomJS");
			System.setProperty("enableScenarioScreenshots", "false");
			System.setProperty("saveReportsInHomeDir", "false");
			System.setProperty("phantomJSLoggingLevel", "NONE");

			Assert.assertEquals(0, new TestRunner().run());
		} finally {
			resetDriverPaths();
		}
	}

	@Test
	public void launchVerificationTest() {
		try {
			System.setProperty("appURLOverride", "http://ticketmonster-jdf.rhcloud.com");
			System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/22.verification/test.feature");
			System.setProperty("testDestination", "Firefox");
			System.setProperty("enableScenarioScreenshots", "false");
			System.setProperty("saveReportsInHomeDir", "false");
			System.setProperty("phantomJSLoggingLevel", "NONE");

			Assert.assertEquals(0, new TestRunner().run());
		} finally {
			resetDriverPaths();
		}
	}

	private void resetDriverPaths() {
		System.setProperty("webdriver.chrome.driver", "");
		System.setProperty("webdriver.opera.driver", "");
		System.setProperty("webdriver.ie.driver", "");
		System.setProperty("phantomjs.binary.path", "");
	}
}
