package au.com.agic.apptesting.lambda;

import au.com.agic.apptesting.Main;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

/**
 * An entry point for the app when called from AWS lambda
 */
public class LambdaEntry implements RequestHandler<Map<String, String>, Integer> {

	@Override
	public Integer handleRequest(final Map<String, String> input, final Context context) {

		input.entrySet().stream()
			.filter(entry -> LambdaSettings.ACCEPTED_SETTINGS.contains(entry.getKey()))
			.forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		final int failures = Main.run();
		return failures;
	}
}
