package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.ScreenshotUtils;
import au.com.agic.apptesting.utils.SystemPropertyUtils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotNull;

/**
 * And implementation of the screenshot service
 */
public class ScreenshotUtilsImpl implements ScreenshotUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotUtilsImpl.class);
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();
	private static final String SCREENSHOT_DATE_FORMAT = "YYYYMMddHHmmssSSS";

	@Override
	public void takeScreenshot(@NotNull final String suffix, @NotNull final FeatureState featureState) {
		checkNotNull(suffix);
		checkNotNull(featureState);

		/*
			Take a screenshot if we have enabled the setting
		 */
		final boolean enabledScreenshots = Boolean.parseBoolean(
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.ENABLE_SCREENSHOTS));

		try {
			if (enabledScreenshots) {
				final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
				if (webDriver instanceof TakesScreenshot) {
					final File screenshot =
						((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);

					/*
						Screenshot filenames are the time that it was taken to allow for easy
						sorting.
					 */
					final String filename = new SimpleDateFormat(SCREENSHOT_DATE_FORMAT)
						.format(new Date()) + suffix + ".png";

					final File reportFile =
						new File(featureState.getReportDirectory() + "/" + filename);

					FileUtils.copyFile(screenshot, reportFile);
					LOGGER.info("Saved screenshot to {}", reportFile.getAbsolutePath());
				}
			}
		} catch (final WebDriverException ex) {
			/*
				There is a known bug when attempting to get screenshots in Windows
				with Firefox, so don't let this exception break a test.
			 */
			if (ex.getMessage().contains("Could not convert screenshot to base64")) {
				LOGGER.error("Could nto save screenshot because of a know bug "
					+ "with WebDriver and Firefox. See "
					+ "https://github.com/seleniumhq/selenium/issues/1097 "
					+ "for more details", ex);
			} else {
				throw ex;
			}
		} catch (final IOException ex) {
			LOGGER.error("There was an error saving or copying the screenshot.", ex);
		}
	}
}
