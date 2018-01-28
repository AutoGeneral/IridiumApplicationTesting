package au.com.agic.apptesting.lambda;

import au.com.agic.apptesting.Main;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * An entry point for the app when called from AWS lambda
 */
public class LambdaEntry implements RequestHandler<Map<String, String>, Integer> {

	/**
	 * The list of accepted options
	 */
	private static final List<String> ACCEPTED_SETTINGS = Arrays.asList(
		"browserStackUsername",
		"browserStackAccessToken",
		"webdriver.chrome.driver",
		"webdriver.ie.driver",
		"webdriver.opera.driver",
		"opera.binary",
		"phantomjs.binary.path",
		"testDestination",
		"testSource",
		"groupName",
		"featureGroupName",
		"configuration",
		"leaveWindowsOpen",
		"localBrowser",
		"appURLOverride",
		"numberDataSets",
		"numberOfThreads",
		"tagsOverride",
		"dataset",
		"openReportFile",
		"saveReportsInHomeDir",
		"importBaseUrl",
		"numberURLs",
		"enableVideoCapture",
		"enableScenarioScreenshots",
		"startInternalProxy",
		"externalProxyHost",
		"externalProxyPort",
		"externalProxyUsername",
		"externalProxyPassword",
		"externalProxyRealm",
		"newBrowserPerScenario",
		"failAllAfterFirstScenarioError",
		"phantomJSUserAgent",
		"testBrowsers",
		"testRetryCount",
		"dryRun",
		"monochromeOutput"
	);

	@Override
	public Integer handleRequest(final Map<String, String> input, final Context context) {

		input.entrySet().stream()
			.filter(entry -> ACCEPTED_SETTINGS.contains(entry.getKey()))
			.forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		final int failures = Main.run();
		return failures;
	}
}
