package au.com.agic.apptesting.exception;

/**
 * Represents an invalid request to configure a ZAP policy
 */
public class ZAPPolicyException extends RuntimeException {


	private static final long serialVersionUID = 1954923278770088042L;

	public ZAPPolicyException() {
	}

	public ZAPPolicyException(final String message) {
		super(message);
	}

	public ZAPPolicyException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public ZAPPolicyException(final Exception ex) {
		super(ex);
	}
}
