package au.com.agic.apptesting;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import au.com.agic.apptesting.utils.ThreadWebDriverMap;
import au.com.agic.apptesting.utils.impl.LocalThreadWebDriverMapImpl;
import au.com.agic.apptesting.utils.impl.RemoteThreadWebDriverMapImpl;
import au.com.agic.apptesting.utils.impl.SystemPropertyUtilsImpl;

/**
 * Maintains the state of the system. Threads will call into this state to get their connection
 * information.
 */
public final class State {

	public static final ThreadWebDriverMap THREAD_DESIRED_CAPABILITY_MAP;
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	static {
		/*
			Select the location of the tests based on the system property
		 */
		THREAD_DESIRED_CAPABILITY_MAP = Constants.REMOTE_TESTS.equalsIgnoreCase(
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.TEST_DESTINATION_SYSTEM_PROPERTY))
			? new RemoteThreadWebDriverMapImpl()
			: new LocalThreadWebDriverMapImpl();
	}

	private State() {
	}
}
