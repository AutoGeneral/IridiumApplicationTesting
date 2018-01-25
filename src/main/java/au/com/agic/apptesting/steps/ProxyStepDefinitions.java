package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.impl.BrowsermobProxyUtilsImpl;
import cucumber.api.java.en.When;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.vavr.control.Try;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.proxy.CaptureType;
import net.lightbody.bmp.util.HttpMessageContents;
import net.lightbody.bmp.util.HttpMessageInfo;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.EnumSet;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

/**
 * This class contains Gherkin step definitions relating to the use of the embedded
 * BrowserMob proxy.
 *
 * These steps have Atom snipptets that start with the prefix "block" and "delete".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class ProxyStepDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProxyStepDefinitions.class);

	@Autowired
	private AutoAliasUtils autoAliasUtils;

	/**
	 * EWnable HAR logging
	 */
	@When("^I enable HAR logging$")
	public void enableHar() {
		final Optional<ProxyDetails<?>> proxy =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		final EnumSet<CaptureType> captureTypes = CaptureType.getAllContentCaptureTypes();
		captureTypes.addAll(CaptureType.getCookieCaptureTypes());
		captureTypes.addAll(CaptureType.getHeaderCaptureTypes());

		proxy
			.flatMap(ProxyDetails::getInterface)
			.map(BrowserMobProxy.class::cast)
			.ifPresent(x -> {
				x.setHarCaptureTypes(captureTypes);
				x.newHar();
			});
	}

	/**
	 * Saves a HAR file with the details of the transactions that have passed through BrowserMob.
	 * This step is only required if you wish to save har files at particular points during the test.
	 * The HAR file is always saved when browsermob is shut down, meaning that once you begin the
	 * capture of the HAR file it will be saved regardless of the success or failure of the test.
	 * @param alias If this word is found in the step, it means the filename is found from the
	 *              data set.
	 * @param filename The optional filename to use for the HAR file
	 */
	@When("^I dump the HAR file(?: to( alias)? \"(.*?)\")?$")
	public void saveHarFile(final String alias, final String filename) {

		final String fixedFilename = autoAliasUtils.getValue(
			StringUtils.defaultString(filename, Constants.HAR_FILE_NAME),
			StringUtils.isNotBlank(alias),
			State.getFeatureStateForThread());

		final Optional<ProxyDetails<?>> proxy =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		proxy
			.flatMap(ProxyDetails::getInterface)
			.map(BrowserMobProxy.class::cast)
			.map(x -> Try.run(() -> {
				final Har har = x.getHar();

				checkState(
					har != null,
					"You need to add the step \"I enable HAR logging\" before saving the HAR file");

				final File file = new File(
					State.getFeatureStateForThread().getReportDirectory()
						+ "/"
						+ fixedFilename);
				har.writeTo(file);
			}));
	}

	/**
	 * Block access to all urls that match the regex
	 *
	 * @param url      A regular expression that matches URLs to be blocked
	 * @param response The response code to send back when a matching URL is accessed
	 */
	@When("^I block access to the URL regex \"(.*?)\" with response \"(\\d+)\"$")
	public void blockUrl(final String url, final Integer response) {
		final Optional<ProxyDetails<?>> proxy =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		proxy
			.flatMap(ProxyDetails::getInterface)
			.map(BrowserMobProxy.class::cast)
			.ifPresent(x -> {
				x.blacklistRequests(url, response);
			});
	}

	/**
	 * Block access to all urls that match the regex
	 *
	 * @param url      A regular expression that matches URLs to be blocked
	 * @param response The response code to send back when a matching URL is accessed
	 * @param type     The http type of request to block (CONNECT, GET, PUT etc)
	 */
	@When("^I block access to the URL regex \"(.*?)\" of the type \"(.*?)\" with response \"(\\d+)\"$")
	public void blockUrl(final String url, final String type, final Integer response) {
		final Optional<ProxyDetails<?>> proxy =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		proxy
			.flatMap(ProxyDetails::getInterface)
			.map(BrowserMobProxy.class::cast)
			.ifPresent(x -> {
				x.blacklistRequests(url, response, type);
			});
	}

	/**
	 * Allow access to all urls that match the regex
	 *
	 * @param response      The response code for all whitelisted urls
	 */
	@When("^I enable the whitelist with responding with \"(\\d+)\" for unmatched requests$")
	public void enableWhitelist(final Integer response) {
		final Optional<ProxyDetails<?>> proxy =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		proxy
			.flatMap(ProxyDetails::getInterface)
			.map(BrowserMobProxy.class::cast)
			.ifPresent(x -> {
				x.enableEmptyWhitelist(response);
			});
	}

	/**
	 * Allow access to all urls that match the regex
	 *
	 * @param url      A regular expression that matches URLs to be allowed
	 */
	@When("^I allow access to the URL regex \"(.*?)\"$")
	public void allowUrl(final String url) {
		final Optional<ProxyDetails<?>> proxy =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		proxy
			.flatMap(ProxyDetails::getInterface)
			.map(BrowserMobProxy.class::cast)
			.ifPresent(x -> {
				x.addWhitelistPattern(url);
			});
	}

	/**
	 * Apps like life express will often include AWSELB cookies from both the root "/" context and the
	 * application "/life-express" context. Supplying both cookies means that requests are sent to a EC2
	 * instance that didn't generate the initial session, and so the request fails. This step allows us to
	 * remove these duplicated cookies from the request.
	 *
	 * @param url The regex that matches URLs that should have duplicate AWSELB cookies removed
	 */
	@When("^I (?:remove|delete) root AWSELB cookie from the request to the URL regex \"(.*?)\"$")
	public void stripHeaders(final String url) {
		final Optional<ProxyDetails<?>> proxy =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		/*
			Get the name of the thread running the test
		 */
		final String threadName = Thread.currentThread().getName();


		proxy
			.flatMap(ProxyDetails::getInterface)
			.map(BrowserMobProxy.class::cast)
			.ifPresent(x -> {
				x.addRequestFilter(new RequestFilter() {
					@Override
					public HttpResponse filterRequest(
						final HttpRequest request,
						final HttpMessageContents contents,
						final HttpMessageInfo messageInfo) {
						if (messageInfo.getOriginalRequest().getUri().matches(url)) {
							final Optional<String> cookies =
								Optional.ofNullable(request.headers().get("Cookie"));

						/*
							Only proceed if we have supplied some cookies
						 */
							if (cookies.isPresent()) {
							/*
								Find the root context cookie
							 */
								final WebDriver webDriver =
									State
										.getThreadDesiredCapabilityMap()
										.getWebDriverForThread(threadName, true);

								final Optional<Cookie> awselb =
									webDriver.manage().getCookies()
										.stream()
										.filter(x -> "AWSELB".equals(x.getName()))
										.filter(x -> "/".equals(x.getPath()))
										.findFirst();

							/*
								If we have a root context cookie,
								remove it from the request
							 */
								if (awselb.isPresent()) {

									LOGGER.info(
										"WEBAPPTESTER-INFO-0002: "
											+ "Removing AWSELB cookie with value {}",
										awselb.get().getValue());

									final String newCookie =
										cookies.get().replaceAll(awselb.get().getName()
												+ "="
												+ awselb.get().getValue() + ";"
												+ "( "
												+ "GMT=; "
												+ "\\d+-\\w+-\\d+=\\d+:\\d+:\\d+;"
												+ ")?",
											"");

									request.headers().set("Cookie", newCookie);
								}

								final int awsElbCookieCount = StringUtils.countMatches(
									request.headers().get("Cookie"),
									"AWSELB");

								if (awsElbCookieCount != 1) {
									LOGGER.info(
										"WEBAPPTESTER-INFO-0003: "
											+ "{} AWSELB cookies found",
										awsElbCookieCount);
								}
							}

						}

						return null;
					}
				});
			});
	}

	/**
	 * Set new or modify an existing HTTP header
	 *
	 * @param headerName HTTP header name
	 * @param headerValue HTTP header value
	 */
	@When("^I set header \"([^\"]*)\" with value \"([^\"]*)\"$")
	public void changeHeader(final String headerName, final String headerValue) {
		final Optional<ProxyDetails<?>> proxy =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		proxy
			.flatMap(ProxyDetails::getInterface)
			.map(BrowserMobProxy.class::cast)
			.ifPresent(x -> x.addRequestFilter((request, contents, messageInfo) -> {
				request.headers().set(headerName, headerValue);
				return null;
			}));
	}

	/**
	 * Remove HTTP header
	 *
	 * @param headerName HTTP header name
	 */
	@When("^I remove header \"([^\"]*)\"$")
	public void removeHeader(final String headerName) {
		final Optional<ProxyDetails<?>> proxy =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		proxy
			.flatMap(ProxyDetails::getInterface)
			.map(BrowserMobProxy.class::cast)
			.ifPresent(x -> x.addRequestFilter((request, contents, messageInfo) -> {
				request.headers().remove(headerName);
				return null;
			}));
	}
}
