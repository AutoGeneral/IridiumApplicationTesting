package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import au.com.agic.apptesting.utils.SleepUtils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cucumber.api.java.en.When;

/**
 * Gherin steps used to scroll the web page
 *
 * These steps have Atom snipptets that start with the prefix "scroll".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class ScrollStepDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScrollStepDefinitions.class);
	@Autowired
	private SleepUtils sleepUtils;
	@Autowired
	private AutoAliasUtils autoAliasUtils;


	/**
	 * Scrolls to the bottom of the page
	 */
	@When("^I scroll to the bottom of the page$")
	public void scrollToBottom() {
		final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
		final JavascriptExecutor js = JavascriptExecutor.class.cast(webDriver);
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	/**
	 * Scrolls to the bottom of the page
	 */
	@When("^I scroll to the top of the page$")
	public void scrollToTop() {
		final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
		final JavascriptExecutor js = JavascriptExecutor.class.cast(webDriver);
		js.executeScript("window.scrollTo(0, 0)");
	}
}
