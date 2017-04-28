package au.com.agic.apptesting.profiles;

import au.com.agic.apptesting.exception.ConfigurationException;
import javaslang.control.Try;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

class FileContentRetrieval {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileContentRetrieval.class);

	private static final int TIMEOUT = 15000;

	Optional<String> retrieveStringFromFile(@NotNull final String filename) {
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
				.of(() -> retrieveStringFromLocalFile(filename))
				/*
					Fall back to getting the contents of a remote file
				 */
				.recover(exception -> retrieveStringFromRemoteFile(filename))
				/*
					Convert the pojo to an optional
				 */
				.map(pojo -> Optional.of(pojo))
				.getOrElseGet(exception -> {
					LOGGER.error("There was an exception retrieving content from the file {}",
						filename,
						exception);
					return Optional.empty();
				});
		}
		return Optional.empty();
	}

	private String retrieveStringFromLocalFile(@NotNull final String localFilename)  {
		checkArgument(StringUtils.isNoneBlank(localFilename));

		try {
			return FileUtils.readFileToString(new File(localFilename), Charset.defaultCharset());
		} catch (final IOException ex) {
			throw new ConfigurationException(ex);
		}
	}

	private String retrieveStringFromRemoteFile(@NotNull final String remoteFileName)  {
		checkArgument(StringUtils.isNoneBlank(remoteFileName));

		File copy = null;
		try {
			copy = File.createTempFile("capabilities", ".tmp");
			FileUtils.copyURLToFile(new URL(remoteFileName), copy, TIMEOUT, TIMEOUT);
			return retrieveStringFromLocalFile(copy.getAbsolutePath());
		} catch (final IOException ex) {
			throw new ConfigurationException(ex);
		} finally {
			if (copy != null) {
				FileUtils.deleteQuietly(copy);
			}
		}
	}
}
