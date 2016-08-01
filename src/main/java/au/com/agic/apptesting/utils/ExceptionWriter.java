package au.com.agic.apptesting.utils;

import javax.validation.constraints.NotNull;

/**
 * Services for saving exceptions to a log file on disk
 */
public interface ExceptionWriter {

	/**
	 * Writes out the details of the exception to a file
	 * @param location The location of the exception file
	 * @param exception The exception to save
	 */
	void saveException(@NotNull final String location, @NotNull final Throwable exception);
}
