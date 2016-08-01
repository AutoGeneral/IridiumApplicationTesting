package au.com.agic.apptesting.utils;

import au.com.agic.apptesting.utils.impl.FileDetails;

import java.io.File;

import javax.validation.constraints.NotNull;

/**
 * An interface for a module that can import the contents of a file into another one based on a
 * comment marker.
 */
public interface FeatureFileImporter {

	File processFeatureImportComments(@NotNull final FileDetails file, final String baseUrl);
}
