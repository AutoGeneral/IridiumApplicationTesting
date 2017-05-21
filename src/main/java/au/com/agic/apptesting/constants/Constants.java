package au.com.agic.apptesting.constants;

import java.util.regex.Pattern;

/**
 * Common settings
 */
public final class Constants {

	/**
	 * The number of times we try to copy files from URLs
	 */
	public static final int URL_COPY_RETRIES = 3;

	/**
	 * The number of times we try to perform web driver actions that might fail
	 */
	public static final int WEBDRIVER_ACTION_RETRIES = 3;

	/**
	 * The error code returned when a web driver failed to start
	 */
	public static final int WEB_DRIVER_FAILURE_EXIT_CODE = -3;

	/**
	 * The format of dates that are saved in filenames like screenshots and har files
	 */
	public static final String FILE_DATE_FORMAT = "YYYYMMddHHmmssSSS";

	/**
	 * The base file name for the default har file
	 */
	public static final String HAR_FILE_NAME_PREFIX = "browsermob";
	/**
	 * The extension for the default har file
	 */
	public static final String HAR_FILE_NAME_EXTENSION = "har";

	/**
	 * The default name of the HAR file saved by browsermob
	 */
	public static final String HAR_FILE_NAME = HAR_FILE_NAME_PREFIX + "." + HAR_FILE_NAME_EXTENSION;

	/**
	 * The system property that can be used to disable the automatic webdriver extraction
	 */
	public static final String USE_SUPPLIED_WEBDRIVERS = "useSuppliedWebdrivers";

	/**
	 * The system property that defines how long the app will run for before shuting down
	 */
	public static final String MAX_EXECUTION_TIME = "maxExecutionTime";

	/**
	 * The name of the merged junit xml file
	 */
	public static final String MERGED_REPORT = "MergedReport.xml";

	/**
	 * Sets the delay between test retries
	 */
	public static final String DELAY_BETWEEN_RETRY = "delayBetweenRetries";

	/**
	 * Sets the access token used when accessing browserstack
	 */
	public static final String BROWSER_STACK_ACCESS_TOKEN = "browserStackAccessToken";
	/**
	 * Sets the username used when accessing browserstack
	 */
	public static final String BROWSER_STACK_USERNAME = "browserStackUsername";

	/**
	 * This system property can be set to true to disable all automatic workarounds implemented for different
	 * browsers.
	 */
	public static final String DISABLE_INTEROP = "disableInterop";

	/**
	 * Because the web driver is not thread safe, we need to do a running loop over
	 * each of the different element location methods in short time slices to emulate
	 * a parallel search.
	 *
	 * This value also serves as the default sleep time for any loop.
	 */
	public static final int TIME_SLICE = 100;

	public static final int MILLISECONDS_PER_SECOND = 1000;

	/**
	 * This is the prefix we use for the thread name
	 */
	public static final String THREAD_NAME_PREFIX = "CucumberThread";

	/**
	 * A system property used to enable or disable the HTML report file
	 */
	public static final String HTML_REPORT_FILE = "htmlReportFile";
	/**
	 * A system property used to enable or disable the JSON report file
	 */
	public static final String JSON_REPORT_FILE = "jsonReportFile";
	/**
	 * A system property used to enable or disable the Text report file
	 */
	public static final String TXT_REPORT_FILE = "txtReportFile";
	/**
	 * A system property used to enable or disable the JUnit report file
	 */
	public static final String JUNIT_REPORT_FILE = "junitReportFile";

	/**
	 * Set this system property to true to configure cucumber to output text in monochrome
	 */
	public static final String MONOCHROME_OUTPUT = "monochromeOutput";

	/**
	 * The system property that is set to enable a dry run through the script
	 */
	public static final String DRY_RUN = "dryRun";
	/**
	 * The system property that defines how many times to retry tests before giving up
	 */
	public static final String TEST_RETRY_COUNT = "testRetryCount";
	/**
	 * If this system property is set to true, a new web driver (and therefor a new browser window)
	 * will be created for every scenario.
     */
	public static final String NEW_BROWSER_PER_SCENARIO = "newBrowserPerScenario";
	/**
	 * If set to true, or not set at all, this system property instructs Iridium to fail
	 * all scenarios after the first error. Otherwise, scenarios will continue independently.
     */
	public static final String FAIL_ALL_AFTER_FIRST_SCENARIO_ERROR = "failAllAfterFirstScenarioError";
	/**
	 * This system property hold the name of an internal proxy to start before the tests are run
	 */
	public static final String START_INTERNAL_PROXY = "startInternalProxy";
	/**
	 * This system property defines the name of the featureGroup that we'll be testing
	 */
	public static final String IMPORT_BASE_URL = "importBaseUrl";
	/**
	 * This system property defines the name of the featureGroup that we'll be testing
	 */
	public static final String FEATURE_GROUP_SYSTEM_PROPERTY = "featureGroupName";
	/**
	 * This system property defines the group of features and capabilities that we'll be testing
	 */
	public static final String GROUP_NAME_SYSTEM_PROPERTY = "groupName";
	/**
	 * Determines where the tests are run (i.e. locally or on BrowserStack)
	 */
	public static final String TEST_DESTINATION_SYSTEM_PROPERTY = "testDestination";
	/**
	 * Determines where the tests are loaded from
	 */
	public static final String TEST_SOURCE_SYSTEM_PROPERTY = "testSource";
	/**
	 * If true, the test script will save a screenshot at the end of each scenario
	 */
	public static final String ENABLE_SCREENSHOTS = "enableScenarioScreenshots";
	/**
	 * If true, the test script will save a screenshot if there was an error
	 */
	public static final String ENABLE_SCREENSHOT_ON_ERROR = "enableScreenshotOnError";
	/**
	 * This prefix is used in the filename for screenshots taken when the script has failed
	 */
	public static final String FAILURE_SCREENSHOT_SUFFIX = "Failure";
	/**
	 * The value assigned to the TEST_DESTINATION_SYSTEM_PROPERTY to indicate that the tests should
	 * be loaded locally
	 */
	public static final String REMOTE_TESTS = "BrowserStack";
	/**
	 * The value assigned to the system property to indicate that the tests should
	 * be run in chrome. Chrome is the default if no other matches are found for
	 * the system property.
	 */
	public static final String CHROME = "Chrome";
	/**
	 * The value assigned to the system property to indicate that the tests should
	 * be run in chrome with a number of command line switches to lock it down.
	 */
	public static final String CHROME_SECURE = "ChromeSecure";
	/**
	 * The value assigned to the system property to indicate that the tests should
	 * be run in chrome. Chrome is the default if no other matches are found for
	 * the system property.
	 */
	public static final String CHROME_HEADLESS = "ChromeHeadless";
	/**
	 * The value assigned to the system property to indicate that the tests should
	 * be run in chrome in headless mode locked down.
	 */
	public static final String CHROME_HEADLESS_SECURE = "ChromeHeadlessSecure";
	/**
	 * The value assigned to the system property to indicate that the tests should
	 * be run in Firefox using the Marionette driver. This is required with Firefox 48
	 * and all future versions.
	 * See https://github.com/SeleniumHQ/selenium/issues/2559 for details.
	 */
	public static final String MARIONETTE = "Marionette";
	/**
	 * The value assigned to the system property to indicate that the tests should
	 * be run in Firefox. This is only valid for versions of Firefox up to 47.0.1
	 */
	public static final String FIREFOX = "Firefox";
	/**
	 * The value assigned to the TEST_DESTINATION_SYSTEM_PROPERTY to indicate that the tests should
	 * be run in Safari.
	 */
	public static final String SAFARI = "Safari";
	/**
	 * The value assigned to the system property to indicate that the tests should
	 * be run in Opera.
	 */
	public static final String OPERA = "Opera";
	/**
	 * The value assigned to the system property to indicate that the tests should
	 * be run in IE.
	 */
	public static final String IE = "IE";
	/**
	 * The value assigned to the system property to indicate that the tests should
	 * be run in Edge.
	 */
	public static final String EDGE = "Edge";
	/**
	 * The value assigned to the system property to indicate that the tests should
	 * be run in the headless PhantomJS browser.
	 */
	public static final String PHANTOMJS = "PhantomJS";
	/**
	 * The location of the Firefox executable
	 */
	public static final String FIREFOX_BINARY = "firefoxBinary";
	/**
	 * The system property that defines the phantomjs logging level
	 */
	public static final String PHANTOMJS_LOGGING_LEVEL_SYSTEM_PROPERTY = "phantomJSLoggingLevel";
	/**
	 * The system property that defines the phantom js user agent
	 */
	public static final String PHANTON_JS_USER_AGENT_SYSTEM_PROPERTY = "phantomJSUserAgent";
	/**
	 * The default phantom js logging level
	 */
	public static final String DEFAULT_PHANTOM_JS_LOGGING_LEVEL = "NONE";
	/**
	 * The location of the chrome webdriver executable See http://chromedriver.storage.googleapis.com/index.html
	 */
	public static final String CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY = "webdriver.chrome.driver";
	/**
	 * The location of the chrome executable
	 */
	public static final String CHROME_EXECUTABLE_LOCATION_SYSTEM_PROPERTY = "webdriver.chrome.bin";
	/**
	 * The location of the opera webdriver executable https://github.com/operasoftware/operachromiumdriver/releases
	 */
	public static final String OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY = "webdriver.opera.driver";
	/**
	 * The location of the Edge webdriver executable
     */
	public static final String EDGE_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY = "webdriver.edge.driver";
	/**
	 * The location of the phantomjs distribution https://github.com/detro/ghostdriver
	 */
	public static final String PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY = "phantomjs.binary.path";
	/**
	 * The location of the IE webdriver executable http://selenium-release.storage.googleapis.com/index.html?path=2.53/
	 */
	public static final String IE_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY = "webdriver.ie.driver";
	/**
	 * The name of the profile to use when starting firefox. This defaults to webdriver creating an
	 * anonymous profile
	 */
	public static final String FIREFOX_PROFILE_SYSTEM_PROPERTY = "webdriver.firefox.profile";
	/**
	 * The location of the firefox executable
	 */
	public static final String FIREFOX_EXECUTABLE_LOCATION_SYSTEM_PROPERTY = "webdriver.firefox.bin";
	/**
	 * The maximum number of data sets to run
	 */
	public static final String NUMBER_DATA_SETS_SYSTEM_PROPERTY = "numberDataSets";
	/**
	 * The name of the profile that contains data sets for the test
	 */
	public static final String DATA_SETS_PROFILE_SYSTEM_PROPERTY = "dataset";
	/**
	 * The maximum number of data sets to run
	 */
	public static final String NUMBER_URLS_SYSTEM_PROPERTY = "numberURLs";
	/**
	 * The URL to use for the app
	 */
	public static final String APP_URL_OVERRIDE_SYSTEM_PROPERTY = "appURLOverride";
	/**
	 * Defines a URL to name mapping from a system property
	 */
	public static final Pattern APP_URL_OVERRIDE_SYSTEM_PROPERTY_REGEX = Pattern.compile("appURLOverride\\.(.+)");
	/**
	 * The tags to use for the test
	 */
	public static final String TAGS_OVERRIDE_SYSTEM_PROPERTY = "tagsOverride";
	/**
	 * The name of profiles to load over the BROWSERSTACK_CONFIG_PROFILE_NAME profile for this run
	 * to overwrite some settings.
	 */
	public static final String CONFIGURATION = "configuration";
	/**
	 * The name of the system property that defines whether the browser windows should be left open
	 * after the test has been run.
	 */
	public static final String LEAVE_WINDOWS_OPEN_SYSTEM_PROPERTY = "leaveWindowsOpen";
	/**
	 * The name of the system property that defines whether the HTML report is opened automatically
	 * once the test is completed.
	 */
	public static final String OPEN_REPORT_FILE_SYSTEM_PROPERTY = "openReportFile";
	/**
	 * The directory that will hold the final reports
	 */
	public static final String REPORTS_DIRECTORY = "reportsDirectory";
	/**
	 * The name of the system property that defines whether the cucumber reports are saved in the
	 * users home directory, or in the current working directory
	 */
	public static final String SAVE_REPORTS_IN_HOME_DIR = "saveReportsInHomeDir";
	/**
	 * How many cucumber instances to run concurrently
	 */
	public static final String NUMBER_THREADS_SYSTEM_PROPERTY = "numberOfThreads";
	/**
	 * Whether or not to capture the video of the test
	 */
	public static final String VIDEO_CAPTURE_SYSTEM_PROPERTY = "enableVideoCapture";
	public static final String CAPABILITIES_XPATH = "settings/desiredCapabilities%/capability%";
	public static final String URLS_XPATH = "urlMappings/%/urlMapping%";
	/**
	 * This is the class assigned to the postcode input boxes in A&amp;G apps. It is used to execute
	 * some custom javascript to populate the suburb selection.
	 */
	public static final String POSTCODE_CLASS = "adr-postCode";
	/**
	 * A regex that matches the xpath of a feature node
	 */
	public static final String FEATURE_XPATH_RE = "features/feature(\\[\\d+\\])?";
	/**
	 * A regex that matches the xpath of a feature node script element
	 */
	public static final String FEATURE_SCRIPT_XPATH_RE = "features/feature(\\[\\d+\\])?/script";
	/**
	 * The attribute on a commonTagSet element that defines the name
	 */
	public static final String NAME_ATTRIBUTE = "name";
	public static final Pattern COMMENT_LINE = Pattern.compile("^#.*");
	/**
	 * Metadata comments at the top of a feature script to define a feature group
	 */
	public static final Pattern FEATURE_GROUP_HEADER =
		Pattern.compile("^#\\s*FeatureGroup:\\s*(?<value>.+)");
	/**
	 * Metadata comments at the top of a feature script to define a tag set
	 */
	public static final Pattern TAG_SET_HEADER =
		Pattern.compile("^#\\s*TagSet:\\s*(?<name>[^\\s:]+):?\\s*(?<value>.+)");
	/**
	 * Metadata comments at the top of a feature script to define whether a feature is enabled or
	 * not
	 */
	public static final Pattern ENABLED_HEADER = Pattern.compile("^#\\s*Enabled:\\s*(?<value>.+)");

	/**
	 * How long to wait for an element to be available
	 */
	public static final int WAIT = 2;
	/**
	 * Wait a second between steps by default
	 */
	public static final int DEFAULT_WAIT_TIME = 1000;

	/**
	 * System property that holds the external proxy host name or ip address
	 */
	public static final String EXTERNAL_PROXY_HOST = "externalProxyHost";

	/**
	 * System property that holds the external proxy port
	 */
	public static final String EXTERNAL_PROXY_PORT = "externalProxyPort";

	/**
	 * System property that holds the external proxy username
	 */
	public static final String EXTERNAL_PROXY_USERNAME = "externalProxyUsername";

	/**
	 * System property that holds the external proxy password
	 */
	public static final String EXTERNAL_PROXY_PASSWORD = "externalProxyPassword";

	/**
	 * System property that holds the external proxy realm
	 */
	public static final String EXTERNAL_PROXY_REALM = "externalProxyRealm";

	/**
	 * Change this if you have set the apikey in ZAP via Options / API
	 */
	public static final String ZAP_API_KEY = null;

	/**
	 * How long to delay when entering each character into a text box
	 */
	public static final int KEY_STROKE_DELAY = 300;

	/**
	 * How quickly Selenium should poll the browser for an element it is waiting for
	 */
	public static final int ELEMENT_WAIT_SLEEP_TIMEOUT = 100;

	/**
	 * A regex that catches line endings across multiple platforms
	 */
	public static final String LINE_END_REGEX = "\\r\\n?|\\n";

	/**
	 * Line endings used by any generated files
	 */
	public static final String LINE_END_OUTPUT = "\n";

	private Constants() {
	}

}
