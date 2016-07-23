package au.com.agic.apptesting.steps;

import static com.google.common.base.Preconditions.checkState;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SleepUtils;
import au.com.agic.apptesting.utils.ThreadDetails;
import au.com.agic.apptesting.utils.impl.GetByImpl;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;

/**
 * Steps that are specific to A&G web apps
 */
public class AGStepDefinitions {
	private static final GetBy GET_BY = new GetByImpl();
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final ThreadDetails threadDetails =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

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
		final String postcodeValue = StringUtils.isNotBlank(alias)
			? threadDetails.getDataSet().get(postcode) : postcode;

		final By by = GET_BY.getBy("class", false, Constants.POSTCODE_CLASS, threadDetails);
		final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);

		final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
		final JavascriptExecutor js = (JavascriptExecutor) threadDetails.getWebDriver();
		js.executeScript("arguments[0].autoSelectSuburb('" + postcodeValue + "');", element);
		SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
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
			final String attr = " alias".equals(attributeNameAlias)
				? threadDetails.getDataSet().get(attributeName) : attributeName;
			final String value = " alias".equals(attributeValueAlias)
				? threadDetails.getDataSet().get(attributeValue) : attributeValue;

			checkState(attr != null, "the aliased attribute name does not exist");
			checkState(value != null, "the aliased attribute value does not exist");

			LocalDate theDate = LocalDate.now();
			int today = theDate.getDayOfMonth();
			int tomorrow = theDate.getDayOfMonth() + 1;
			int dateValue = "today".equals(value) ? today : tomorrow;

			final WebDriverWait wait = new WebDriverWait(threadDetails.getWebDriver(), Constants.WAIT);
			final WebElement element = wait.until(
				ExpectedConditions.elementToBeClickable(
					By.cssSelector("[" + attr + "='" + dateValue + "']")));
			element.click();
			SLEEP_UTILS.sleep(threadDetails.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}
}
