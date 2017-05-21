package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.AutoAliasUtils;
import cucumber.api.java.en.Then;
import javaslang.control.Try;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * This class contains steps that can be used to display help information
 * above the browser
 */
@Component
public class HelpPopupStepDefinitions {

	@Autowired
	private AutoAliasUtils autoAliasUtils;

	/**
	 * Displays a message in a window above the browser. Only works
	 * where Java is able to create a UI.
	 * @param message The message to display
	 * @param timeAlias indicates that the time value is aliased
	 * @param time The time to show the message for
	 * @param ignoreErrors Add this text to ignore any errors
	 */
	@Then("I display the help message \"(.*?)\"(?: for( alias)? \"(.*?)\" seconds)?( ignoring errors)?")
	public void displayMessage(final String message,
							   final String timeAlias,
							   final String time,
							   final String ignoreErrors) {
		try {

			final String timeValue = autoAliasUtils.getValue(
				time,
				isNotBlank(timeAlias),
				State.getFeatureStateForThread());

			final Integer fixedTime = NumberUtils.toInt(timeValue, 5);

			final JFrame frame = new JFrame();
			frame.setAlwaysOnTop(true);
			frame.setUndecorated(true);
			frame.setSize(800, 600);

			/*
				Center the window
			 */
			frame.setLocationRelativeTo(null);

			/*
				Create the message
			 */
			final JLabel label = new JLabel(message);
			final Font labelFont = label.getFont();
			label.setFont(new Font(labelFont.getName(), Font.PLAIN, 48));
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setVerticalAlignment(JLabel.CENTER);
			label.setOpaque(true);
			label.setBackground(new Color(255, 236, 179));
			label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
			frame.getContentPane().add(label);

			/*
				Display the message
			 */
			frame.setVisible(true);
			Try.run(() -> Thread.sleep(fixedTime * 1000));

			/*
				Close the window
			 */
			frame.setVisible(false);
			frame.dispose();
		} catch (final Exception ex) {
			if (!StringUtils.isEmpty(ignoreErrors)) {
				throw ex;
			}
		}
	}
}
