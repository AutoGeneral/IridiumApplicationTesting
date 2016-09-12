package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import au.com.agic.apptesting.utils.AutoAliasUtils;
import au.com.agic.apptesting.utils.FeatureState;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * Implementation of the AutoAliasUtils service
 */
@Component
public class AutoAliasUtilsImpl implements AutoAliasUtils {

	@Override
	public String getValue(
			@NotNull final String value,
			final boolean forceAlias,
			@NotNull final FeatureState featureState) {

		checkArgument(StringUtils.isNotBlank(value));
		checkNotNull(featureState);

		if (featureState.getAutoAlias() || forceAlias) {
			final Optional<String> aliasedValue = featureState.getDataSet().entrySet().stream()
				.filter(x -> value.equals(x.getKey()))
				.map(Map.Entry::getValue)
				.filter(StringUtils::isNotBlank)
				.findFirst();

			if (forceAlias) {
				checkState(aliasedValue.isPresent(), "Alias is blank");
			}

			return aliasedValue.orElse(value);
		}

		return value;
	}
}
