package au.com.agic.apptesting.profiles;

import au.com.agic.apptesting.exception.FileProfileAccessException;
import io.vavr.control.Try;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.nio.charset.Charset;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

public class FileProfileAccess<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileProfileAccess.class);

	private final Unmarshaller jaxbUnmarshaller;
	private final String filename;

	private final FileContentRetrieval fileContentRetrieval = new FileContentRetrieval();

	public FileProfileAccess(final String filename, @NotNull final Class<T> clazz) {
		checkNotNull(clazz);

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
			Optional<String> fileContent = fileContentRetrieval.retrieveStringFromFile(filename);

			if (fileContent.isPresent()) {
				return Try.of(() -> (T) jaxbUnmarshaller.unmarshal(IOUtils.toInputStream(
					fileContent.get(), Charset.defaultCharset())))
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
		}
		return Optional.empty();
	}
}
