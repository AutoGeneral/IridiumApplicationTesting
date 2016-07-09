package au.com.agic.apptesting.utils;

/**
 * Services for pausing the current thread
 */
public interface SleepUtils {
	/**
	 *
	 * @param sleep how long in milliseconds to pause the thread for
     */
	void sleep(final long sleep);
}
