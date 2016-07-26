package au.com.agic.apptesting.exception;

/**
 * Represents an exception that is thrown when we check for invalid http responses passed
 * through the browsermob proxy
 */
public class HttpResponseException extends RuntimeException {

	private static final long serialVersionUID = 1103215915022195552L;

	public HttpResponseException() {
	}

	public HttpResponseException(final String message) {
		super(message);
	}

	public HttpResponseException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public HttpResponseException(final Exception ex) {
		super(ex);
	}
}
