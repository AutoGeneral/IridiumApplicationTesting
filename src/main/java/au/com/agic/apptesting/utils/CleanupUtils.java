package au.com.agic.apptesting.utils;

/**
 * A service used to clean up old report files
 */
public interface CleanupUtils {

	/**
	 * Deletes the cucumber report files
	 */
	void cleanupOldReports();
}
