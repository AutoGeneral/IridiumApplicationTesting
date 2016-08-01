package au.com.agic.apptesting.exception;

/**
 * Represents an exception thrown when trying to download a remote feature file
 */
public class RemoteFeatureException extends RuntimeException {


	private static final long serialVersionUID = 4672729068927387793L;

	public RemoteFeatureException() {
	}

	public RemoteFeatureException(final String message) {
		super(message);
	}

	public RemoteFeatureException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public RemoteFeatureException(final Exception ex) {
		super(ex);
	}
}
