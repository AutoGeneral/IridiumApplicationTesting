package au.com.agic.apptesting.exception;

/**
 * Represents invalid input from a user script
 */
public class InvalidInputException extends RuntimeException {

	private static final long serialVersionUID = 4179093323563275689L;

	public InvalidInputException() {
	}

	public InvalidInputException(final String message) {
		super(message);
	}

	public InvalidInputException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public InvalidInputException(final Exception ex) {
		super(ex);
	}
}
