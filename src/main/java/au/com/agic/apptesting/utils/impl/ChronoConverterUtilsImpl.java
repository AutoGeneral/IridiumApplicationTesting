package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkArgument;

import au.com.agic.apptesting.exception.InvalidInputException;
import au.com.agic.apptesting.utils.ChronoConverterUtils;

import org.apache.commons.lang3.StringUtils;

import java.time.temporal.ChronoUnit;

import javax.validation.constraints.NotNull;

/**
 * An implementation of the chrono converter service
 */
public class ChronoConverterUtilsImpl implements ChronoConverterUtils {

	@Override
	public ChronoUnit fromString(@NotNull final String input) {
		checkArgument(StringUtils.isNotBlank(input));

		if ("Nanos".equalsIgnoreCase(input) || "Nano".equalsIgnoreCase(input)) {
			return ChronoUnit.NANOS;
		}

		if ("Micros".equalsIgnoreCase(input) || "Micro".equalsIgnoreCase(input)) {
			return ChronoUnit.MICROS;
		}

		if ("Millis".equalsIgnoreCase(input) || "Milli".equalsIgnoreCase(input)) {
			return ChronoUnit.MILLIS;
		}

		if ("Seconds".equalsIgnoreCase(input) || "Second".equalsIgnoreCase(input)) {
			return ChronoUnit.SECONDS;
		}

		if ("Minutes".equalsIgnoreCase(input) || "Minute".equalsIgnoreCase(input)) {
			return ChronoUnit.MINUTES;
		}

		if ("Hours".equalsIgnoreCase(input) || "Hour".equalsIgnoreCase(input)) {
			return ChronoUnit.HOURS;
		}

		if ("HalfDays".equalsIgnoreCase(input) || "HalfDay".equalsIgnoreCase(input)) {
			return ChronoUnit.HALF_DAYS;
		}

		if ("Days".equalsIgnoreCase(input) || "Day".equalsIgnoreCase(input)) {
			return ChronoUnit.DAYS;
		}

		if ("Weeks".equalsIgnoreCase(input) || "Week".equalsIgnoreCase(input)) {
			return ChronoUnit.WEEKS;
		}

		if ("Months".equalsIgnoreCase(input) || "Month".equalsIgnoreCase(input)) {
			return ChronoUnit.MONTHS;
		}

		if ("Years".equalsIgnoreCase(input) || "Year".equalsIgnoreCase(input)) {
			return ChronoUnit.YEARS;
		}

		if ("Decades".equalsIgnoreCase(input) || "Decade".equalsIgnoreCase(input)) {
			return ChronoUnit.DECADES;
		}

		if ("Centuries".equalsIgnoreCase(input) || "Century".equalsIgnoreCase(input)) {
			return ChronoUnit.CENTURIES;
		}

		if ("Millennia".equalsIgnoreCase(input)) {
			return ChronoUnit.MILLENNIA;
		}

		if ("Eras".equalsIgnoreCase(input) || "Era".equalsIgnoreCase(input)) {
			return ChronoUnit.ERAS;
		}

		if ("Forever".equalsIgnoreCase(input)) {
			return ChronoUnit.FOREVER;
		}

		throw new InvalidInputException(input + " is not a valid ChronoUnit");
	}


}
