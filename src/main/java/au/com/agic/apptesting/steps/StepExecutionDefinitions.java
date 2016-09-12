package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.FeatureState;

import org.springframework.stereotype.Component;

import cucumber.api.java.en.When;

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
		final FeatureState featureState =
			State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

		featureState.setSkipSteps(true);
	}
}
