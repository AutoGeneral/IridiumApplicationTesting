package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.exception.WebElementException;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import au.com.agic.apptesting.utils.SimpleWebElementInteraction;
import au.com.agic.apptesting.utils.SleepUtils;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Gherin steps used to modify the attributes on elements.
 *
 * These steps have Atom snipptets that start with the prefix "modify".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
@Component
public class ModifyAttributeDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModifyAttributeDefinitions.class);
	@Autowired
	private SleepUtils sleepUtils;
	@Autowired
	private AutoAliasUtils autoAliasUtils;
	@Autowired
	private SimpleWebElementInteraction simpleWebElementInteraction;

	/**
	 * Opens up the supplied URL.
	 *
	 * @param alias          include this text if the url is actually an alias to be loaded from the
	 *                       configuration file
	 * @param selectorValue  The text used to select the element
	 * @param attributeAlias true if the attribute name is forced to an alias
	 * @param attribute      The name of the attribute
	 * @param valueAlias     true if the attribute value is an alias
	 * @param value          The value to assign to the attribute
	 * @param exists         Set this string to silently fail if the element can not be found
	 */
	@When("^I modify the(?: element found by)?( alias)? \"(.*?)\"(?: \\w+)*? by setting the attribute"
		+ "( alias)? \"(.*?)\" to( alias)? \"(.*?)\"( if it exists)?$")
	public void modifyAttribute(
		final String alias,
		final String selectorValue,
		final String attributeAlias,
		final String attribute,
		final String valueAlias,
		final String value,
		final String exists) {

		try {
			final WebDriver webDriver = State.getThreadDesiredCapabilityMap().getWebDriverForThread();
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			final WebElement element = simpleWebElementInteraction.getPresenceElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				State.getFeatureStateForThread());

			final String aliasName = autoAliasUtils.getValue(
				attribute,
				StringUtils.isNoneBlank(attributeAlias),
				State.getFeatureStateForThread());

			final String aliasValue = autoAliasUtils.getValue(
				value,
				StringUtils.isNoneBlank(valueAlias),
				State.getFeatureStateForThread());

			final String fixedAliasValue = aliasValue.replaceAll("'", "\\'");

			js.executeScript(
				"arguments[0].setAttribute('" + aliasName + "', '" + fixedAliasValue + "');",
				element);

		} catch (final WebElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}
}
