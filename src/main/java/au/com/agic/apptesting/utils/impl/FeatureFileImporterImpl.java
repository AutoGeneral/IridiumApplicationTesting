package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import au.com.agic.apptesting.utils.FeatureFileImporter;
import au.com.agic.apptesting.utils.StringBuilderUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import javaslang.control.Try;

/**
 * An implementation of the FeatureFileImporter interface
 */
public class FeatureFileImporterImpl implements FeatureFileImporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureFileImporterImpl.class);
	private static final Pattern IMPORT_COMMENT_RE = Pattern.compile("^\\s*#\\s*IMPORT\\s*:\\s*(?<filename>.*?)$");
	private static final StringBuilderUtils STRING_BUILDER_UTILS = new StringBuilderUtilsImpl();

	@Override
	public File processFeatureImportComments(@NotNull final FileDetails file, final String baseUrl) {
		checkNotNull(file);

		try {
			/*
				Assume a null base url means we are looking for files relative
				to the base feature file
			 */
			final String fixedBaseUrl = getFixedBaseUrl(file, baseUrl);

			/*
				The contents of the new, processed file
			 */
			final StringBuilder output = new StringBuilder();
			/*
				Read the original file
			 */
			final String[] fileContents = FileUtils.readFileToString(file.getFile()).split("\n");
			/*
				Loop over each line looking for an import comment
			 */
			for (final String line : fileContents) {
				/*
					Test the line against the import comment regex
				 */
				final Matcher matcher = IMPORT_COMMENT_RE.matcher(line);

				if (matcher.find()) {
					/*
						Import comment found, so replace it with the contents of the file to be
						imported
					 */
					final String filename = matcher.group("filename");
					final String completeFileName = fixedBaseUrl + filename;
					final StringBuilder importFileContents = new StringBuilder();

					Try.of(() -> new File(completeFileName))
						.andThenTry(
							e -> importFileContents.append(FileUtils.readFileToString(e))
						)
						.orElseRun(e -> Try.of(
							() -> importFileContents.append(
								processRemoteUrl(completeFileName)))
						);

					STRING_BUILDER_UTILS.appendWithDelimiter(
						output,
						importFileContents.toString(),
						"\n");
				} else {
					/*
						This is not an import comment, so copy the input line directly to the
						output
					 */
					STRING_BUILDER_UTILS.appendWithDelimiter(output, line, "\n");
				}
			}

			/*
				Save the new file
			 */
			final File newFile = getNewTempFile(file.getFile());
			FileUtils.write(newFile, output.toString(), false);
			return newFile;

		} catch (final IOException ex) {
			LOGGER.error("Could not process file {}", file.getFile().getAbsolutePath(), ex);
		}

		/*
			All else fails, return the file we were passed in
		 */
		return file.getFile();
	}

	private File getNewTempFile(final File file) throws IOException {
		final Path temp2 = Files.createTempDirectory(null);
		return new File(temp2 + "/" + file.getName());
	}

	private String processRemoteUrl(@NotNull final String path) throws IOException {
		final File copy = File.createTempFile("webapptester", ".feature");
		FileUtils.copyURLToFile(new URL(path), copy);
		return FileUtils.readFileToString(copy);
	}

	private String getFixedBaseUrl(@NotNull final FileDetails file, final String baseUrl) {
		checkNotNull(file);

		if (file.isLocalSource() || StringUtils.isBlank(baseUrl)) {
			checkState(file.getFile() != null);
			checkState(file.getFile().getParentFile() != null);
			return file.getFile().getParentFile().getAbsolutePath() + "/";
		}

		return baseUrl;
	}
}
