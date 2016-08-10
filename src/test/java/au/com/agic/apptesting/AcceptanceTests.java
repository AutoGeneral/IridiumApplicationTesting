package au.com.agic.apptesting;

import org.junit.Assert;
import org.junit.Test;

/**
 * Runs the acceptance test example
 */
public class AcceptanceTests {
	@Test
	public void launchAcceptanceTest() {
		System.setProperty("appURLOverride", "https://mcasperson.github.io/iridium/examples/app.html");
		System.setProperty("testSource", "https://raw.githubusercontent.com/mcasperson/IridiumApplicationTesting/master/examples/24.acceptancetests/test-populated.feature");
		System.setProperty("testDestination", "PhantomJS");
		System.setProperty("enableScenarioScreenshots", "false");
		System.setProperty("saveReportsInHomeDir", "false");
		System.setProperty("phantomJSLoggingLevel", "NONE");

		Assert.assertEquals(0, new TestRunner().run());
	}
}
