package au.com.agic.apptesting.profiles;

import au.com.agic.apptesting.exception.FileProfileAccessException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import javaslang.control.Try;

public class FileProfileAccess<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileProfileAccess.class);

	private static final int TIMEOUT = 15000;

	private final Unmarshaller jaxbUnmarshaller;
	private final String filename;

	public FileProfileAccess(final String filename, final Class<T> clazz) {
		this.filename = filename;

		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (final JAXBException ex) {
			throw new FileProfileAccessException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	public Optional<T> getProfile() {

		try {
			if (StringUtils.isNotBlank(filename)) {

				final String profileString = processLocalFile(filename)
					.orElseGet(() ->
						Try.of(() -> processRemoteFile(filename).orElseGet(() -> ""))
							.getOrElse("")
					);

				return Optional.of((T) jaxbUnmarshaller.unmarshal(
					IOUtils.toInputStream(profileString))
				);
			}
		} catch (final Exception ex) {
			LOGGER.error("There was an exception unmarshalling configuration from the file {}",
				filename,
				ex);
		}

		return Optional.empty();
	}

	private Optional<String> processLocalFile(final String localFilename) {
		try {
			if (Files.exists(Paths.get(localFilename))) {
				return Optional.of(FileUtils.readFileToString(new File(localFilename)));
			}
		} catch (final Exception ignored) {
			/*
				The path is probably a url that will cause an exception to be thrown
			 */
		}

		return Optional.empty();
	}

	private Optional<String> processRemoteFile(final String remoteFileName) throws IOException {
		final File copy = File.createTempFile("capabilities", ".xml");
		FileUtils.copyURLToFile(new URL(remoteFileName), copy, TIMEOUT, TIMEOUT);
		return processLocalFile(copy.getAbsolutePath());
	}

}
