package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import au.com.agic.apptesting.utils.CountConverter;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of the count conversion service
 */
@Component
public class CountConverterImpl implements CountConverter {

	@Autowired
	private AutoAliasUtils autoAliasUtils;

	@Override
	public Integer convertCountToInteger(final String timesAlias, final String times) {
		return NumberUtils.toInt(
			autoAliasUtils.getValue(
				StringUtils.defaultString(times, "1"),
				StringUtils.isNotBlank(timesAlias),
				State.getFeatureStateForThread()),
			1);
	}
}
