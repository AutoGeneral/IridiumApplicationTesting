package au.com.agic.apptesting.utils;

import javax.validation.constraints.NotNull;

/**
 * A service for configuring the logging
 */
public interface LoggingConfiguration {
	void configureLogging(@NotNull final String logfile);
}
