package au.com.agic.apptesting.utils;

import javax.validation.constraints.NotNull;

/**
 * Services for saving exceptions to a log file on disk
 */
public interface ExceptionWriter {

	void saveException(@NotNull final String location, @NotNull final Exception exception);
}
