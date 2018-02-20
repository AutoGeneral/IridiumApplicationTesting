package au.com.agic.apptesting.utils;

import au.com.agic.apptesting.utils.impl.FileDetails;

import javax.validation.constraints.NotNull;
import java.util.List;

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
		@NotNull String path,
		String featureGroup,
		String baseUrl);
}
