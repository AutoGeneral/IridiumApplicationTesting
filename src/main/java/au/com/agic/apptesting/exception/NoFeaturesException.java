package au.com.agic.apptesting.exception;

/**
 * Represents an exception thrown when there are no scripts that match the options
 */
public class NoFeaturesException extends RuntimeException {

	private static final long serialVersionUID = -2016925488610144462L;

	public NoFeaturesException() {
	}

	public NoFeaturesException(final String message) {
		super(message);
	}

	public NoFeaturesException(final Exception ex) {
		super(ex);
	}
}
