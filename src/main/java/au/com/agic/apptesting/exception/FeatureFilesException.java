package au.com.agic.apptesting.exception;

/**
 * Represents an exception thrown when there was an error with the feature files
 */
public class FeatureFilesException extends RuntimeException {


	private static final long serialVersionUID = 2702981117965959250L;

	public FeatureFilesException() {
	}

	public FeatureFilesException(final String message) {
		super(message);
	}

	public FeatureFilesException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public FeatureFilesException(final Exception ex) {
		super(ex);
	}
}
