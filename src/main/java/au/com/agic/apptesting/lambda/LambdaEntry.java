package au.com.agic.apptesting.lambda;

import au.com.agic.apptesting.Main;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * An entry point for the app when called from AWS lambda
 */
public class LambdaEntry implements RequestHandler<String, String> {
	@Override
	public String handleRequest(final String input, final Context context) {
		final int failures = Main.run();
		return "Failure count: " + failures;
	}
}
