package au.com.agic.apptesting.exception;

/**
 * Represents an exception thrown to indicate an error with the processing of feature files
 */
public class FileProfileAccessException extends RuntimeException {

	private static final long serialVersionUID = 7397804514998558871L;

	public FileProfileAccessException() {
	}

	public FileProfileAccessException(final String message) {
		super(message);
	}

	public FileProfileAccessException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public FileProfileAccessException(final Exception ex) {
		super(ex);
	}
}
