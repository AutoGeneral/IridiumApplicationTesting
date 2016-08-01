package au.com.agic.apptesting.utils;

/**
 * Some handy utils for building up StringBuilders
 */
public interface StringBuilderUtils {

	void appendWithSpace(final StringBuilder stringBuilder, final String append);

	void appendWithComma(final StringBuilder stringBuilder, final String append);

	void appendWithSemicolon(final StringBuilder stringBuilder, final String append);

	void appendWithDelimiter(final StringBuilder stringBuilder, final String append, final String delimiter);
}
