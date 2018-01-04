package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import cucumber.api.java.en.Then;
import io.vavr.control.Try;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;


/**
 * This class contains steps that can be used to display help information
 * above the browser
 */
@Component
public class HelpPopupStepDefinitions {

	private static final Logger LOGGER = LoggerFactory.getLogger(HelpPopupStepDefinitions.class);
	private static final int MESSAGE_WIDTH = 800;
	private static final int MESSAGE_HEIGHT = 600;
	private static final int BORDER_THICKNESS = 5;
	private static final Integer DEFAULT_TIME_TO_DISPLAY = 5;
	private static final int MESSAGE_FONT_SIZE = 48;
	private static final Color MESSAGE_BACKGROUND_COLOUR = new Color(255, 236, 179);

	@Autowired
	private AutoAliasUtils autoAliasUtils;

	/**
	 * Displays a message in a window above the browser. Only works
	 * where Java is able to create a UI.
	 *
	 * @param message      The message to display
	 * @param width        The width of the help popup
	 * @param height       The height of the help popup
	 * @param fontSize     The font size
	 * @param timeAlias    indicates that the time value is aliased
	 * @param time         The time to show the message for
	 * @param ignoreErrors Add this text to ignore any errors
	 */
	@Then("I display the help message \"(.*?)\""
		+ "(?: in a window sized \"(.*?)x(.*?)\")?"
		+ "(?: with font size \"(.*?)\")?"
		+ "(?: for( alias)? \"(.*?)\" seconds)?( ignoring errors)?")
	public void displayMessage(
		final String message,
		final String width,
		final String height,
		final String fontSize,
		final String timeAlias,
		final String time,
		final String ignoreErrors) {

		try {

			final String timeValue = StringUtils.isBlank(time)
				? DEFAULT_TIME_TO_DISPLAY.toString()
				: autoAliasUtils.getValue(
				time,
				StringUtils.isNotBlank(timeAlias),
				State.getFeatureStateForThread());

			final Integer fixedTime = NumberUtils.toInt(timeValue, DEFAULT_TIME_TO_DISPLAY);
			final Integer fixedWidth = NumberUtils.toInt(width, MESSAGE_WIDTH);
			final Integer fixedHeight = NumberUtils.toInt(height, MESSAGE_HEIGHT);
			final Integer fixedFont = NumberUtils.toInt(fontSize, MESSAGE_FONT_SIZE);

			final JFrame frame = new JFrame();
			frame.setAlwaysOnTop(true);
			frame.setUndecorated(true);
			frame.setSize(fixedWidth, fixedHeight);

			/*
				Center the window
			 */
			frame.setLocationRelativeTo(null);

			/*
				Create the message
			 */
			final JLabel label = new JLabel(
				"<html><p style='padding: 20px'>"
					+ message
					+ "</p></html>");
			final Font labelFont = label.getFont();
			label.setFont(new Font(labelFont.getName(), Font.PLAIN, fixedFont));
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setVerticalAlignment(JLabel.CENTER);
			label.setOpaque(true);
			label.setMaximumSize(new Dimension(fixedWidth, fixedHeight));
			label.setBackground(MESSAGE_BACKGROUND_COLOUR);
			label.setBorder(BorderFactory.createLineBorder(Color.BLACK, BORDER_THICKNESS));
			frame.getContentPane().add(label);

			/*
				Display the message
			 */
			frame.setVisible(true);
			Try.run(() -> Thread.sleep(fixedTime * Constants.MILLISECONDS_PER_SECOND));

			/*
				Close the window
			 */
			frame.setVisible(false);
			frame.dispose();
		} catch (final Exception ex) {
			LOGGER.error("Could not display popup", ex);
			if (!StringUtils.isEmpty(ignoreErrors)) {
				throw ex;
			}
		}
	}
}
