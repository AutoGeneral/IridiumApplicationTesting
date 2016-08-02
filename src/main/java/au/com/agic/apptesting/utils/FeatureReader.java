package au.com.agic.apptesting.utils;

import javax.validation.constraints.NotNull;
import java.io.File;

/**
 * Utils methods for scanning feature files
 */
public interface FeatureReader {

	/**
	 * @param file The file being scanned
	 * @param app  The app we are testing against
	 * @return true if the file indicated that it is a test for the app, and false otherwise
	 */
	boolean selectFile(@NotNull final File file, @NotNull final String app);

	/**
	 * @param featureGroupList A comma separated list of feature groups
	 * @param featureGroup     A feature group to match to the list in featureGroupList
	 * @return true if featureGroup is found in featureGroupList, false otherwise
	 */
	boolean matchesFeatureGroup(@NotNull final String featureGroupList, @NotNull final String featureGroup);
}
