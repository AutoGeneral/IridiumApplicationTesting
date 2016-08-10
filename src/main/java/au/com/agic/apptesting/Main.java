package au.com.agic.apptesting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private Main() {
	}

	public static void main(final String... args) {
		try {
			/*
				This is required to run ZAP from webstart
			 */
			System.setSecurityManager(null);

			/*
				Execute the tests
			 */
			final int failures = new TestRunner().run();
			System.exit(failures);
		} catch (final Exception ex) {
			LOGGER.error(
				"An exception was raised while attempting to run the Cucumber test scripts", ex);
			System.exit(-1);
		}
	}
}
