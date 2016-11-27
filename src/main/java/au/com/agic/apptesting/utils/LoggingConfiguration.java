package au.com.agic.apptesting.utils;

import javax.validation.constraints.NotNull;

/**
 * A service for configuring the logging
 */
public interface LoggingConfiguration {

	/**
	 * Configure the logging subsystem
	 * @param logfile The path to the log file that should have logging sent to it
	 */
	void configureLogging(@NotNull String logfile);

	/**
	 * Write out the current version of the application to the logs
	 */
	void logVersion();
}
