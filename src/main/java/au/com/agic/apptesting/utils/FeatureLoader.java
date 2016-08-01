package au.com.agic.apptesting.utils;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * A service that loads features from some location and puts them in a location on the disk for
 * Cucumber to find.
 */
public interface FeatureLoader {

	/**
	 * Load the features from some external source and save them to the disk
	 *
	 * @param identifier Some identifier that the implementing class can use to find the features
	 * @param group      The group that the features have to belong to to be included
	 * @param app        The name of the application we are testing
	 * @return The path where the features were saved, so Cucumber can load them
	 */
	@NotNull
	String loadFeatures(final String identifier, final String app, final String group);

	/**
	 * Load the features from some external source and save them to the disk
	 *
	 * @param identifiers Some identifiers that the implementing class can use to find the features
	 * @param app         The name of the application we are testing
	 * @return The path where the features were saved, so Cucumber can load them
	 */
	@NotNull
	String loadFeatures(@NotNull final List<String> identifiers, final String app);
}
