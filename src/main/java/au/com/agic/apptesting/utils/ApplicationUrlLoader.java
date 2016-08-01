package au.com.agic.apptesting.utils;

import au.com.agic.apptesting.profiles.configuration.UrlMapping;

import java.util.List;
import java.util.Map;

/**
 * A service for loading the urls associated with an application. We do this because each application typically has a
 * few dozen different varieties or brands that we need to test. By mapping a test to an application, and an application
 * to multiple urls, we have a convenient way to create one test and run it across all varieties.
 */
public interface ApplicationUrlLoader {

	/**
	 * @param featureGroup The name of the application, matched to the step "I open the application"
	 * @return The list of URLs mapped to the application
	 */
	List<UrlMapping> getAppUrls(final String featureGroup);

	/**
	 * @return The mapping between data set keys and values for groups of data sets
	 */
	Map<Integer, Map<String, String>> getDatasets();
}
