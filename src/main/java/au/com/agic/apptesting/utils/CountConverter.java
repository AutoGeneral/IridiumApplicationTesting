package au.com.agic.apptesting.utils;

/**
 * A util service for converting counts to integers
 */
public interface CountConverter {

	/**
	 *
	 * @param timesAlias The string that represents the 'alias' phrase in a step
	 * @param times The times to convert to an int, or the name of an alias
	 * @return
	 */
	Integer convertCountToInteger(String timesAlias, String times);
}
