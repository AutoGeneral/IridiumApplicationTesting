package au.com.agic.apptesting.utils;

import org.openqa.selenium.WebElement;

import java.util.concurrent.CompletableFuture;

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
	 * @return A promise that will have the element, or throw an exception
	 */
	CompletableFuture<WebElement> getClickableElementFoundBy(
		final boolean valueAlias,
		final String value,
		final FeatureState featureState);

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
	 * @return A promise that will have the element, or throw an exception
	 */
	CompletableFuture<WebElement> getClickableElementFoundBy(
		final boolean valueAlias,
		final String value,
		final FeatureState featureState,
		final long wait);

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
	 * @return A promise that will have the element, or throw an exception
	 */
	CompletableFuture<WebElement> getVisibleElementFoundBy(
		final boolean valueAlias,
		final String value,
		final FeatureState featureState);

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
	 * @return A promise that will have the element, or throw an exception
	 */
	CompletableFuture<WebElement> getVisibleElementFoundBy(
		final boolean valueAlias,
		final String value,
		final FeatureState featureState,
		final long wait);

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
	 * @return A promise that will have the element, or throw an exception
	 */
	CompletableFuture<WebElement> getPresenceElementFoundBy(
		final boolean valueAlias,
		final String value,
		final FeatureState featureState);

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
	 * @return A promise that will have the element, or throw an exception
	 */
	CompletableFuture<WebElement> getPresenceElementFoundBy(
		final boolean valueAlias,
		final String value,
		final FeatureState featureState,
		final long wait);
}
