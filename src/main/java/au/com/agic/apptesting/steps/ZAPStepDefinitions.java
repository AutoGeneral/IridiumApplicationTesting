package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.collect.Iterables;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.ZAPPolicyException;
import au.com.agic.apptesting.utils.CastUtils;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.impl.ZapProxyUtilsImpl;
import au.com.agic.apptesting.zap.ZAPFalsePositive;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zaproxy.clientapi.core.Alert;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javaslang.control.Try;

/**
 * These are Cucubmber steps that are used by ZAP
 */
public class ZAPStepDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZAPStepDefinitions.class);
	private static final String DECIMAL_FORMAT = "#.##";
	private static final int ZAP_SLEEP = 5000;
	/**
	 * Don't spider for longer than 5 minutes
	 */
	private static final int ZAP_SPIDER_TIMEOUT = 300000;
	private static final int ZAP_MINIMUM_DONE_BEFORE_COMPLETE = 99;
	private static final int ZAP_DONE = 100;

	/**
	 * This is a mapping between scanner types and their ZAP IDs.
	 */
	private static final Map<String, String> SCANNER_IDS = new HashMap<>();

	/**
	 * A static in it block to populate the SCANNER_IDS map
	 */
	static {
		SCANNER_IDS.put("directory-browsing", "0");
		SCANNER_IDS.put("cross-site-scripting", "40012,40014,40016,40017");
		SCANNER_IDS.put("sql-injection", "40018");
		SCANNER_IDS.put("path-traversal", "6");
		SCANNER_IDS.put("remote-file-inclusion", "7");
		SCANNER_IDS.put("server-side-include", "40009");
		SCANNER_IDS.put("script-active-scan-rules", "50000");
		SCANNER_IDS.put("server-side-code-injection", "90019");
		SCANNER_IDS.put("remote-os-command-injection", "90020");
		SCANNER_IDS.put("external-redirect", "20019");
		SCANNER_IDS.put("crlf-injection", "40003");
		SCANNER_IDS.put("source-code-disclosure", "42,10045,20017");
		SCANNER_IDS.put("shell-shock", "10048");
		SCANNER_IDS.put("remote-code-execution", "20018");
		SCANNER_IDS.put("ldap-injection", "40015");
		SCANNER_IDS.put("xpath-injection", "90021");
		SCANNER_IDS.put("xml-external-entity", "90023");
		SCANNER_IDS.put("padding-oracle", "90024");
		SCANNER_IDS.put("el-injection", "90025");
		SCANNER_IDS.put("insecure-http-methods", "90028");
		SCANNER_IDS.put("parameter-pollution", "20014");
	}

	/**
	 * The property key that defines the current scanners that have been enabled
	 */
	private static final String SCANNER_IDS_KEY = "ScannerIds";
	/**
	 * The property key that defines the group state of the scanner
	 */
	private static final String GLOBAL_SCANNER_POLICY_KEY = "GlobalPolicy";
	/**
	 * The property key that defines the list of false positives
	 */
	private static final String FALSE_POSITIVES_KEY = "FalsePositives";
	/**
	 * The property that holds the spidered state of the app
	 */
	private static final String SPIDERED_KEY = "Spidered";
	/**
	 * The key to a collection of urls that are ignored by the spider
	 */
	private static final String SPIDER_IGNORED_URLS = "SpiderIgnoreURLs";

	/**
	 * This is how deeply the spider will traverse the website
	 */
	private static final int DEFAULT_SPIDER_DEPTH = 10;

	/**
	 * This is how many threads the spider will use by default
	 */
	private static final int DEFAULT_SPIDER_THREAD_COUNT = 10;

	/**
	 * Get the web driver for this thread
	 */
	private final FeatureState featureState =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

	/**
	 * The suffix of the directory that we will save zap report files in
	 */
	private static final String ZAP_REPORT_DIR_SUFFIX = "zap";

	private static final String ERROR_MESSAGE = "You can not use ZAP steps without enabling the proxy with the "
		+ Constants.START_INTERNAL_PROXY + " system property set to " + Constants.ZED_ATTACK_PROXY;

	/**
	 * Does a bunch of checks to ensure that the test has specified the correct settings to use ZAP, and returns the
	 * client api object
	 *
	 * @return The ZAP Client API object
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private ClientApi getClientApi() {
		checkState(featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).isPresent(), ERROR_MESSAGE);

		final Optional<?> proxyInterface = featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME)
			.get().getInterface();

		checkState(proxyInterface.isPresent() && proxyInterface.get() instanceof ClientApi, ERROR_MESSAGE);

		return (ClientApi) proxyInterface.get();
	}

	/**
	 * Creates an empty ZAP session
	 * @throws ClientApiException When the ZAP API threw an exception
	 */
	@When("I create an empty ZAP session")
	public void startSession() throws ClientApiException {
		final ClientApi clientApi = getClientApi();
		final String url = featureState.getUrlDetails().getDefaultUrl();
		clientApi.httpSessions.createEmptySession(Constants.ZAP_API_KEY, url, "session");
	}

	/**
	 * Creates an empty ZAP session
	 * @throws ClientApiException When the ZAP API threw an exception
	 */
	@When("I set the active ZAP session")
	public void activeSession() throws ClientApiException {
		final ClientApi clientApi = getClientApi();
		final String url = featureState.getUrlDetails().getDefaultUrl();
		clientApi.httpSessions.addSessionToken(Constants.ZAP_API_KEY, url, "session");
	}

	/**
	 * This step will save the ZAP report to disk with the given filename
	 *
	 * @param path The name of the report, like "zapreport.xml"
	 * @throws IOException When the report file could not be written
	 * @throws ClientApiException When the ZAP API threw an exception
     */
	@When("the ZAP XML report is written to the file \"(.*?)\"")
	public void writeXmlReport(final String path) throws IOException, ClientApiException {
		final ClientApi clientApi = getClientApi();
		final String report = new String(clientApi.core.xmlreport(Constants.ZAP_API_KEY));

		final File reportDir = new File(featureState.getReportDirectory());
		final File zapReportDir = new File(
			reportDir,
			Thread.currentThread().getName() + "." + ZAP_REPORT_DIR_SUFFIX);
		if (!zapReportDir.exists()) {
			zapReportDir.mkdir();
		}

		FileUtils.write(new File(zapReportDir, path), report);
	}

	/**
	 * This configures ZAP with all scanners, both passive and active
	 * @throws ClientApiException When the ZAP API threw an exception
     */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Given("a scanner with all policies enabled")
	public void enableAllScanners() throws ClientApiException {
		final ClientApi clientApi = getClientApi();
		clientApi.pscan.enableAllScanners(Constants.ZAP_API_KEY);
		clientApi.ascan.enableAllScanners(Constants.ZAP_API_KEY, null);

		final Map<String, Object> properties = featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME)
			.get().getProperties();
		properties.put(GLOBAL_SCANNER_POLICY_KEY, "ALL");
		featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).get().setProperties(properties);
	}

	/**
	 * This configures ZAP with no scanners, neither passive nor active
	 * @throws ClientApiException When the ZAP API threw an exception
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Given("a scanner with all policies disabled")
	public void disableAllScanners() throws ClientApiException {
		final ClientApi clientApi = getClientApi();
		clientApi.pscan.disableAllScanners(Constants.ZAP_API_KEY);
		clientApi.ascan.disableAllScanners(Constants.ZAP_API_KEY, null);

		final Map<String, Object> properties = featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME)
			.get().getProperties();
		properties.put(GLOBAL_SCANNER_POLICY_KEY, "NONE");
		featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).get().setProperties(properties);
	}

	/**
	 * Enables only the passive scanner. The active scanner is disabled.
	 * @throws ClientApiException When the ZAP API threw an exception
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Given("the passive scanner is enabled")
	public void enablePassiveScanner() throws ClientApiException {
		checkState(featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).isPresent());

		final ClientApi clientApi = getClientApi();
		clientApi.pscan.enableAllScanners(Constants.ZAP_API_KEY);
		clientApi.ascan.disableAllScanners(Constants.ZAP_API_KEY, null);

		final Map<String, Object> properties = featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME)
			.get().getProperties();
		properties.put(GLOBAL_SCANNER_POLICY_KEY, "PASSIVE");
		featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).get().setProperties(properties);
	}

	/**
	 * Enables only the active scanner. The passive scanner is disabled.
	 * @throws ClientApiException When the ZAP API threw an exception
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Given("the active scanner is enabled")
	public void enableActiveScanner() throws ClientApiException {
		checkState(featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).isPresent());

		final ClientApi clientApi = getClientApi();
		clientApi.pscan.disableAllScanners(Constants.ZAP_API_KEY);
		clientApi.ascan.enableAllScanners(Constants.ZAP_API_KEY, null);

		final Map<String, Object> properties = featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME)
			.get().getProperties();
		properties.put(GLOBAL_SCANNER_POLICY_KEY, "ACTIVE");
		featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).get().setProperties(properties);
	}

	/**
	 * Enabled the given active scan policy
	 *
	 * @param policyName The name of the active scan policy
	 * @throws ClientApiException When the ZAP API threw an exception
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Given("the \"(.*?)\" policy is enabled")
	public void enablePolicy(final String policyName) throws ClientApiException {
		checkState(featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).isPresent());

		final ClientApi clientApi = getClientApi();

		if (!SCANNER_IDS.containsKey(policyName.toLowerCase())) {
			throw new ZAPPolicyException("No policy found for: " + policyName);
		}

		final String scannerIds = SCANNER_IDS.get(policyName.toLowerCase());

		if (scannerIds == null) {
			throw new ZAPPolicyException("No matching policy found for: " + policyName);
		}

		final Map<String, Object> properties = featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME)
			.get().getProperties();
		properties.put(SCANNER_IDS_KEY, scannerIds);
		featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).get().setProperties(properties);

		clientApi.ascan.enableScanners(Constants.ZAP_API_KEY, scannerIds, null);
	}

	/**
	 * Sets the attack strength
	 * @param strength The ZAP attack strength
	 */
	@Given("the attack strength is set to \"(.*?)\"")
	public void setAttackStrength(final String strength) {

		final ClientApi clientApi = getClientApi();

		SCANNER_IDS.entrySet().stream()
			.filter(a -> StringUtils.isNotBlank(a.getValue()))
			.forEach(a -> Arrays.asList(a.getValue().split(",")).stream()
				.forEach(t ->
					Try.run(() ->
						clientApi.ascan.setScannerAttackStrength(
							Constants.ZAP_API_KEY,
							t,
							strength.toUpperCase(), null)
					)
				)
			);
	}

	/**
	 * Sets the alert threshold for all active scanners
	 * @param threshold The ZAP alert threshold
	 * @throws ClientApiException when the ZAP API threw an exception
	 */
	@Given("the alert threshold is set to \"(.*?)\"")
	public void setAlertThreshold(final String threshold) throws ClientApiException {

		final ClientApi clientApi = getClientApi();

		SCANNER_IDS.entrySet().stream()
			.filter(a -> StringUtils.isNotBlank(a.getValue()))
			.forEach(a -> Arrays.asList(a.getValue().split(",")).stream()
				.forEach(t ->
					Try.run(() ->
						clientApi.ascan.setScannerAlertThreshold(
							Constants.ZAP_API_KEY,
							t,
							threshold.toUpperCase(),
							null)
					)
				)
			);
	}

	/**
	 * Defines a list of URL regular expressions that are excluded from the ZAP scan
	 * @param excludedRegexes A list of URL regular expressions to exclude
	 * @throws ClientApiException when the ZAP API threw an exception
	 */
	@Given("the following URL regular expressions are excluded from the scanner")
	public void excludeUrlsFromScan(final List<String> excludedRegexes) throws ClientApiException {
		final ClientApi clientApi = getClientApi();

		for (String excluded : excludedRegexes) {
			clientApi.ascan.excludeFromScan(Constants.ZAP_API_KEY, excluded);
		}
	}

	/**
	 * Runs the ZAP active scanner
	 * @param appName the optional name of the URL whose URL will be used to launch the scan.
	 * @throws ClientApiException when the ZAP API threw an exception
	 */
	@SuppressWarnings({"OptionalGetWithoutIsPresent", "BusyWait"})
	@When("the active scanner is run(?: from \"([^\"]*)\")?")
	public void runScanner(final String appName) throws ClientApiException {
		final ClientApi clientApi = getClientApi();
		final String url = StringUtils.isNotBlank(appName)
			? featureState.getUrlDetails().getUrl(appName)
			: featureState.getUrlDetails().getDefaultUrl();

		LOGGER.info("Scanning: {}", url);
		clientApi.ascan.scan(Constants.ZAP_API_KEY, url, "true", "false", null, null, null);

		final String scanId = Optional.of(clientApi.ascan.scans())
			.map(e -> CastUtils.as(ApiResponseList.class, e))
			.map(ApiResponseList::getItems)
			.map(Iterables::getLast)
			.map(e -> CastUtils.as(ApiResponseSet.class, e))
			.map(e -> e.getAttribute("id"))
			.orElse(null);

		double lastPercentageDone = -1;

		while (true) {
			/*
				The ZAP API returns a lot of nested key value pairs, and a lot of the objects that are
				returned need to be cast in order to work with them. This is pretty nasty, is not
				documented, and basically requires you keep traversing and casting key/value pairs until
				you find the data you are interested in at the depth that you expect to find it.
			 */
			final double incomplete = Optional.of(clientApi.ascan.scanProgress(scanId))
				.map(v -> CastUtils.as(ApiResponseList.class, v))
				.filter(Objects::nonNull)
				.filter(v -> "scanProgress".equals(v.getName()))
				.map(ApiResponseList::getItems)
				.map(v -> v.stream()
					.map(w -> CastUtils.as(ApiResponseList.class, w))
					.filter(Objects::nonNull)
					.map(ApiResponseList::getItems)
					.map(w -> w.stream()
						.map(x -> CastUtils.as(ApiResponseList.class, x))
						.filter(Objects::nonNull)
						.map(ApiResponseList::getItems)
						.map(y -> y.stream()
							.map(z -> CastUtils.as(ApiResponseElement.class, z))
							.filter(Objects::nonNull)
							.filter(z -> "status".equalsIgnoreCase(z.getName()))
							.mapToInt(z ->
								"Complete".equalsIgnoreCase(z.getValue()) ? 0 : 1)
							.average()
						)
						.mapToDouble(g -> g.orElse(0))
						.average()
					)
					.mapToDouble(w -> w.orElse(0))
					.average()
				)
				.orElse(OptionalDouble.of(0)).orElse(0);

			/*
				In the absence of incomplete scans, we are done
			 */
			if (incomplete == 0) {
				break;
			}

			/*
				Subtract from 1 to find the complete scans
			 */
			final double percentDone = 1.0d - incomplete;

			/*
				Dump so info so the users know where things are at
			 */
			if (percentDone > lastPercentageDone) {
				LOGGER.info("ZAP Active Scan is {}% complete", new DecimalFormat(DECIMAL_FORMAT)
					.format((1.0d - incomplete) * ZAP_DONE));
				lastPercentageDone = percentDone;
			}

			try {
				Thread.sleep(ZAP_SLEEP);
			} catch (final InterruptedException ignored) {
				/*
					nothing to do here
				 */
			}
		}
	}

	/**
	 * Define a list of false positives to be excluded from the scan results
	 * @param falsePositives The false positives to be removed
	 * @throws ClientApiException when the ZAP API threw an exception
	 */
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@When("the following false positives are ignored")
	public void removeFalsePositives(final List<ZAPFalsePositive> falsePositives) throws ClientApiException {
		checkState(featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).isPresent());

		final ClientApi clientApi = getClientApi();

		final Map<String, Object> properties = featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME)
			.get().getProperties();
		properties.put(FALSE_POSITIVES_KEY, falsePositives);
		featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).get().setProperties(properties);
	}

	/**
	 * Check to see if any risks were identified during the scan
	 *
	 * @param risk    The level of risk. Either HIGH, MEDIUM or LOW
	 * @param baseUrl An optional regex that can be used to match the url that a risk is assoicated with
	 * @throws ClientApiException When the ZAP API threw an exception
	 */
	@Then("^no \"(.*?)\" or higher risk vulnerabilities should be present(?: for the base url \"(.*?)\")?$")
	public void checkVulnerabilities(final String risk, final String baseUrl) throws ClientApiException {
		processVulnerabilities(risk, baseUrl, false);
	}

	/**
	 * Report any risks were identified during the scan without throwing an error
	 *
	 * @param risk    The level of risk. Either HIGH, MEDIUM or LOW
	 * @param baseUrl An optional regex that can be used to match the url that a risk is assoicated with
	 * @throws ClientApiException When the ZAP API threw an exception
	 */
	@Then("^I report any \"(.*?)\" or higher risk vulnerabilities (?: for the base url \"(.*?)\")?$")
	public void reportVulnerabilities(final String risk, final String baseUrl) throws ClientApiException {
		processVulnerabilities(risk, baseUrl, true);
	}

	public void processVulnerabilities(
		final String risk,
		final String baseUrl,
		final boolean reportOnly) throws ClientApiException {

		final ClientApi clientApi = getClientApi();

		final Function<String, Alert.Risk> convertRisk = o -> {
			if ("MEDIUM".equalsIgnoreCase(o)) {
				return Alert.Risk.Medium;
			}

			if ("LOW".equalsIgnoreCase(o)) {
				return Alert.Risk.Low;
			}

			return Alert.Risk.High;
		};

		/*
			Turn the baseUrl into a Regex pattern if it was supplied
		 */
		final Optional<Pattern> pattern = Optional.ofNullable(baseUrl)
			.filter(StringUtils::isNotBlank)
			.map(Pattern::compile);

		/*
			Get and filter the risks
		 */
		final List<Alert> alerts = clientApi.getAlerts(null, 0, Integer.MAX_VALUE);
		final List<Alert> filteredAlerts = Optional.of(alerts)
			.map(e -> getAllAlertsByRiskRating(e, convertRisk.apply(risk), pattern))
			.map(this::removeFalsePositivesFromAlerts)
			.orElse(new ArrayList<>());
		final String details = getAlertDetails(filteredAlerts);

		if (reportOnly) {
			LOGGER.info(filteredAlerts.size() + " " + risk
				+ " vulnerabilities found.\nDetails:\n" + details);
		} else {
			/*
				Throw an exception if there are any identified risks
		 	*/
			assertThat(filteredAlerts.size() + " " + risk + " vulnerabilities found.\nDetails:\n" + details,
				filteredAlerts.size(), equalTo(0));
		}

	}

	/**
	 * Starts the ZAP spider.
	 * @param depth How far to search into the application
	 * @param timeout How long to wait for a timeout
	 * @throws ClientApiException When the ZAP API threw an exception
	 */
	@SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
	@And("^the application is spidered(?: to a depth of\"(\\d+)\")?(?: timing out after \"(\\d+)\" seconds)?$")
	public void theApplicationIsSpidered(final Integer depth, final Integer timeout) throws ClientApiException {
		checkState(featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).isPresent());

		final ClientApi clientApi = getClientApi();

		final Map<String, Object> properties = featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME)
			.get().getProperties();

		if (!properties.containsKey(SPIDERED_KEY)
				|| !Boolean.parseBoolean(properties.get(SPIDERED_KEY).toString())) {
			properties.put(SPIDERED_KEY, true);

			// ignore urls
			if (properties.containsKey(SPIDER_IGNORED_URLS)) {
				final List<String> ignoredUrls = (List<String>) properties.get(SPIDER_IGNORED_URLS);
				for (final String regex : ignoredUrls) {
					clientApi.spider.excludeFromScan(Constants.ZAP_API_KEY, regex);
				}
			}

			final int fixedDepth = depth == null ? DEFAULT_SPIDER_DEPTH : depth;
			final long fixedTimeout = (long) (timeout == null ? ZAP_SPIDER_TIMEOUT : timeout) * 1000;
			clientApi.spider.setOptionMaxDepth(Constants.ZAP_API_KEY, fixedDepth);
			clientApi.spider.setOptionThreadCount(Constants.ZAP_API_KEY, DEFAULT_SPIDER_THREAD_COUNT);

			clientApi.spider.scan(
				Constants.ZAP_API_KEY,
				featureState.getUrlDetails().getDefaultUrl(),
				null,
				null,
				null,
				null);

			waitForSpiderToComplete(fixedDepth, fixedTimeout);
		}
	}

	@SuppressWarnings("BusyWait")
	private void waitForSpiderToComplete(final int depth, final long timeout) throws ClientApiException {
		checkArgument(depth > 0);
		checkArgument(timeout > 0);

		final double millisPerSecond = 1000.0d;

		final ClientApi clientApi = getClientApi();

		int status = 0;
		int counter99 = 0; //hack to detect a ZAP spider that gets stuck on 99%

		final long start = new Date().getTime();

		while (status < ZAP_DONE && new Date().getTime() - start < timeout) {
			status = Optional.of(clientApi.spider.scans())
				.map(a -> CastUtils.as(ApiResponseList.class, a))
				.filter(Objects::nonNull)
				.filter(a -> "scans".equals(a.getName()))
				.map(ApiResponseList::getItems)
				.map(Iterables::getLast)
				.map(a -> CastUtils.as(ApiResponseSet.class, a))
				.map(a -> a.getAttribute("progress"))
				.map(Integer::parseInt)
				.orElse(0);

			double timeoutIn = (timeout - (new Date().getTime() - start)) / millisPerSecond;

			LOGGER.info(
				"Spidering {}% done. Timing out in {} seconds",
				status,
				new DecimalFormat("#.##").format(timeoutIn));

			if (status >= ZAP_MINIMUM_DONE_BEFORE_COMPLETE) {
				counter99++;
			}
			if (counter99 > depth) {
				break;
			}
			try {
				Thread.sleep(ZAP_SLEEP);
			} catch (final InterruptedException ignored) {
				/*
					This is ignored
				 */
			}
		}

		clientApi.spider.stop(Constants.ZAP_API_KEY, null);
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	private Optional<String> getProxySetting(final String key) {
		checkArgument(StringUtils.isNotBlank(key));
		checkState(featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).isPresent());

		return featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME)
			.get().getProperties().entrySet().stream()
			.filter(e -> key.equals(e.getKey()))
			.filter(e -> Objects.nonNull(e.getValue()))
			.filter(e -> StringUtils.isNotBlank(e.getValue().toString()))
			.findAny()
			.map(e -> e.getValue().toString());
	}

	@SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
	private List<Alert> removeFalsePositivesFromAlerts(@NotNull final List<Alert> alerts) {
		checkNotNull(alerts);
		checkState(featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).isPresent());

		final Map<String, Object> properties =
			featureState.getProxyInterface(ZapProxyUtilsImpl.PROXY_NAME).get().getProperties();
		if (properties.containsKey(FALSE_POSITIVES_KEY)) {
			final List<ZAPFalsePositive> falsePositives =
				(List<ZAPFalsePositive>) properties.get(FALSE_POSITIVES_KEY);

			return alerts.stream()
				.filter(v ->
					!falsePositives.stream()
						.anyMatch(w -> w.matches(
							v.getUrl(),
							v.getParam(),
							v.getCweId(),
							v.getWascId()))
				)
				.collect(Collectors.toList());
		}

		return alerts;
	}

	private List<Alert> getAllAlertsByRiskRating(
		@NotNull final List<Alert> alerts,
		@NotNull final Alert.Risk rating,
		@NotNull final Optional<Pattern> regex) {
		checkNotNull(alerts);
		checkNotNull(rating);

		return alerts.stream()
			.filter(e -> e.getRisk().ordinal() >= rating.ordinal())
			.filter(e -> !regex.isPresent() || regex.get().matcher(e.getUrl()).find())
			.collect(Collectors.toList());
	}

	private String getAlertDetails(@NotNull final List<Alert> alerts) {
		checkNotNull(alerts);

		final StringBuilder detail = new StringBuilder();
		if (!alerts.isEmpty()) {
			for (Alert alert : alerts) {
				detail.append(alert.getAlert()).append("\n")
					.append("URL: ").append(alert.getUrl()).append("\n")
					.append("Parameter: ").append(alert.getParam()).append("\n")
					.append("CWE-ID: ").append(alert.getCweId()).append("\n")
					.append("WASC-ID: ").append(alert.getWascId()).append("\n");
			}
		}
		return detail.toString();
	}
}
