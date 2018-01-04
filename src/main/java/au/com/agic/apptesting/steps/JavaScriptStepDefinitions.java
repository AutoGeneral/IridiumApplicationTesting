package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Gherkin steps used to run javascript.
 *
 * These steps have Atom snippets that start with the prefix "run".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class JavaScriptStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(JavaScriptStepDefinitions.class);

	/**
	 * Runs arbitrary JavaScript
	 *
	 * @param javaScript         The JavaScript to run
	 */
	@When("^I run the following JavaScript( ignoring errors)?$")
	public void runJavaScript(final String ignoreErrors, final String javaScript) throws Exception {
		try {
			final WebDriver webDriver = State.threadDesiredCapabilityMap.getWebDriverForThread();
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;
			final List<String> aliases = State.getFeatureStateForThread().getDataSet().entrySet().stream()
				.flatMap(it -> Stream.of(it.getKey(), it.getValue()))
				.collect(Collectors.toList());

			js.executeScript(javaScript, aliases);
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Runs arbitrary JavaScript
	 *
	 * @param javaScript         The JavaScript to run
	 * @param alias 			The alias that holds the string result of the script
	 */
	@When("^I run the following JavaScript and save the result to alias \"(.*?)\"( ignoring errors)?$")
	public void runJavaScript(final String alias, final String ignoreErrors, final String javaScript) throws Exception {
		try {
			final WebDriver webDriver = State.threadDesiredCapabilityMap.getWebDriverForThread();
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			final List<String> aliases = State.getFeatureStateForThread().getDataSet().entrySet().stream()
				.flatMap(it -> Stream.of(it.getKey(), it.getValue()))
				.collect(Collectors.toList());

			final Object result = js.executeScript(javaScript, aliases);

			final Map<String, String> dataSet = State.getFeatureStateForThread().getDataSet();
			dataSet.put(alias, result != null ? result.toString() : "");
			State.getFeatureStateForThread().setDataSet(dataSet);
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}
}
