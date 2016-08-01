package au.com.agic.apptesting.utils;

import au.com.agic.apptesting.utils.impl.FileDetails;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Useful methods for dealing with feature files
 */
public interface FeatureFileUtils {

	/**
	 * @param path         The path to check for feature files
	 * @param featureGroup The name of the feature group to look for
	 * @param baseUrl      The optional base url that imported files can be downloaded from
	 * @return A collection of feature files
	 */
	List<FileDetails> getFeatureScripts(
		@NotNull final String path,
		final String featureGroup,
		final String baseUrl);
}
