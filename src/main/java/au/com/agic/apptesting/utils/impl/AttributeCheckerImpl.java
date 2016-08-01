package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import au.com.agic.apptesting.profiles.configuration.FeatureGroup;
import au.com.agic.apptesting.utils.AttributeChecker;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * implementation of the attribute checker interface
 */
public class AttributeCheckerImpl implements AttributeChecker {

	@Override
	public boolean isSingleSettingEnabled(@NotNull final FeatureGroup featureGroup) {
		checkNotNull(featureGroup);

		return Boolean.parseBoolean(featureGroup.getEnabled());
	}

	@Override
	public boolean isSingleSettingForApp(@NotNull final FeatureGroup featureGroup, @NotNull final String app) {
		checkNotNull(featureGroup);
		checkNotNull(app);

		return app.equalsIgnoreCase(featureGroup.getFeatureGroup());
	}

	@Override
	public boolean isSingleSettingInGroup(@NotNull final FeatureGroup featureGroup, final String group) {
		checkNotNull(featureGroup);

		/*
			No group means all match
		 */
		if (StringUtils.isBlank(group)) {
			return true;
		}

		if (featureGroup.getGroup() == null) {
			return true;
		}

		final Iterable<String> split = Splitter.on(',')
			.trimResults()
			.omitEmptyStrings()
			.split(featureGroup.getGroup().toLowerCase());

		return Iterables.contains(split, group.toLowerCase());
	}
}
