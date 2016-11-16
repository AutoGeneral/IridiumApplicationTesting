package au.com.agic.apptesting.utils;

import javax.validation.constraints.NotNull;

/**
 * An interface for a screen recorder
 */
public interface ScreenCapture {

	/**
	 * Begin a screen capture
	 *
	 * @param saveDir The directory that the screen recording will be saved into
	 */
	void start(@NotNull String saveDir);

	/**
	 * Stop a screen capture
	 */
	void stop();
}
