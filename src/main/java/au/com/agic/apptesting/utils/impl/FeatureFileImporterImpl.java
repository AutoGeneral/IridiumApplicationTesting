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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import javaslang.control.Try;

/**
 * An implementation of the FeatureFileImporter interface
 */

public class FeatureFileImporterImpl implements FeatureFileImporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureFileImporterImpl.class);
	private static final Pattern IMPORT_COMMENT_RE = Pattern.compile("^\\s*#\\s*IMPORT\\s*:\\s*(?<filename>.*?)$");
	/**
	 * This is a regex to match Feature lines
	 */
	private static final Pattern FEATURE_STANZA_RE =
		Pattern.compile("^\\s*Feature\\s*:.*?$", Pattern.CASE_INSENSITIVE);
	/**
	 * This is a regex to match Scenario, Background, Scenario Outline etc lines
	 */
	private static final Pattern START_STANZA_RE =
		Pattern.compile("^\\s*(Scenario|Background|Scenario Outline)\\s*:.*?$", Pattern.CASE_INSENSITIVE);
	/**
	 * This is a regex to match a tag
	 */
	private static final Pattern TAG_ANNOTATION_RE = Pattern.compile("^\\s*@.*?$");
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

					final File thisFile = new File(completeFileName);

					Try.of(() -> FileUtils.readFileToString(new File(completeFileName)))
						.orElse(Try.of(() -> processRemoteUrl(completeFileName)))
						.map(this::clearContentToFirstScenario)
						.peek(s -> STRING_BUILDER_UTILS.appendWithDelimiter(
							output, s, "\n"));
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

	/**
	 * https://github.com/AutoGeneral/IridiumApplicationTesting/issues/66
	 * @param contents The raw contents
	 * @return The contents of the supplied string from the first Scenario to the end of the file
	 */
	public String clearContentToFirstScenario(@NotNull final String contents) {
		checkNotNull(contents);
		/*
			http://stackoverflow.com/questions/25569836/equivalent-of-scala-dropwhile
			Make up for the last of a dropWhile
		 */
		class MutableBoolean {

			boolean foundFeature;
			boolean foundScenarioOrTag;
		}

		final MutableBoolean inTail = new MutableBoolean();

		return Stream.of(contents.split("\n"))
			.filter(i -> {
				inTail.foundFeature = inTail.foundFeature || FEATURE_STANZA_RE.matcher(i).matches();
				inTail.foundScenarioOrTag = inTail.foundFeature
					&& (inTail.foundScenarioOrTag
					|| TAG_ANNOTATION_RE.matcher(i).matches()
					|| START_STANZA_RE.matcher(i).matches());

				return inTail.foundFeature && inTail.foundScenarioOrTag;
			})
			.collect(Collectors.joining("\n"));
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
