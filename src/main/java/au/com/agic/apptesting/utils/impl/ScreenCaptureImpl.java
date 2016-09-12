package au.com.agic.apptesting.utils.impl;

import static org.monte.media.AudioFormatKeys.EncodingKey;
import static org.monte.media.AudioFormatKeys.FrameRateKey;
import static org.monte.media.AudioFormatKeys.KeyFrameIntervalKey;
import static org.monte.media.AudioFormatKeys.MIME_AVI;
import static org.monte.media.AudioFormatKeys.MediaType;
import static org.monte.media.AudioFormatKeys.MediaTypeKey;
import static org.monte.media.AudioFormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.QualityKey;

import au.com.agic.apptesting.utils.ScreenCapture;

import org.monte.media.Format;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.validation.constraints.NotNull;

/**
 * An implementation of a screen capture service using the Monte Media Library
 * http://www.randelshofer.ch/monte/ <p> See http://unmesh.me/2012/01/13/recording-screencast-of-selenium-tests-in-java/
 */
public class ScreenCaptureImpl implements ScreenCapture {

	private static final int DEPTH = 24;
	private static final int FRAMERATE = 15;
	private static final float QUALITY = 1.0f;
	private static final int KEYFRAME_INTERVAL = 15 * 60;
	private static final int FRAME_RATE_KEY = 30;
	private static final Logger LOGGER = LoggerFactory.getLogger(ScreenCaptureImpl.class);
	private ScreenRecorder screenRecorder;

	@Override
	public void start(@NotNull final String saveDir) {
		try {
			final GraphicsConfiguration gc = GraphicsEnvironment
				.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration();

			screenRecorder = new ScreenRecorder(gc,
				null,
				new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
				new Format(MediaTypeKey,
					MediaType.VIDEO,
					EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
					CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
					DepthKey, DEPTH,
					FrameRateKey, Rational.valueOf(FRAMERATE),
					QualityKey, QUALITY,
					KeyFrameIntervalKey, KEYFRAME_INTERVAL),
				new Format(MediaTypeKey,
					MediaType.VIDEO,
					EncodingKey, "black",
					FrameRateKey, Rational.valueOf(FRAME_RATE_KEY)),
				null,
				new File(saveDir));

			screenRecorder.start();
		} catch (final Exception ex) {
			LOGGER.error("Exception thrown creating or starting a screen recorder", ex);
		}
	}

	@Override
	public void stop() {
		try {
			if (screenRecorder != null) {
				screenRecorder.getCreatedMovieFiles().stream()
					.forEach(
						e -> LOGGER.info("Screencast saved to: \"{}\"", e.getAbsolutePath())
					);
				screenRecorder.stop();
			}
		} catch (final IOException ex) {
			LOGGER.error("Exception thrown stopping a screen recorder", ex);
		}
	}
}
