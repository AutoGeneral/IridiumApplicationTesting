package au.com.agic.apptesting.utils;

import javax.validation.constraints.NotNull;
import java.time.temporal.ChronoUnit;

/**
 * A service for converting strings to chrono units
 */
public interface ChronoConverterUtils {

	/**
	 *
	 * @param input The string representation of the chrono unit
	 * @return The matching ChronoUnit
	 */
	ChronoUnit fromString(@NotNull String input);
}
