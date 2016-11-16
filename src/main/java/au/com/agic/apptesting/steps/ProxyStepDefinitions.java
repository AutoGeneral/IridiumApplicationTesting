package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkState;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.impl.BrowsermobProxyUtilsImpl;

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
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

import cucumber.api.java.en.When;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import javaslang.control.Try;

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

	/**
	 * EWnable HAR logging
	 */
	@When("^I enable HAR logging$")
	public void enableHar() {
		final Optional<ProxyDetails<?>> proxy =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		proxy
			.map(ProxyDetails::getInterface)
			.map(BrowserMobProxy.class::cast)
			.ifPresent(x -> {
				x.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
				x.newHar();
			});
	}

	/**
	 * Saves a HAR file with the details of the transactions that have passed through BorwserMob
	 * @param filename The optional filename to use for the HAR file
	 * @throws IOException Thrown when there is an issue saving the HAR file
	 */
	@When("^I dump the HAR file(?: to \"(.*?)\")?$")
	public void saveHarFile(final String filename) {
		final Optional<ProxyDetails<?>> proxy =
			State.getFeatureStateForThread().getProxyInterface(BrowsermobProxyUtilsImpl.PROXY_NAME);

		proxy
			.map(ProxyDetails::getInterface)
			.map(BrowserMobProxy.class::cast)
			.map(x -> Try.run(() -> {
				final String fixedFilename = StringUtils.defaultString(filename, "browsermob.har");
				final Har har = x.getHar();

				checkState(
					har != null,
					"You need to add the step \"I enable HAR logging\" before saving the HAR file");

				final File file = new File(State.getFeatureStateForThread().getReportDirectory() + "/" + fixedFilename);
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
			.map(ProxyDetails::getInterface)
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
			.map(ProxyDetails::getInterface)
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
			.map(ProxyDetails::getInterface)
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
			.map(ProxyDetails::getInterface)
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
			.map(ProxyDetails::getInterface)
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
										.THREAD_DESIRED_CAPABILITY_MAP
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
}
