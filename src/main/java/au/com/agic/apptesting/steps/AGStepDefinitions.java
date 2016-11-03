package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SleepUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import cucumber.api.java.en.When;

/**
 * Steps that are specific to Auto and General web apps
 */
@Component
public class AGStepDefinitions {
	@Autowired
	private GetBy GET_BY;
	@Autowired
	private SleepUtils SLEEP_UTILS;
	@Autowired
	private AutoAliasUtils AUTO_ALIAS_UTILS;

	/**
	 * The postcode suggestion box is a pain in the ass. It won't be triggered without a keyup event,
	 * and browsers like Firefox and Safari don't seem to trigger this event if the browser is no focused.
	 * It also fails to trigger in Browserstack. <p> So, to work around this, we use a utility method from
	 * the branding project in jquery.address.js to set the value of the post code.
	 *
	 * @param postcode The postcode to enter into the suburb text box
	 * @param alias Include this text if the value to be added to the postcode box is an alias
	 */
	@When("I autoselect the post code of( alias)? \"([^\"]*)\"")
	public void autoselectPostcode(final String alias, final String postcode) {
		final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
		final String postcodeValue = AUTO_ALIAS_UTILS.getValue(
			postcode, StringUtils.isNotBlank(alias), State.getFeatureStateForThread());

		final By by = GET_BY.getBy("class", false, Constants.POSTCODE_CLASS, State.getFeatureStateForThread());
		final WebDriverWait wait = new WebDriverWait(
			webDriver,
			State.getFeatureStateForThread().getDefaultWait(),
			Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);

		final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
		final JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("arguments[0].autoSelectSuburb('" + postcodeValue + "');", element);
		SLEEP_UTILS.sleep(State.getFeatureStateForThread().getDefaultSleep());
	}

	/**
	 * Clicks an element on the page of the datepicker.
	 *
	 * @param attributeNameAlias  If this word is found in the step, it means the attributeName is found from
	 *                            the data set.
	 * @param attributeName       The name of the attribute to match.
	 * @param attributeValueAlias If this word is found in the step, it means the attributeValue is found from
	 *                            the data set.
	 * @param attributeValue      The value of the attribute to match - Currently supported values are today
	 *                            and tomorrow only.
	 * @param exists              If this text is set, an error that would be thrown because the element was
	 *                            not found is ignored. Essentially setting this text makes this an optional
	 *                            statement.
	 */
	@When("^I click (?:a|the) datepicker with (?:a|an|the) attribute( alias)? of \"([^\"]*)\" equal to( alias)? "
		+ "\"([^\"]*)\"( if it exists)?$")
	public void clickElementWithDatepicker(
		final String attributeNameAlias,
		final String attributeName,
		final String attributeValueAlias,
		final String attributeValue,
		final String exists) {

		try {
			final String attr = AUTO_ALIAS_UTILS.getValue(
				attributeName, StringUtils.isNotBlank(attributeNameAlias), State.getFeatureStateForThread());

			final String value = AUTO_ALIAS_UTILS.getValue(
				attributeValue, StringUtils.isNotBlank(attributeValueAlias), State.getFeatureStateForThread());

			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();

			LocalDate theDate = LocalDate.now();
			int today = theDate.getDayOfMonth();
			int tomorrow = theDate.getDayOfMonth() + 1;
			int dateValue = "today".equals(value) ? today : tomorrow;

			final WebDriverWait wait = new WebDriverWait(
				webDriver,
				State.getFeatureStateForThread().getDefaultWait(),
				Constants.ELEMENT_WAIT_SLEEP_TIMEOUT);

			final WebElement element = wait.until(
				ExpectedConditions.elementToBeClickable(
					By.cssSelector("[" + attr + "='" + dateValue + "']")));
			element.click();
			SLEEP_UTILS.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}
}
