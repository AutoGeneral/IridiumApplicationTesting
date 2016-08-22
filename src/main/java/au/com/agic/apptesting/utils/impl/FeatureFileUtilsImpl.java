package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.exception.FileProfileAccessException;
import au.com.agic.apptesting.exception.RemoteFeatureException;
import au.com.agic.apptesting.utils.FeatureFileUtils;
import au.com.agic.apptesting.utils.FeatureReader;
import javaslang.control.Try;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An implementation of the feature files utils service
 */
public class FeatureFileUtilsImpl implements FeatureFileUtils {

	private static final String FEATURE_EXT = ".feature";
	private static final FeatureReader FEATURE_READER = new FeatureReaderImpl();

	@Override
	public List<FileDetails> getFeatureScripts(
		@NotNull final String path,
		final String featureGroup,
		final String baseUrl) {
		checkArgument(StringUtils.isNotBlank(path));

		/*
			Assume a null base url is just an empty string
		 */
		final String fixedBaseUrl = StringUtils.defaultIfBlank(baseUrl, "");
		final String fixedPath = fixedBaseUrl + path;

		/*
			We need to be wary of different OSs throwing exceptions working with
			urls in these file methods.
		 */
		return Try.of(() -> {
			if (Files.isDirectory(Paths.get(fixedPath))) {
				/*
					We know this is a local directory, so process the files.
					We only return files that match the feature group header
				 */
				return processLocalFiles(fixedPath).stream()
					.filter(e -> FEATURE_READER.selectFile(e, featureGroup))
					.map(e -> new FileDetails(e, true))
					.collect(Collectors.toList());

			}

			if (Files.isRegularFile(Paths.get(fixedPath))) {
				/*
					We know this is a single file, so just return it. Note that we
					ignore the supplied feature group when we are looking at
					a single file
				 */
				return Arrays.asList(new FileDetails(new File(path), true));
			}

			throw new Exception();
		})
		/*
			Either there was an exception because the url was not accepted in the
			file operations, or we correctly determine that the url is not a
			file or directory
		 */
		.orElse(Try.of(() -> processRemoteUrl(fixedPath).stream()
			.map(e -> new FileDetails(e, false))
			.collect(Collectors.toList()))
		)
		/*
			This wasn't a url or a file, so we just have to throw an exception
		 */
		.getOrElseThrow(
			ex -> new FileProfileAccessException("Error attempting to open \"" + path
				+ "\". Make sure the supplied path or URL was correct.", ex)
		);
	}

	private List<File> processRemoteUrl(@NotNull final String path) throws IOException {
		try {
			final File copy = File.createTempFile("webapptester", ".feature");
			FileUtils.copyURLToFile(new URL(path), copy);
			return Arrays.asList(copy);
		} catch (final FileNotFoundException ex) {
			throw new RemoteFeatureException("The remote file could not be downloaded."
				+ " Either the URL was invalid, or the path was actually supposed to reference a"
				+ " local file but that file could not be found an so was assumed to be a URL.",  ex);
		}
	}

	private List<File> processLocalFiles(@NotNull final String path) {
		final List<File> features = new ArrayList<>();
		loopOverFiles(Paths.get(path).toFile(), features, new ArrayList<>());
		return features;
	}

	private void loopOverFiles(
		@NotNull final File directory,
		@NotNull final List<File> features,
		@NotNull final List<String> processed) {
		checkNotNull(directory);
		checkNotNull(features);

		if (!processed.contains(directory.toString())) {
			processed.add(directory.toString());
			if (directory.isDirectory()) {
				final File[] files = directory.listFiles();
				for (final File file : files) {
					if (file.isDirectory()) {
						loopOverFiles(file, features, processed);
					} else {
						if (file.getName().endsWith(FEATURE_EXT)) {
							features.add(file);
						}
					}
				}
			}
		}
	}
}
