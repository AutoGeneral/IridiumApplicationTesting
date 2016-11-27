package au.com.agic.apptesting.utils;

import java.io.File;

import javax.validation.constraints.NotNull;

/**
 * Utils methods for scanning feature files
 */
public interface FeatureReader {

	/**
	 * @param file The file being scanned
	 * @param app  The app we are testing against
	 * @return true if the file indicated that it is a test for the app, and false otherwise
	 */
	boolean selectFile(@NotNull File file, @NotNull String app);

	/**
	 * @param featureGroupList A comma separated list of feature groups
	 * @param featureGroup     A feature group to match to the list in featureGroupList
	 * @return true if featureGroup is found in featureGroupList, false otherwise
	 */
	boolean matchesFeatureGroup(@NotNull String featureGroupList, @NotNull String featureGroup);
}
