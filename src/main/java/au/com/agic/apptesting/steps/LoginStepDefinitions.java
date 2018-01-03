package au.com.agic.apptesting.steps;

import cucumber.api.java.en.When;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

/**
 * Step defininitions for logging into a web page
 */
@Component
public class LoginStepDefinitions {

	/**
	 * Authentication was removed in selenium 3.8.0
	 *
	 * @param username The username
	 * @param password The password
	 * @param exists   If this text is set, an error that would be thrown because the element was not found is
	 *                 ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("I log in with username \"([^\"]*)\" and password \"([^\"]*)\"( if it exists)?$")
	public void login(final String username, final String password, final String exists) {
		throw new NotImplementedException("Authentication was removed in Selenium 3.8.0");
	}
}
