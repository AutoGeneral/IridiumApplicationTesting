package au.com.agic.apptesting.exception;

/**
 * Represents an error extracting the webdriver driver executables, or returning
 * a web driver for use with the feature.
 */
public class DriverException extends RuntimeException {

	private static final long serialVersionUID = -4801120335272955576L;

	public DriverException() {
	}

	public DriverException(final String message) {
		super(message);
	}

	public DriverException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public DriverException(final Exception ex) {
		super(ex);
	}
}
