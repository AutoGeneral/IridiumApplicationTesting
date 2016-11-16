package au.com.agic.apptesting.utils;

import javax.validation.constraints.NotNull;

/**
 * Methods for dealing with cross platform issues
 */
public interface CrossPlatformUtils {

	/**
	 * @param input The string to normalise
	 * @return The input string with consistent line breaks
	 */
	String normaliseLineBreaks(@NotNull String input);
}
