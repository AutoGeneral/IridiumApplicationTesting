package au.com.agic.apptesting.lambda;

import java.util.Arrays;
import java.util.List;

public class LambdaSettings {
	/**
	 * The list of accepted options
	 */
	public static final List<String> ACCEPTED_SETTINGS = Arrays.asList(
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
}
