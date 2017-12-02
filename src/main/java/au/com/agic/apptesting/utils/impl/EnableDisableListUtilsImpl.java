package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.EnableDisableListUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * An implementation of the EnableDisableListUtils service
 */
public class EnableDisableListUtilsImpl implements EnableDisableListUtils {

	@Override
	public boolean enabled(@NotNull final String list, @NotNull final String option) {
		checkNotNull(list);
		checkArgument(StringUtils.isNotBlank(option));

		return enabled(list, option, false);
	}

	@Override
	public boolean enabled(@NotNull final String list, @NotNull final String option, final boolean defaultValue) {
		checkNotNull(list);
		checkArgument(StringUtils.isNotBlank(option));

		return enabled(list, option, false, defaultValue);
	}

	@Override
	public boolean enabled(@NotNull final String list, @NotNull final String option, final boolean ignoreCase, final boolean defaultValue) {
		checkNotNull(list);
		checkArgument(StringUtils.isNotBlank(option));

		final String fixedOption = ignoreCase ? option.toLowerCase() : option;
		final String fixedList = ignoreCase ? list.toLowerCase() : list;

		final List<String> options = Lists.newArrayList(Splitter.on(',')
			.trimResults()
			.omitEmptyStrings()
			.split(fixedList));

		if (options.contains(fixedOption) || options.contains("+" + fixedOption)) {
			return true;
		}

		if (options.contains("-" + fixedOption)) {
			return false;
		}

		return defaultValue;
	}
}
