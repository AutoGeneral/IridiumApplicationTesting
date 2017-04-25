package au.com.agic.apptesting.utils;

/**
 * Some handy utils for building up StringBuilders
 */
public interface StringBuilderUtils {

	void appendWithSpace(StringBuilder stringBuilder, String append);

	void appendWithComma(StringBuilder stringBuilder, String append);

	void appendWithSemicolon(StringBuilder stringBuilder, String append);

	/**
	 *
	 * @param stringBuilder The builder holding the contents
	 * @param append The text to append
	 * @param delimiter The delimiter between the appended text any any existing text
	 */
	void appendWithDelimiter(StringBuilder stringBuilder, String append, String delimiter);
}
