package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import cucumber.api.java.en.When;
import org.springframework.stereotype.Component;

/**
 * Steps that change the execution of the scenarios and steps
 */
@Component
public class StepExecutionDefinitions {
	/**
	 * Instruct Iridum to skip all remaining steps
	 */
	@When("I skip all remaining steps")
	public void skip() {
		State.getFeatureStateForThread().setSkipSteps(true);
	}
}
