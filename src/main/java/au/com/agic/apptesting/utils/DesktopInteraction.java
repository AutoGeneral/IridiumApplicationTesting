package au.com.agic.apptesting.utils;

import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * Module with methods for interacting with the desktop
 */
public interface DesktopInteraction {

	/**
	 * Opens a web page
	 *
	 * @param uri The address of the web page to open
	 */
	void openWebpage(@NotNull URI uri);
}
