package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.ExceptionWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of exception saving service
 */
public class ExceptionWriterImpl implements ExceptionWriter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionWriterImpl.class);
	private static final String EXCEPTION_FILE_NAME = "exception.txt";

	@Override
	public void saveException(@NotNull final String location, @NotNull final Throwable exception) {
		checkArgument(StringUtils.isNoneBlank(location));
		checkNotNull(exception);

		final String message = ExceptionUtils.getMessage(exception);
		final String stackTrace = ExceptionUtils.getStackTrace(exception);

		try {
			FileUtils.writeStringToFile(
				new File(location + "/" + EXCEPTION_FILE_NAME),
				message + "\n" + stackTrace + "\n",
				true);

			if (exception.getCause() != null) {
				saveException(location, exception.getCause());
			}
		} catch (final IOException ex) {
			LOGGER.error("WEBAPPTESTER-BUG-0005: Could not write exception details to log file", ex);
		}
	}
}
