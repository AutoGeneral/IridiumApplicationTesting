package au.com.agic.apptesting.exception;

/**
 * Represents an error download the jar file and saving it locally
 */
public class JarDownloadException extends RuntimeException {

	private static final long serialVersionUID = -4801120335272955576L;

	public JarDownloadException() {
	}

	public JarDownloadException(final String message) {
		super(message);
	}

	public JarDownloadException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public JarDownloadException(final Exception ex) {
		super(ex);
	}
}
