package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.profiles.FileProfileAccess;
import au.com.agic.apptesting.profiles.configuration.Configuration;
import au.com.agic.apptesting.utils.BrowserDetection;
import au.com.agic.apptesting.utils.RemoteTestsUtils;
import au.com.agic.apptesting.utils.RetryService;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.json.JSONObject;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Steps that are specific to Auto and General web apps
 */
public class RemoteTestsUtilsImpl implements RemoteTestsUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(RemoteTestsUtilsImpl.class);
	private static final Pattern SESSION_ID_REGEX = Pattern.compile("[a-f0-9]{40}");
	private BrowserDetection browserDetection = new BrowserDetectionImpl();
	private SystemPropertyUtils systemPropertyUtils = new SystemPropertyUtilsImpl();
	private RetryService retryService = new RetryServiceImpl();
	private final FileProfileAccess<Configuration> profileAccess = new FileProfileAccess<>(
		systemPropertyUtils.getProperty(Constants.CONFIGURATION),
		Configuration.class);

	private boolean shouldDownloadVideoFile() {
		return Option.of(State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread())
			.map(browserDetection::isRemote)
			.filter(isRemote -> isRemote && systemPropertyUtils.getPropertyAsBoolean(
				Constants.DOWNLOAD_BROWSERSTACK_VIDEO_ON_COMPLETION,
				false))
			.isDefined();
	}

	private Option<Executor> getExecutor() {
		return getCredentials()
			.map(creds -> Executor.newInstance()
				.auth(new HttpHost("www.browserstack.com"), creds._1(), creds._2())
				.authPreemptive(new HttpHost("www.browserstack.com")));
	}

	private File shouldDownloadVideoFile(@NotNull final Executor executor,
										 @NotNull final String sessionID,
										 @NotNull final String destination) {
		checkNotNull(executor);
		checkArgument(StringUtils.isNotBlank(sessionID));
		checkArgument(StringUtils.isNotBlank(destination));

		return retryService.getRetryTemplate().execute(context ->
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
				.getOrElseThrow(ex -> new RuntimeException(ex))
		);
	}

	@Override
	public void saveVideoRecording(@NotNull final String sessionID, @NotNull final String destination) {
		checkArgument(StringUtils.isNotBlank(sessionID));
		checkArgument(StringUtils.isNotBlank(destination));

		if (shouldDownloadVideoFile()) {
			getExecutor().toTry()
				.mapTry(executor -> shouldDownloadVideoFile(executor, sessionID, destination))
				.onFailure(ex -> LOGGER.error("WEBAPPTESTER-BUG-0011: Failed to save BrowserStack video file.", ex));
		}
	}

	@Override
	public Option<Tuple2<String, String>> getCredentials() {
		return loadDetailsFromSysProps()
			.orElse(loadDetailsFromProfile());
	}

	@Override
	public Option<String> getSessionID() {
		return Try.of(() -> State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread())
			.mapTry(RemoteWebDriver.class::cast)
			.map(RemoteWebDriver::toString)
			.map(SESSION_ID_REGEX::matcher)
			.filter(Matcher::find)
			.map(Matcher::group)
			.toOption();
	}


	private Option<Tuple2<String, String>> loadDetailsFromProfile() {
		return Option.ofOptional(profileAccess.getProfile())
			.map(profile -> Tuple.of(
				profile.getBrowserstack().getUsername(),
				profile.getBrowserstack().getAccessToken()))
			.filter(tuple -> StringUtils.isNotBlank(tuple._1) && StringUtils.isNotBlank(tuple._2));
	}

	private Option<Tuple2<String, String>> loadDetailsFromSysProps() {
		return Option.of(Tuple.of(
			systemPropertyUtils.getPropertyEmptyAsNull(Constants.BROWSER_STACK_USERNAME),
			systemPropertyUtils.getPropertyEmptyAsNull(Constants.BROWSER_STACK_ACCESS_TOKEN)))
			.filter(tuple -> StringUtils.isNotBlank(tuple._1) && StringUtils.isNotBlank(tuple._2));
	}
}
