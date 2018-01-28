package au.com.agic.apptesting.lambda;

import au.com.agic.apptesting.Main;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

/**
 * An entry point for the app when called from AWS lambda. The function returns immediately,
 * running the test asynchronously. This makes is suitable to be called from API Gateway.
 */
public class LambdaAsyncEntry implements RequestHandler<Map<String, String>, String> {

	@Override
	public String handleRequest(final Map<String, String> input, final Context context) {

		input.entrySet().stream()
			.filter(entry -> LambdaSettings.ACCEPTED_SETTINGS.contains(entry.getKey()))
			.forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		new Thread(() -> Main.run()).start();
		return "";
	}
}
