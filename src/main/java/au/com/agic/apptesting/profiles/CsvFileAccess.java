package au.com.agic.apptesting.profiles;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.Optional;

public class CsvFileAccess {

	private static final Logger LOGGER = LoggerFactory.getLogger(CsvFileAccess.class);
	private static final FileContentRetrieval FILE_CONTENT_RETRIEVAL = new FileContentRetrieval();
	private static final String NULL_STRING = "UNDEFINED";
	private static final char COMMENT_CHARACTER = '#';

	private final String filename;

	public CsvFileAccess(final String filename) {
		this.filename = filename;
	}

	public Optional<CSVParser> getCsvRecords() {
		if (StringUtils.isNotBlank(filename)) {
			Optional<String> fileContentOptional = FILE_CONTENT_RETRIEVAL.retrieveStringFromFile(filename);

			if (fileContentOptional.isPresent()) {
				try {
					CSVFormat csvFormat = CSVFormat.RFC4180
						.withFirstRecordAsHeader()
						.withIgnoreSurroundingSpaces()
						.withNullString(NULL_STRING)
						.withCommentMarker(COMMENT_CHARACTER)
						.withIgnoreEmptyLines(true);

					String fileContent = fileContentOptional.get();

					// ensure the correct line endings
					String recordSeparator = csvFormat.getRecordSeparator();
					fileContent = fileContent.replaceAll("\r\n", "\n").replaceAll("\n", recordSeparator);

					StringReader stringReader = new StringReader(fileContent);

					return Optional.of(csvFormat.parse(stringReader));
				} catch (Exception exception) {
					LOGGER.error("There was an exception extracting CSV from the file {}",
						filename,
						exception);
				}
			}
		}

		return Optional.empty();
	}
}
