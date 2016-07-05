package au.com.agic.apptesting.utils;

import java.io.IOException;

/**
 * Services related to dealing with ports
 */
public interface ServerPortUtils {

	/**
	 *
	 * @return Get a free port on the server
	 */
	int getFreePort() throws IOException;
}
