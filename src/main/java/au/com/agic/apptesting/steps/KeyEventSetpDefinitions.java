package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.GetBy;
import au.com.agic.apptesting.utils.JavaScriptRunner;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cucumber.api.java.en.When;

/**
 * Gherkin steps for simulating key presses and other key events.
 *
 * These steps have Atom snipptets that start with the prefix "disptach" and "press".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class KeyEventSetpDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(KeyEventSetpDefinitions.class);
	@Autowired
	private GetBy getBy;
	@Autowired
	private SleepUtils sleepUtils;
	@Autowired
	private SimpleWebElementInteraction simpleWebElementInteraction;
	@Autowired
	private JavaScriptRunner javaScriptRunner;

	/**
	 * Press the CTRL-A keys to the active element. This step is known to have issues
	 * with the Firefox Marionette driver.
	 * @param ignoreErrors Add this text to ignore any exceptions. This is really only useful for debugging.
	 */
	@When("^I press CTRL-A on the active element( ignoring errors)?$")
	public void pressCtrlAStep(final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebElement element = webDriver.switchTo().activeElement();
			element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Press the CMD-A keys to the active element. This step is known to have issues
	 * with the Firefox Marionette driver.
	 * @param ignoreErrors Add this text to ignore any exceptions. This is really only useful for debugging.
	 */
	@When("^I press CMD-A on the active element( ignoring errors)?$")
	public void pressCmdAStep(final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebElement element = webDriver.switchTo().activeElement();
			element.sendKeys(Keys.chord(Keys.COMMAND, "a"));
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Press the CMD-A or CTRL-A keys to the active element depending on the client os.
	 * This step is known to have issues with the Firefox Marionette driver.
	 * @param ignoreErrors Add this text to ignore any exceptions. This is really only useful for debugging.
	 */
	@When("^I select all the text in the active element( ignoring errors)?$")
	public void pressCmdOrCtrlAStep(final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebElement element = webDriver.switchTo().activeElement();

			if (SystemUtils.IS_OS_MAC) {
				element.sendKeys(Keys.chord(Keys.COMMAND, "a"));
			} else {
				element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			}
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Press the delete key on the active element. This step is known to have issues with
	 * the Firefox Marionette driver.
	 * @param times The number of times to press the delete key
	 * @param ignoreErrors Add this text to ignore any exceptions. This is really only useful for debugging.
	 */
	@When("^I press(?: the)? Delete(?: key)? on the active element(?: \"(\\d+)\" times)?( ignoring errors)?$")
	public void pressDeleteStep(final Integer times, final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebElement element = webDriver.switchTo().activeElement();

			for (int i = 0; i < ObjectUtils.defaultIfNull(times, 1); ++i) {
				element.sendKeys(Keys.DELETE);
				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultKeyStrokeDelay());
			}

			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Press the tab key on the active element. This step is known to have issues with
	 * the Firefox Marionette driver.
	 * @param ignoreErrors Add this text to ignore any exceptions. This is really only useful for debugging.
	 */
	@When("^I press(?: the)? tab(?: key)? on the active element( ignoring errors)?$")
	public void pressTabStep(final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebElement element = webDriver.switchTo().activeElement();
			element.sendKeys(Keys.TAB);
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Press the down arrow on the active element. This step is known to have issues with
	 * the Firefox Marionette driver.
	 * @param ignoreErrors Add this text to ignore any exceptions. This is really only useful for debugging.
	 */
	@When("^I press(?: the)? down arrow(?: key)? on the active element( ignoring errors)?$")
	public void pressDownArrowStep(final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebElement element = webDriver.switchTo().activeElement();
			element.sendKeys(Keys.ARROW_DOWN);
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Press the up arrow on the active element. This step is known to have issues with
	 * the Firefox Marionette driver.
	 * @param ignoreErrors Add this text to ignore any exceptions. This is really only useful for debugging.
	 */
	@When("^I press(?: the)? up arrow(?: key)? on the active element( ignoring errors)?$")
	public void pressUpArrowStep(final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebElement element = webDriver.switchTo().activeElement();
			element.sendKeys(Keys.ARROW_UP);
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Press the left arrow on the active element. This step is known to have issues with
	 * the Firefox Marionette driver.
	 * @param ignoreErrors Add this text to ignore any exceptions. This is really only useful for debugging.
	 */
	@When("^I press(?: the)? left arrow(?: key)? on the active element( ignoring errors)?$")
	public void pressLeftArrowStep(final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebElement element = webDriver.switchTo().activeElement();
			element.sendKeys(Keys.ARROW_LEFT);
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Press the right arrow on the active element. This step is known to have issues with
	 * the Firefox Marionette driver.
	 * @param ignoreErrors Add this text to ignore any exceptions. This is really only useful for debugging.
	 */
	@When("^I press(?: the)? right arrow(?: key)? on the active element( ignoring errors)?$")
	public void pressRightArrowStep(final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebElement element = webDriver.switchTo().activeElement();
			element.sendKeys(Keys.ARROW_RIGHT);
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Presses the backspace key on the active element. This step is known to have issues with
	 * the Firefox Marionette driver.
	 * @param times The number of times to press the key
	 * @param ignoreErrors Add this text to ignore any exceptions. This is really only useful for debugging.
	 */
	@When("^I press(?: the)? backspace(?: key)? on the active element(?: \"(\\d+)\" times)?( ignoring errors)?$")
	public void pressBackspaceStep(final Integer times, final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebElement element = webDriver.switchTo().activeElement();
			for (int i = 0; i < ObjectUtils.defaultIfNull(times, 1); ++i) {
				element.sendKeys(Keys.BACK_SPACE);
				sleepUtils.sleep(State.getFeatureStateForThread().getDefaultKeyStrokeDelay());
			}

			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}

	/**
	 * Presses the enter key on the active element. This step is known to have issues with
	 * the Firefox Marionette driver.
	 * @param ignoreErrors Add this text to ignore any exceptions. This is really only useful for debugging.
	 */
	@When("^I press(?: the)? enter(?: key)? on the active element( ignoring errors)?$")
	public void pressEnterStep(final String ignoreErrors) {
		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final WebElement element = webDriver.switchTo().activeElement();
			element.sendKeys(Keys.ENTER);
			sleepUtils.sleep(State.getFeatureStateForThread().getDefaultSleep());
		} catch (final Exception ex) {
			if (StringUtils.isBlank(ignoreErrors)) {
				throw ex;
			}
		}
	}
}
