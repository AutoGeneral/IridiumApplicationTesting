package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.exception.JarDownloadException;
import au.com.agic.apptesting.utils.JarDownloader;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Downloads the JAR file and saves it locally
 */
public class JarDownloaderImpl implements JarDownloader {
	private static final Logger LOGGER = LoggerFactory.getLogger(JarDownloaderImpl.class);
	private static final String HTTP_SCHEME = "http";

	@Override
	public void downloadJar(@NotNull final List<File> tempFiles) {
		checkNotNull(tempFiles);

		try {
			/*
				Get the path to the JAR file
			 */
			final URI uri = JarDownloaderImpl.class
				.getProtectionDomain()
				.getCodeSource()
				.getLocation()
				.toURI();

			if (uri.getScheme().startsWith(HTTP_SCHEME)) {
				LOGGER.info("Downloading JAR file locally for ZAP and Cucumber to use");

				/*
					Copy it somewhere locally
				 */
				final File copy = File.createTempFile("iridium", ".jar");
				FileUtils.copyURLToFile(uri.toURL(), copy);

				/*
					Make a note of the temp file
				 */
				tempFiles.add(copy);

				/*
					Set the system property so other libraries can find this file
				 */
				System.setProperty(LOCAL_JAR_FILE_SYSTEM_PROPERTY, copy.getAbsolutePath());
			}
		} catch (Exception ex) {
			throw new JarDownloadException(ex);
		}
	}
}
