package au.com.agic.apptesting.utils;

import javax.validation.constraints.NotNull;

/**
 * A servcie for taking screenshots
 */
public interface ScreenshotUtils {
	void takeScreenshot(@NotNull String suffix, @NotNull FeatureState featureState);
}
