package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.CrossPlatformUtils;

import javax.validation.constraints.NotNull;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * implementation of CrossPlatformUtils
 */
public class CrossPlatformUtilsImpl implements CrossPlatformUtils {

	private static final Pattern WINDOWS_LINE_ENDINGS = Pattern.compile("\r\n");
	private static final String UNIX_LINE_ENDINGS = "\n";

	@Override
	public String normaliseLineBreaks(@NotNull final String input) {
		checkNotNull(input);
		return WINDOWS_LINE_ENDINGS.matcher(input).replaceAll(UNIX_LINE_ENDINGS);
	}
}
