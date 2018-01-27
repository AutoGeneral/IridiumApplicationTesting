package au.com.agic.apptesting.utils;

import java.util.List;
import java.util.Optional;

/**
 * Defines a service for working with system properties
 */
public interface SystemPropertyUtils {

	/**
	 *
	 * @return A list of all the system properties with web start prefixes removed
	 */
	List<String> getNormalisedProperties();

	/**
	 * Extracts system properties, either from their default name, or with the javaws prefix
	 *
	 * @param name The name of the system property
	 * @return The value of the system property
	 */
	String getProperty(String name);

	/**
	 * Gets a system property as a boolean
	 * @param name The name of the property
	 * @param defaultValue The default value if the property is empty or null
	 * @return the boolean value of the system property, or the default value if the system property is empty
	 */
	boolean getPropertyAsBoolean(String name, boolean defaultValue);

	/**
	 * Gets a system property as a float
	 * @param name The name of the property
	 * @param defaultValue The default value if the property is empty or null
	 * @return the boolean value of the system property, or the default value if the system property is empty
	 */
	float getPropertyAsFloat(String name, float defaultValue);

	/**
	 * Gets a system property as a int
	 * @param name The name of the property
	 * @param defaultValue The default value if the property is empty or null
	 * @return the boolean value of the system property, or the default value if the system property is empty
	 */
	int getPropertyAsInt(String name, int defaultValue);

	/**
	 * Extracts system properties, either from their default name, or with the javaws prefix.
	 * Treats empty strings as null.
	 *
	 * @param name The name of the system property
	 * @return The value of the system property
	 */
	String getPropertyEmptyAsNull(String name);

	/**
	 * Extracts system properties, either from their default name, or with the javaws prefix.
	 * Treats empty strings as empty optional.
	 *
	 * @param name The name of the system property
	 * @return The value of the system property
	 */
	Optional<String> getPropertyEmptyAsOptional(String name);

	/**
	 * Copies system properties from the javaws prefixed namespace into the default namespace
	 *
	 * @param name The name of the variable, excluding the javaws prefix
	 */
	void copyVariableToDefaultLocation(String name);

	/**
	 * Some system properties are defined by external dependencies that can't be passed in
	 * directly by Web Start, so we copy them from the web start jnlp prefixed properties
	 * to the standard properties
	 */
	void copyDependentSystemProperties();
}
