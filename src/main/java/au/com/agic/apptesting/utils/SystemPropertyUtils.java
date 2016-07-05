package au.com.agic.apptesting.utils;

/**
 * Defines a service for working with system properties
 */
public interface SystemPropertyUtils {

	/**
	 * Extracts system properties, either from their default name, or with the javaws prefix
	 *
	 * @param name The name of the system property
	 * @return The value of the system property
	 */
	String getProperty(final String name);

	/**
	 * Copies system properties from the javaws prefixed namespace into the default namespace
	 *
	 * @param name The name of the variable, excluding the javaws prefix
	 */
	void copyVariableToDefaultLocation(final String name);
}
