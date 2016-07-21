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
	 * Extracts system properties, either from their default name, or with the javaws prefix.
	 * Treats empty strings as null.
	 *
	 * @param name The name of the system property
	 * @return The value of the system property
	 */
	String getPropertyEmptyAsNull(final String name);

	/**
	 * Copies system properties from the javaws prefixed namespace into the default namespace
	 *
	 * @param name The name of the variable, excluding the javaws prefix
	 */
	void copyVariableToDefaultLocation(final String name);

	/**
	 * Some system properties are defined by external dependencies that can't be passed in
	 * directly by Web Start, so we copy them from the web start jnlp prefixed properties
	 * to the standard properties
	 */
	void copyDependentSystemProperties();
}
