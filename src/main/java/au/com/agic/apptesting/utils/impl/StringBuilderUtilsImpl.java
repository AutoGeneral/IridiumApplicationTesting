package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.utils.StringBuilderUtils;

/**
 * Implementation of StringBuilderUtils
 */
public class StringBuilderUtilsImpl implements StringBuilderUtils {

	/**
	 * Appends a value to a string builder with a space if the string builder is not empty
	 *
	 * @param stringBuilder The string builder to append to
	 * @param append        The string to append
	 */
	@Override
	public void appendWithSpace(final StringBuilder stringBuilder, final String append) {
		checkNotNull(stringBuilder);
		checkNotNull(append);

		appendWithDelimiter(stringBuilder, append, " ");
	}

	/**
	 * Appends a value to a string builder with a comma if the string builder is not empty
	 *
	 * @param stringBuilder The string builder to append to
	 * @param append        The string to append
	 */
	@Override
	public void appendWithComma(final StringBuilder stringBuilder, final String append) {
		checkNotNull(stringBuilder);
		checkNotNull(append);

		appendWithDelimiter(stringBuilder, append, ",");
	}

	/**
	 * Appends a value to a string builder with a semicolon if the string builder is not empty
	 *
	 * @param stringBuilder The string builder to append to
	 * @param append        The string to append
	 */
	@Override
	public void appendWithSemicolon(final StringBuilder stringBuilder, final String append) {
		checkNotNull(stringBuilder);
		checkNotNull(append);

		appendWithDelimiter(stringBuilder, append, ";");
	}

	@Override
	public void appendWithDelimiter(
		final StringBuilder stringBuilder,
		final String append,
		final String delimiter) {

		checkNotNull(stringBuilder);
		checkNotNull(append);

		if (stringBuilder.length() != 0) {
			stringBuilder.append(delimiter);
		}
		stringBuilder.append(append);
	}
}
