package au.com.agic.apptesting.profiles.dataset;

import au.com.agic.apptesting.profiles.CsvFileAccess;
import au.com.agic.apptesting.profiles.FileProfileAccess;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.FilenameUtils;

import java.util.Optional;

public class DatasetsFactory {
	private static final String XML_EXTENSION = "xml";
	private static final String CSV_EXTENSION = "csv";

	private static final CSVParserToDataSetTransformer DATA_SET_CSV_TRANSFORMER = new CSVParserToDataSetTransformer();

	public Optional<DatasetsRootElement> getDatasets(final String filename) {
		String fileExtension = FilenameUtils.getExtension(filename);

		if (fileExtension != null) {
			switch (fileExtension) {
				case CSV_EXTENSION: {
					CsvFileAccess datasetCsvAccess = new CsvFileAccess(filename);
					Optional<CSVParser> csvParser = datasetCsvAccess.getCsvRecords();

					if (csvParser.isPresent()) {
						return Optional.of(DATA_SET_CSV_TRANSFORMER.transform(csvParser.get()));
					}
					return Optional.empty();
				}
				case XML_EXTENSION:
				default: {
					FileProfileAccess<DatasetsRootElement> datasetXmlAccess = new FileProfileAccess<>(
						filename,
						DatasetsRootElement.class);

					return datasetXmlAccess.getProfile();
				}
			}
		}
	}
}
