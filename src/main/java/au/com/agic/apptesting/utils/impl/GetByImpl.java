package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.exception.InvalidInputException;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.GetBy;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import static com.google.common.base.Preconditions.checkState;

/**
 * Implementation of the GetBy service
 */
public class GetByImpl implements GetBy {

	private static final AutoAliasUtils AUTO_ALIAS_UTILS = new AutoAliasUtilsImpl();

	@Override
	public By getBy(
			final String selector,
			final boolean valueAlias,
			final String value,
			final FeatureState featureState) {

		final String fixedValue = AUTO_ALIAS_UTILS.getValue(value, valueAlias, featureState);

		if (ID.equals(selector)) {
			return By.id(fixedValue);
		}

		if (XPATH.equals(selector)) {
			return By.xpath(fixedValue);
		}

		if (CLASS.equals(selector)) {
			return By.cssSelector("." + fixedValue);
		}

		if (NAME.equals(selector)) {
			return By.name(fixedValue);
		}

		if (VALUE.equals(selector)) {
			final String escaped = fixedValue.replaceAll("'", "\'");
			return By.cssSelector("[value='" + escaped + "']");
		}

		if (CSS_SELECTOR.equals(selector)) {
			return By.cssSelector(fixedValue);
		}

		if (TEXT.equals(selector)) {
			final String escaped = fixedValue.replaceAll("'", "''");
			return By.xpath("//*[text()[normalize-space(.)='" + escaped + "']]");
		}

		throw new InvalidInputException("Unexpected selector");
	}
}
