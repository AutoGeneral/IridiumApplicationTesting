package au.com.agic.apptesting.steps;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.SleepUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cucumber.api.java.en.When;

/**
 * This class contains the Gherkin step definitions for working with frames/iframes.
 */
@Component
public class FrameStepDefinition {

	@Autowired
	private SleepUtils sleepUtils;

	@Autowired
	private GetBy getBy;

	/**
	 * Switches to the frame/iframe with given selector or alias.
	 *
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias
	 *                      was set, this value is found from the data set. Otherwise it is a literal value.
	 */
	@When("^I switch to (?:a|an|the) (?:frame|iframe) with (?:a|an|the) (ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\"")
	public void switchToFrame(
		final String selector,
		final String alias,
		final String selectorValue) {

		final By by = getBy.getBy(selector, isNotBlank(alias), selectorValue, State.getFeatureStateForThread());

		switchToFrameBy(by);
	}

	/**
	 * Functionality depends on 2 scenarios:
	 * - The control is within a frame set.
	 * - The control is within an iframe.
	 *
	 * In first case, defaultContent() will hand over the control to the first frame in the frame set.
	 * In second case, defaultContent() will hand over the control to the main document in the page.
	 */
	@When("I switch to the default content")
	public void switchToDefaultContent() {
		final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
		webDriver.switchTo().defaultContent();
		sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
	}


	private void switchToFrameBy(final By by) {
		final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
		final WebElement webElement = webDriver.findElement(by);
		webDriver.switchTo().frame(webElement);
		sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
	}
}
