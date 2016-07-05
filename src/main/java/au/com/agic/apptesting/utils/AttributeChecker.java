package au.com.agic.apptesting.utils;

import au.com.agic.apptesting.profiles.configuration.FeatureGroup;

import javax.validation.constraints.NotNull;

/**
 * Utility methods for checking the state of a xml node
 */
public interface AttributeChecker {

	/**
	 * @param featureGroup The feature Group record to test
	 * @return true if the setting has an attribute called "enabled" set to "true", and false
	 * otherwise
	 */
	boolean isSingleSettingEnabled(@NotNull final FeatureGroup featureGroup);

	/**
	 * @param featureGroup The feature Group record to test
	 * @param group        The group to test for
	 * @return true if the setting has an attribute called "group" that includes the supplied group,
	 * and false otherwise
	 */
	boolean isSingleSettingInGroup(@NotNull final FeatureGroup featureGroup, final String group);

	/**
	 * @param featureGroup The feature Group record to test
	 * @param app          The name of the application to test for
	 * @return true if the setting has an attribute called "app" that includes the supplied app, and
	 * false otherwise
	 */
	boolean isSingleSettingForApp(@NotNull final FeatureGroup featureGroup, @NotNull final String app);
}
