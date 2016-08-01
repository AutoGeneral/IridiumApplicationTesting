package au.com.agic.apptesting.utils;

import javax.validation.constraints.NotNull;

/**
 * A servcie for taking screenshots
 */
public interface ScreenshotUtils {
	void takeScreenshot(@NotNull final String suffix, @NotNull final ThreadDetails threadDetails);
}
