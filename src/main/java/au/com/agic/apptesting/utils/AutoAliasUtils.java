package au.com.agic.apptesting.utils;

import javax.validation.constraints.NotNull;

/**
 * A service that supports the autoalias functionality
 */
public interface AutoAliasUtils {

	/**
	 *
	 * @param value The value passed in, which could represent an alias or a raw value
	 * @param featureState The statue of the feature
	 * @return The value to be used when finding an element
	 */
	String getValue(@NotNull final String value, boolean forceAlias, @NotNull final FeatureState featureState);
}
