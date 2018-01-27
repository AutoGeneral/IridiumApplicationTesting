package au.com.agic.apptesting;

import au.com.agic.apptesting.profiles.CsvFileAccess;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

public class CsvFileAccessTest {
	@Test
	public void testNullValuesAndEmptyStrings() throws IOException {
		String dataSetFile = this.getClass().getResource("/trickyDataset.csv").toString();

		CsvFileAccess datasetCsvAccess = new CsvFileAccess(dataSetFile);
		Optional<CSVParser> csvParser = datasetCsvAccess.getCsvRecords();

		assertThat(csvParser.isPresent(), is(true));

		CSVParser csvRecords = csvParser.get();

		List<CSVRecord> records = csvRecords.getRecords();
		assertThat(records.size(), is(4));

		checkRow(records.get(0), is(""), is(nullValue()), is("hello"));
		checkRow(records.get(1), is("not empty"), is("not null"), is("trimmed"));
		checkRow(records.get(2), is(""), is(nullValue()), is("comma, here"));
		checkRow(records.get(3), is(""), is(nullValue()), is("  not trimmed  "));
	}

	private void checkRow(final CSVRecord row,
						  final Matcher<Object> expectedColumn1,
						  final Matcher<Object> expectedColumn2,
						  final Matcher<Object> expectedColumn3) {
		assertThat(row.get("Sometimes empty"), expectedColumn1);
		assertThat(row.get("Sometimes null"), expectedColumn2);
		assertThat(row.get("Surrounded in spaces"), expectedColumn3);
	}
}
