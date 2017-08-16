package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.exception.FeatureFilesException;
import au.com.agic.apptesting.exception.NoFeaturesException;
import au.com.agic.apptesting.utils.FeatureFileImporter;
import au.com.agic.apptesting.utils.FeatureFileUtils;
import au.com.agic.apptesting.utils.FeatureLoader;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import io.vavr.control.Try;
import org.apache.commons.io.FileUtils;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A feature loaded that just returns a local path name that contains existing feature files
 */

public class LocalPathFeatureLoaderImpl implements FeatureLoader {

	private static final FeatureFileUtils FEATURE_FILE_UTILS = new FeatureFileUtilsImpl();
	private static final FeatureFileImporter FEATURE_FILE_IMPORTER = new FeatureFileImporterImpl();
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	/**
	 * @return The path defined by the testSource system property
	 */
	@NotNull
	@Override
	public File loadFeatures(
		final String identifier,
		final String featureGroup) {
		return loadFeatures(Arrays.asList(identifier), featureGroup);
	}

	/**
	 * @return The path defined by the testSource system property
	 */
	@NotNull
	@Override
	public File loadFeatures(
			@NotNull final List<String> identifier,
			final String featureGroup) {
		checkNotNull(identifier);

		try {
			final List<FileDetails> filteredFiles = FEATURE_FILE_UTILS.getFeatureScripts(
				SYSTEM_PROPERTY_UTILS.getProperty(Constants.TEST_SOURCE_SYSTEM_PROPERTY),
				featureGroup,
				SYSTEM_PROPERTY_UTILS.getProperty(Constants.IMPORT_BASE_URL));

			if (filteredFiles.isEmpty()) {
				throw new NoFeaturesException("No matching feature files were found. If you "
					+ "specified a directory for the " + Constants.TEST_SOURCE_SYSTEM_PROPERTY
					+ " system property, make sure you also supplied a matching "
					+ Constants.FEATURE_GROUP_SYSTEM_PROPERTY + " system property");
			}

			final Path temp2 = Files.createTempDirectory(null);

			filteredFiles.stream()
				.map(e -> FEATURE_FILE_IMPORTER.processFeatureImportComments(
					e,
					SYSTEM_PROPERTY_UTILS.getProperty(Constants.IMPORT_BASE_URL)))
				.peek(e -> Try.run(() -> {
					FileUtils.copyFileToDirectory(e, temp2.toFile());
				}))
				.forEach(FileUtils::deleteQuietly);

			/*
				Delete any files we downloaded
			 */
			filteredFiles.stream()
				.filter(e -> !e.isLocalSource())
				.map(e -> e.getFile())
				.forEach(FileUtils::deleteQuietly);

			return temp2.toFile();
		} catch (final IOException ex) {
			throw new FeatureFilesException(ex);
		}
	}
}
