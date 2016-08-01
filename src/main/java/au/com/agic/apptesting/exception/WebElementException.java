package au.com.agic.apptesting.exception;

/**
 * Represents an error finding an element on the page
 */
public class WebElementException extends RuntimeException {

	public WebElementException() {
	}

	public WebElementException(final String message) {
		super(message);
	}

	public WebElementException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public WebElementException(final Exception ex) {
		super(ex);
	}
}
