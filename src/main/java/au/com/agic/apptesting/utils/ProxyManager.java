package au.com.agic.apptesting.utils;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * A service that is used to create and configure the proxies reqyired to run a test
 */
public interface ProxyManager {

	/**
	 * Create and configure all the proxies that are required ti run the test. This can include
	 * security proxies like ZAP, request modifying proxies like BrowserMob, and configuration
	 * of external proxies.
	 * @param globalTempFiles Tempoarry files to be cleaned up once Iridium is closed
	 * @param tempFiles Temporary files to be cleaned up after test is completed
	 * @return A list of proxies created
	 */
	List<ProxyDetails<?>> configureProxies(
		@NotNull final List<File> globalTempFiles,
		@NotNull final List<File> tempFiles);

	/**
	 * Gracefully shutdown proxies before we exit the app
	 * @param proxies The list of proxies that were created for this test
	 */
	void stopProxies(@NotNull final List<ProxyDetails<?>> proxies);
}
