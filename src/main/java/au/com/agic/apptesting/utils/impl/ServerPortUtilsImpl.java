package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.ServerPortUtils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * An implementation of the server port utils
 */
@Component
public class ServerPortUtilsImpl implements ServerPortUtils {

	@Override
	public int getFreePort()  throws IOException {
		try (ServerSocket server = new ServerSocket(0)) {
			return server.getLocalPort();
		}
	}
}
