package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.ThreadDetails;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.security.UserAndPassword;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java.en.When;

/**
 * Step defininitions for logging into a web page
 */
public class LoginStepDefinitions {

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread(
			Thread.currentThread().getName());

	/**
	 * This code is supposed to populate the login dialog, but it actually doesn't work with most modern
	 * browsers.
	 *
	 * @param username The username
	 * @param password The password
	 * @param exists   If this text is set, an error that would be thrown because the element was not found is
	 *                 ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("I log in with username \"([^\"]*)\" and password \"([^\"]*)\"( if it exists)?$")
	public void login(final String username, final String password, final String exists) {
		try {
			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final Alert alert = wait.until(ExpectedConditions.alertIsPresent());
			alert.authenticateUsing(new UserAndPassword(username, password));
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}
}
