package au.com.agic.apptesting.profiles;

import au.com.agic.apptesting.exception.ConfigurationException;
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

		if (StringUtils.isNotBlank(filename)) {
			/*
				Attempt to get the contents of the local file, falling back
				to getting the contents of a remote file. If all fail,
				the result will be empty
			 */
			return Try
				/*
					Attempt to get the contents of a local file
				 */
				.of(() -> processLocalFile(filename))
				/*
					Fall back to getting the contents of a remote file
				 */
				.recover(exception -> processRemoteFile(filename))
				/*
					Convert the file contents to a pojo
				 */
				.mapTry(profileString -> (T) jaxbUnmarshaller.unmarshal(IOUtils.toInputStream(profileString)))
				/*
					Convert the pojo to an optional
				 */
				.map(pojo -> Optional.of(pojo))
				/*
					If we couldn't get the file contents, log a message and return an empty optional
				 */
				.getOrElseGet(exception -> {
					LOGGER.error("There was an exception unmarshalling configuration from the file {}",
						filename,
						exception);
					return Optional.empty();
				});
		}

		return Optional.empty();
	}

	private String processLocalFile(final String localFilename)  {
		try {
			if (Files.exists(Paths.get(localFilename))) {
				return FileUtils.readFileToString(new File(localFilename));
			}

			throw new ConfigurationException("File " + localFilename + " does not exist");
		} catch (final IOException ex) {
			throw new ConfigurationException(ex);
		}
	}

	private String processRemoteFile(final String remoteFileName)  {
		try {
			final File copy = File.createTempFile("capabilities", ".xml");
			FileUtils.copyURLToFile(new URL(remoteFileName), copy, TIMEOUT, TIMEOUT);
			return processLocalFile(copy.getAbsolutePath());
		} catch (final IOException ex) {
			throw new ConfigurationException(ex);
		}
	}

}
