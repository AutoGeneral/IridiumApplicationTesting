package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.MouseMovementUtils;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.awt.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of MouseMovementUtils
 */
@org.springframework.stereotype.Component
public class MouseMovementUtilsImpl implements MouseMovementUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(MouseMovementUtilsImpl.class);

	@Autowired
	private SystemPropertyUtils systemPropertyUtils;

	@Override
	public void mouseGlide(int x1, int y1, int x2, int y2, int time, int steps) {
		try {
			final Robot r = new Robot();

			final double dx = (x2 - x1) / ((double) steps);
			final double dy = (y2 - y1) / ((double) steps);
			final double dt = time / ((double) steps);
			for (int step = 1; step <= steps; step++) {
				Thread.sleep((int) dt);
				r.mouseMove((int) (x1 + dx * step), (int) (y1 + dy * step));
			}
		} catch (final AWTException | InterruptedException ex) {
			LOGGER.error("WEBAPPTESTER-BUG-0010: Exception thrown while moving mouse cursor", ex);
		}
	}

	@Override
	public void mouseGlide(int x2, int y2, int time, int steps) {
		final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
		mouseGlide(mouseLocation.x, mouseLocation.y, x2, y2, time, steps);
	}

	@Override
	public void mouseGlide(@NotNull final JavascriptExecutor javascriptExecutor,
						   @NotNull final WebElement element,
						   int time,
						   int steps) {
		checkNotNull(element);

		final boolean moveMouseCursor =
			systemPropertyUtils.getPropertyAsBoolean(
				Constants.MOVE_CURSOR_TO_ELEMENT, false);

		final int verticalOffset =
			systemPropertyUtils.getPropertyAsInt(
				Constants.MOUSE_MOVE_VERTICAL_OFFSET, 0);

		if (moveMouseCursor) {

			final float zoom = systemPropertyUtils.getPropertyAsFloat(
				Constants.SCREEN_ZOOM_FACTOR, 1.0f);

			final org.openqa.selenium.Point viewPoint = element.getLocation();
			final Long height = (Long)javascriptExecutor.executeScript(
				"return arguments[0].clientHeight;", element);
			final Long width = (Long)javascriptExecutor.executeScript(
				"return arguments[0].clientWidth;", element);
			mouseGlide(
				(int)((viewPoint.x + width / 2) * zoom),
				(int)((viewPoint.y + verticalOffset + height / 2) * zoom),
				Constants.MOUSE_MOVE_TIME,
				Constants.MOUSE_MOVE_STEPS);
		}
	}
}
