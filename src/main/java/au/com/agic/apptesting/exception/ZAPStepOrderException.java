package au.com.agic.apptesting.exception;

/**
 * Represents the error when ZAP steps are called out of order
 */
public class ZAPStepOrderException extends RuntimeException {


	private static final long serialVersionUID = 1954923278770088042L;

	public ZAPStepOrderException() {
	}

	public ZAPStepOrderException(final String message) {
		super(message);
	}

	public ZAPStepOrderException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public ZAPStepOrderException(final Exception ex) {
		super(ex);
	}
}
