package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.FeatureReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import javax.validation.constraints.NotNull;

/**
 * Implementations of feature reader methods
 */
@Component
public class FeatureReaderImpl implements FeatureReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureReaderImpl.class);

	@Override
	public boolean selectFile(@NotNull final File file, @NotNull final String app) {
		checkNotNull(file);
		checkArgument(StringUtils.isNotBlank(app));

		boolean isEnabled = true;
		boolean matchesFeatureGroup = false;

		try {
			final List<String> lines = FileUtils.readLines(file);

			for (final String line : lines) {
				if (Constants.COMMENT_LINE.matcher(line).find()) {
					final Matcher featureGroup = Constants.FEATURE_GROUP_HEADER.matcher(line);
					if (featureGroup.find()) {
						matchesFeatureGroup = matchesFeatureGroup(
							featureGroup.group("value"),
							app);
					}

					final Matcher enabled = Constants.ENABLED_HEADER.matcher(line);
					if (enabled.find()) {
						isEnabled = Boolean.parseBoolean(enabled.group("value"));
					}
				} else {
					/*
						We have found a line that is not a comment, so stop processing the file
					 */
					break;
				}
			}
		} catch (final IOException ex) {
			LOGGER.error("Could not read file.", ex);
		}

		return isEnabled && matchesFeatureGroup;
	}

	@Override
	public boolean matchesFeatureGroup(@NotNull final String featureGroupList, @NotNull final String featureGroup) {
		checkArgument(StringUtils.isNotBlank(featureGroupList));
		checkArgument(StringUtils.isNotBlank(featureGroup));

		final Iterable<String> split = Splitter.on(',')
			.trimResults()
			.omitEmptyStrings()
			.split(featureGroupList.toLowerCase());

		return Iterables.contains(split, featureGroup.toLowerCase());
	}
}
