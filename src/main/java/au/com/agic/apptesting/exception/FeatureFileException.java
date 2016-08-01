package au.com.agic.apptesting.exception;

/**
 * Represents an exception thrown when there are no scripts that match the options
 */
public class FeatureFileException extends RuntimeException {

	private static final long serialVersionUID = 8167572990952388226L;

	public FeatureFileException() {
	}

	public FeatureFileException(final String message) {
		super(message);
	}

	public FeatureFileException(final Exception ex) {
		super(ex);
	}
}
