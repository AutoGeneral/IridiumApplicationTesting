package au.com.agic.apptesting.utils;

import java.io.IOException;

/**
 * Services related to dealing with ports
 */
public interface ServerPortUtils {

	/**
	 *
	 * @return Get a free port on the server
	 * @throws IOException if an I/O error occurs when opening the socket
	 */
	int getFreePort() throws IOException;
}
