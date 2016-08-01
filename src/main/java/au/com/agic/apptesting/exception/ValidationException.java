package au.com.agic.apptesting.exception;

/**
 * Represents a validation exception
 */
public class ValidationException extends RuntimeException {


	private static final long serialVersionUID = 8702372076939951881L;

	public ValidationException() {
	}

	public ValidationException(final String message) {
		super(message);
	}

	public ValidationException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public ValidationException(final Exception ex) {
		super(ex);
	}
}
