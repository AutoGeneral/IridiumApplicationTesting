package au.com.agic.apptesting.utils;

import org.openqa.selenium.WebElement;

import javax.validation.constraints.NotNull;

/**
 * A service that returns elements based on fuzzy selections
 */
public interface SimpleWebElementInteraction {
	/**
	 * This method queries the web page for elements that can be found by the supplied value.
	 * The value is assumed to be either an ID, a name, a xpath, a css selector or a class.
	 *
	 * If the value matches more than one of these selection techniques, the returned element
	 * is undefined. Only use this method when you are sure the value is unique, or at the very
	 * least will return the same element regradless of which selection returns first.
	 *
	 * @param valueAlias True if the value an alias, false otherwise
	 * @param value The string used to find the element
	 * @param featureState The current thread's state object
	 * @return The element, or throw an exception
	 */
	WebElement getClickableElementFoundBy(
		boolean valueAlias,
		@NotNull String value,
		@NotNull FeatureState featureState);

	/**
	 * This method queries the web page for elements that can be found by the supplied value.
	 * The value is assumed to be either an ID, a name, a xpath, a css selector or a class.
	 *
	 * If the value matches more than one of these selection techniques, the returned element
	 * is undefined. Only use this method when you are sure the value is unique, or at the very
	 * least will return the same element regradless of which selection returns first.
	 *
	 * @param valueAlias True if the value an alias, false otherwise
	 * @param value The string used to find the element
	 * @param featureState The current thread's state object
	 * @param wait How long to wait for
	 * @return The element, or throw an exception
	 */
	WebElement getClickableElementFoundBy(
		boolean valueAlias,
		@NotNull String value,
		@NotNull FeatureState featureState,
		long wait);

	/**
	 * This method queries the web page for elements that can be found by the supplied value.
	 * The value is assumed to be either an ID, a name, a xpath, a css selector or a class.
	 * <p>
	 * If the value matches more than one of these selection techniques, the returned element
	 * is undefined. Only use this method when you are sure the value is unique, or at the very
	 * least will return the same element regradless of which selection returns first.
	 *
	 * @param valueAlias   True if the value an alias, false otherwise
	 * @param value        The string used to find the element
	 * @param featureState The current thread's state object
	 */
	void getNotClickableElementFoundBy(
		boolean valueAlias,
		@NotNull String value,
		@NotNull FeatureState featureState);

	/**
	 * This method queries the web page for elements that can be found by the supplied value.
	 * The value is assumed to be either an ID, a name, a xpath, a css selector or a class.
	 * <p>
	 * If the value matches more than one of these selection techniques, the returned element
	 * is undefined. Only use this method when you are sure the value is unique, or at the very
	 * least will return the same element regardless of which selection returns first.
	 *
	 * @param valueAlias   True if the value an alias, false otherwise
	 * @param value        The string used to find the element
	 * @param featureState The current thread's state object
	 * @param wait         How long to wait for
	 */
	void getNotClickableElementFoundBy(
		boolean valueAlias,
		@NotNull String value,
		@NotNull FeatureState featureState,
		long wait);

	/**
	 * This method queries the web page for elements that can be found by the supplied value.
	 * The value is assumed to be either an ID, a name, a xpath, a css selector or a class.
	 *
	 * If the value matches more than one of these selection techniques, the returned element
	 * is undefined. Only use this method when you are sure the value is unique, or at the very
	 * least will return the same element regradless of which selection returns first.
	 *
	 * @param valueAlias True if the value an alias, false otherwise
	 * @param value The string used to find the element
	 * @param featureState The current thread's state object
	 * @return The element, or throw an exception
	 */
	WebElement getVisibleElementFoundBy(
		boolean valueAlias,
		@NotNull String value,
		@NotNull FeatureState featureState);

	/**
	 * This method queries the web page for elements that can be found by the supplied value.
	 * The value is assumed to be either an ID, a name, a xpath, a css selector or a class.
	 *
	 * If the value matches more than one of these selection techniques, the returned element
	 * is undefined. Only use this method when you are sure the value is unique, or at the very
	 * least will return the same element regradless of which selection returns first.
	 *
	 * @param valueAlias True if the value an alias, false otherwise
	 * @param value The string used to find the element
	 * @param featureState The current thread's state object
	 * @param wait How long to wait for
	 * @return The element, or throw an exception
	 */
	WebElement getVisibleElementFoundBy(
		boolean valueAlias,
		@NotNull String value,
		@NotNull FeatureState featureState,
		long wait);

	/**
	 * This method queries the web page for elements that can be found by the supplied value.
	 * The value is assumed to be either an ID, a name, a xpath, a css selector or a class.
	 *
	 * When none of the queries matches the method will return. An exception will be thrown
	 * if at least one query matches an element for the duration of the timeout.
	 *
	 * @param valueAlias True if the value an alias, false otherwise
	 * @param value The string used to find the element
	 * @param featureState The current thread's state object
	 */
	void getNotVisibleElementFoundBy(
		boolean valueAlias,
		@NotNull String value,
		@NotNull FeatureState featureState);

	/**
	 * This method queries the web page for elements that can be found by the supplied value.
	 * The value is assumed to be either an ID, a name, a xpath, a css selector or a class.
	 *
	 * When none of the queries matches the method will return. An exception will be thrown
	 * if at least one query matches an element for the duration of the timeout.
	 *
	 * @param valueAlias True if the value an alias, false otherwise
	 * @param value The string used to find the element
	 * @param featureState The current thread's state object
	 * @param wait How long to wait for
	 */
	void getNotVisibleElementFoundBy(
		boolean valueAlias,
		@NotNull String value,
		@NotNull FeatureState featureState,
		long wait);

	/**
	 * This method queries the web page for elements that can be found by the supplied value.
	 * The value is assumed to be either an ID, a name, a xpath, a css selector or a class.
	 *
	 * If the value matches more than one of these selection techniques, the returned element
	 * is undefined. Only use this method when you are sure the value is unique, or at the very
	 * least will return the same element regradless of which selection returns first.
	 *
	 * @param valueAlias True if the value an alias, false otherwise
	 * @param value The string used to find the element
	 * @param featureState The current thread's state object
	 * @return The element, or throw an exception
	 */
	WebElement getPresenceElementFoundBy(
		boolean valueAlias,
		@NotNull String value,
		@NotNull FeatureState featureState);

	/**
	 * This method queries the web page for elements that can be found by the supplied value.
	 * The value is assumed to be either an ID, a name, a xpath, a css selector or a class.
	 *
	 * If the value matches more than one of these selection techniques, the returned element
	 * is undefined. Only use this method when you are sure the value is unique, or at the very
	 * least will return the same element regradless of which selection returns first.
	 *
	 * @param valueAlias True if the value an alias, false otherwise
	 * @param value The string used to find the element
	 * @param featureState The current thread's state object
	 * @param wait How long to wait for
	 * @return The element, or throw an exception
	 */
	WebElement getPresenceElementFoundBy(
		boolean valueAlias,
		@NotNull String value,
		@NotNull FeatureState featureState,
		long wait);

	/**
	 * This method queries the web page for elements that can be found by the supplied value.
	 * The value is assumed to be either an ID, a name, a xpath, a css selector or a class.
	 *
	 * When none of the queries matches the method will return. An exception will be thrown
	 * if at least one query matches an element for the duration of the timeout.
	 *
	 * @param valueAlias True if the value an alias, false otherwise
	 * @param value The string used to find the element
	 * @param featureState The current thread's state object
	 */
	void getNotPresenceElementFoundBy(
		boolean valueAlias,
		@NotNull String value,
		@NotNull FeatureState featureState);

	/**
	 * This method queries the web page for elements that can be found by the supplied value.
	 * The value is assumed to be either an ID, a name, a xpath, a css selector or a class.
	 *
	 * When none of the queries matches the method will return. An exception will be thrown
	 * if at least one query matches an element for the duration of the timeout.
	 *
	 * @param valueAlias True if the value an alias, false otherwise
	 * @param value The string used to find the element
	 * @param featureState The current thread's state object
	 * @param wait How long to wait for
	 */
	void getNotPresenceElementFoundBy(
		boolean valueAlias,
		@NotNull String value,
		@NotNull FeatureState featureState,
		long wait);
}
