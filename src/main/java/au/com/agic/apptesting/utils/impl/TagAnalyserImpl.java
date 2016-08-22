package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.TagAnalyser;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the tag analyser
 */
public class TagAnalyserImpl implements TagAnalyser {

	@Override
	public List<String> convertTagsToList(final String tags) {
		if (StringUtils.isBlank(tags)) {
			return new ArrayList<>();
		}

		/*
			@tag1,@tag2;@tag3

			means (@tag1 or @tag2) and @tag3

			which maps to (https://github.com/cucumber/cucumber/wiki/Tags)

			--tags @tag1,@tag2 --tags @tag3
		 */

		return Lists.newArrayList(Splitter.on(';')
			.trimResults()
			.omitEmptyStrings()
			.split(tags));

	}
}
