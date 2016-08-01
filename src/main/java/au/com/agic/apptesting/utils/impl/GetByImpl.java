package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.exception.InvalidInputException;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.ThreadDetails;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import static com.google.common.base.Preconditions.checkState;

/**
 * Implementation of the GetBy service
 */
public class GetByImpl implements GetBy {
	@Override
	public By getBy(
			final String selector,
			final boolean valueAlias,
			final String value,
			final ThreadDetails threadDetails) {

		final String fixedValue = valueAlias ? threadDetails.getDataSet().get(value) : value;

		checkState(StringUtils.isNotBlank(fixedValue), "Selector or alias is blank");

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
			return By.xpath("//*[text()='" + escaped + "')");
		}

		throw new InvalidInputException("Unexpected selector");
	}
}
