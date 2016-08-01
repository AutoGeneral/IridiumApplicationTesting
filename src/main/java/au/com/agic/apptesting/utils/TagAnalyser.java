package au.com.agic.apptesting.utils;

import java.util.List;

/**
 * A module for converting tags strings (i.e. tags defined in xml attributes) into lists of tags to
 * be passed to the Cucumber cli.
 */
public interface TagAnalyser {

	List<String> convertTagsToList(final String tags);
}
