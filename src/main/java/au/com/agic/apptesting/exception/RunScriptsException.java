package au.com.agic.apptesting.exception;

/**
 * Represents an exception thrown when there was an error running the scripts
 */
public class RunScriptsException extends RuntimeException {

	private static final long serialVersionUID = 4034448566539240049L;

	public RunScriptsException() {
	}

	public RunScriptsException(final String message) {
		super(message);
	}

	public RunScriptsException(final String message, final Throwable ex) {
		super(message, ex);
	}

	public RunScriptsException(final Exception ex) {
		super(ex);
	}
}
