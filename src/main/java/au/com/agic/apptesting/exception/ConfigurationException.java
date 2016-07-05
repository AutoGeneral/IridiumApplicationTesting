package au.com.agic.apptesting.exception;

/**
 * Represents a state where the configuration files are incomplete or invalid
 */
public class ConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 1103215915022195552L;

	public ConfigurationException() {
	}

	public ConfigurationException(final String message) {
		super(message);
	}

	public ConfigurationException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public ConfigurationException(final Exception ex) {
		super(ex);
	}
}
