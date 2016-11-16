package au.com.agic.apptesting.utils;

/**
 * Some handy utils for building up StringBuilders
 */
public interface StringBuilderUtils {

	void appendWithSpace(StringBuilder stringBuilder, String append);

	void appendWithComma(StringBuilder stringBuilder, String append);

	void appendWithSemicolon(StringBuilder stringBuilder, String append);

	void appendWithDelimiter(StringBuilder stringBuilder, String append, String delimiter);
}
