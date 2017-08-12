package au.com.agic.apptesting.utils;

import javaslang.Tuple2;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * A service for working with remote tests servers like BrowserStack
 */
public interface RemoteTestsUtils {
	/**
	 * Download the video recording
	 * @param sessionID The remote session ID
	 * @param destination Where to save the video
	 */
	void saveVideoRecording(@NotNull final String sessionID, @NotNull final String destination);

	/**
	 * @return The Browserstack credentials
	 */
	Optional<Tuple2<String, String>> getCredentials();

	/**
	 * @return The remote session ID
	 */
	Optional<String> getSessionID();
}
