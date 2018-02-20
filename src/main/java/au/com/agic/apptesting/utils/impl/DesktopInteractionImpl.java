package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.DesktopInteraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.awt.*;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of DesktopInteraction
 */
public class DesktopInteractionImpl implements DesktopInteraction {

	private static final Logger LOGGER = LoggerFactory.getLogger(DesktopInteractionImpl.class);

	@Override
	public void openWebpage(@NotNull final URI uri) {
		checkNotNull(uri);

		if (Desktop.isDesktopSupported()) {
			final Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(uri);
				} catch (final Exception ex) {
					LOGGER.error("Failed to open the web site", ex);
				}
			}
		}
	}
}
