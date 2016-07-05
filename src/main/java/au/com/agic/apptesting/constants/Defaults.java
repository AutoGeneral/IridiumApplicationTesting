package au.com.agic.apptesting.constants;

/**
 * Provides default values for the UI, to be used on the first run
 */
public final class Defaults {
	/**
	 * The default name of a url mapped to a feature group
	 */
	public static final String DEFAULT_URL_NAME = "default";
	/**
	 * By default BrowserStack can only run two parallel tests
	 */
	public static final int MAX_BROWSERSTACK_THREADS = 2;

	private Defaults() {
	}
}
