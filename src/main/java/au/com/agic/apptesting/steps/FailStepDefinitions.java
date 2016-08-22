package au.com.agic.apptesting.steps;

import cucumber.api.java.en.When;

/**
 * Steps that cause a scenario to fail. Useful for testing.
 */
public class FailStepDefinitions {
	/**
	 * A step that will always fail
	 */
	@When("I fail the scenario")
	public void fail() {
		throw new RuntimeException("Failed this step on purpose");
	}
}
