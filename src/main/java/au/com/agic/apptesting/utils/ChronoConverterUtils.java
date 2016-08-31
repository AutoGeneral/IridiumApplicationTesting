package au.com.agic.apptesting.utils;

import java.time.temporal.ChronoUnit;

import javax.validation.constraints.NotNull;

/**
 * A service for converting strings to chrono units
 */
public interface ChronoConverterUtils {

	/**
	 *
	 * @param input The string representation of the chrono unit
	 * @return The matching ChronoUnit
	 */
	ChronoUnit fromString(@NotNull final String input);
}
