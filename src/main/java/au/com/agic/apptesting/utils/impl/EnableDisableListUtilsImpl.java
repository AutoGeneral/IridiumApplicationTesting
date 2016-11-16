package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import au.com.agic.apptesting.utils.EnableDisableListUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import javax.validation.constraints.NotNull;

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

		final List<String> options = Lists.newArrayList(Splitter.on(',')
			.trimResults()
			.omitEmptyStrings()
			.split(list));

		if (options.contains(option) || options.contains("+" + option)) {
			return true;
		}

		if (options.contains("-" + option)) {
			return false;
		}

		return defaultValue;
	}
}
