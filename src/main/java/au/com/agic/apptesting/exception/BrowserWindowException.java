package au.com.agic.apptesting.exception;

/**
 * Represents an error working with a browser window or tab
 */
public class BrowserWindowException extends RuntimeException {

	public BrowserWindowException() {
	}

	public BrowserWindowException(final String message) {
		super(message);
	}

	public BrowserWindowException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public BrowserWindowException(final Exception ex) {
		super(ex);
	}
}
