package au.com.agic.apptesting.exception;

/**
 * Represents an exception thrown when there is an issue with the internal proxy
 */
public class ProxyException extends RuntimeException {

	private static final long serialVersionUID = 6985533315154789872L;

	public ProxyException() {
	}

	public ProxyException(final String message) {
		super(message);
	}

	public ProxyException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public ProxyException(final Exception ex) {
		super(ex);
	}
}
