package au.com.agic.apptesting.utils.impl;


import static com.google.common.base.Preconditions.checkArgument;

import au.com.agic.apptesting.utils.LoggingConfiguration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.LogManager;

import javax.validation.constraints.NotNull;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

/**
 * An implementation of the LogginConfiguration service that configures LogBack
 */
public class LogbackConfiguration implements LoggingConfiguration {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LogbackConfiguration.class);

	@Override
	public void configureLogging(@NotNull final String logfile) {
		checkArgument(StringUtils.isNotBlank(logfile));

		try {
			final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

			final FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
			fileAppender.setContext(loggerContext);
			fileAppender.setName("timestamp");
			// set the file name
			fileAppender.setFile(logfile);

			final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
			encoder.setContext(loggerContext);
			encoder.setPattern("%r %thread %level - %msg%n");
			encoder.start();

			fileAppender.setEncoder(encoder);
			fileAppender.start();

			// attach the rolling file appender to the logger of your choice
			final Logger logbackLogger =
				(Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
			logbackLogger.addAppender(fileAppender);

			/*
				We only want to hear about errors from the libraries that are used by Iridium
			 */
			logbackLogger.setLevel(Level.ERROR);

			/*
				Our own code should be info level
			 */
			final Logger iridiumLogger =
				(Logger) LoggerFactory.getLogger("au.com.agic");
			iridiumLogger.setLevel(Level.INFO);

			/*
				Performance increase for redirected JUL loggers
			 */
			final LevelChangePropagator levelChangePropagator = new LevelChangePropagator();
			levelChangePropagator.setContext(loggerContext);
			levelChangePropagator.setResetJUL(true);
			loggerContext.addListener(levelChangePropagator);

			/*
				Redirect java logging and sys out to slf4j
			 */
			LogManager.getLogManager().reset();
			SLF4JBridgeHandler.removeHandlersForRootLogger();
			SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
			SLF4JBridgeHandler.install();
		} catch (final Exception ex) {
			LOGGER.error("WEBAPPTESTER-BUG-0006: Could not configure Logback", ex);
		}
	}

	@Override
	public void logVersion() {
		try {
			final Optional<InputStream> inputStream =
				Optional.ofNullable(
					getClass().getClassLoader().getResourceAsStream("build.properties"));

			if (inputStream.isPresent()) {
				final Properties prop = new Properties();
				prop.load(inputStream.get());
				LOGGER.info("Iridium Build Timestamp: {}", prop.get("build"));
			}
		} catch (final IOException ex) {
			LOGGER.error("Exception thrown while loading build.properties", ex);
		}
	}
}
