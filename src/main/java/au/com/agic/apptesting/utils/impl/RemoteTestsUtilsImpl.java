package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.profiles.FileProfileAccess;
import au.com.agic.apptesting.profiles.configuration.Configuration;
import au.com.agic.apptesting.utils.BrowserDetection;
import au.com.agic.apptesting.utils.RemoteTestsUtils;
import au.com.agic.apptesting.utils.RetryService;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.control.Try;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.support.RetryTemplate;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Steps that are specific to Auto and General web apps
 */
public class RemoteTestsUtilsImpl implements RemoteTestsUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteTestsUtilsImpl.class);
	private BrowserDetection browserDetection = new BrowserDetectionImpl();
	private SystemPropertyUtils systemPropertyUtils = new SystemPropertyUtilsImpl();
	private RetryService retryService = new RetryServiceImpl();
	private final FileProfileAccess<Configuration> profileAccess = new FileProfileAccess<>(
		systemPropertyUtils.getProperty(Constants.CONFIGURATION),
		Configuration.class);

	@Override
	public void saveVideoRecording(@NotNull final String sessionID, @NotNull final String destination) {
		checkArgument(StringUtils.isNotBlank(sessionID));
		checkArgument(StringUtils.isNotBlank(destination));

		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final boolean isRemote = browserDetection.isRemote(webDriver);
		final boolean downloadVideo = systemPropertyUtils.getPropertyAsBoolean(
			Constants.DOWNLOAD_BROWSERSTACK_VIDEO_ON_COMPLETION,
			false);

		if (isRemote && downloadVideo) {
			final Optional<Tuple2<String, String>> credentials = getCredentials();
			if (credentials.isPresent()) {
				final RetryTemplate template = retryService.getRetryTemplate();
				final Executor executor = Executor.newInstance()
					.auth(new HttpHost("www.browserstack.com"), credentials.get()._1(), credentials.get()._2())
					.authPreemptive(new HttpHost("www.browserstack.com"));

				Try.of(template.execute(context -> {
					Try.of(() -> "https://www.browserstack.com/automate/sessions/" + sessionID + ".json")
						.mapTry(url -> executor.execute(Request.Get(url)
							.connectTimeout(Constants.HTTP_TIMEOUTS)
							.socketTimeout(Constants.HTTP_TIMEOUTS))
							.returnContent().asString())
						.mapTry(JSONObject::new)
						.mapTry(jsonObject -> jsonObject.getJSONObject("automation_session"))
						.mapTry(jsonObject -> jsonObject.getString("video_url"))
						.mapTry(url -> Request.Get(url)
							.connectTimeout(Constants.HTTP_TIMEOUTS)
							.socketTimeout(Constants.HTTP_TIMEOUTS)
							.execute().returnContent().asStream())
						.mapTry(stream -> {
							final File file = new File(destination + File.separator +
									Constants.BROWSERSTACK_VIDEO_FILE_NAME + ".mp4");
							FileUtils.copyInputStreamToFile(stream, file);
							return file;
						})
						.andThenTry(file -> LOGGER.info("Saved BrowserStack video file to " + file.getCanonicalPath()))
						.getOrElseThrow(ex -> new RuntimeException(ex));

					return null;
				}))
					.onFailure(ex -> LOGGER.error("WEBAPPTESTER-BUG-0011: Failed to save BrowserStack video file.", ex));
			}
		}
	}

	@Override
	public Optional<Tuple2<String, String>> getCredentials() {
		return Stream.of(loadDetailsFromSysProps(), loadDetailsFromProfile())
			.filter(Optional::isPresent)
			.map(Optional::get)
			.findFirst();
	}

	@Override
	public Optional<String> getSessionID() {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		if (browserDetection.isRemote(webDriver)) {
			return Optional.of(RemoteWebDriver.class.cast(webDriver))
				.map(RemoteWebDriver::getSessionId)
				.map(Object::toString);
		}
		return Optional.empty();
	}


	private Optional<Tuple2<String, String>> loadDetailsFromProfile() {
		final Optional<Configuration> profile = profileAccess.getProfile();
		if (profile.isPresent()) {
			return Optional.of(Tuple.of(
				profile.get().getBrowserstack().getUsername(),
				profile.get().getBrowserstack().getAccessToken()));
		}

		return Optional.empty();
	}

	private Optional<Tuple2<String, String>> loadDetailsFromSysProps() {
		final Optional<String> browserStackUsername =
			Optional.ofNullable(systemPropertyUtils.getPropertyEmptyAsNull(Constants.BROWSER_STACK_USERNAME));
		final Optional<String> browserStackAccessToken =
			Optional.ofNullable(systemPropertyUtils.getPropertyEmptyAsNull(Constants.BROWSER_STACK_ACCESS_TOKEN));

		if (browserStackUsername.isPresent() && browserStackAccessToken.isPresent()) {
			return Optional.of(Tuple.of(browserStackUsername.get(), browserStackAccessToken.get()));
		}

		return Optional.empty();
	}
}
