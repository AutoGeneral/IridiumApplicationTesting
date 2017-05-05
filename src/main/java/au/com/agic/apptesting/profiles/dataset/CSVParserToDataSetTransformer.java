package au.com.agic.apptesting.profiles.dataset;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class CSVParserToDataSetTransformer {
	public DatasetsRootElement transform(@NotNull final CSVParser csvRecords) {
		checkNotNull(csvRecords);

		Set<String> headings = csvRecords.getHeaderMap().keySet();

		DatasetsRootElement datasetsRootElement = new DatasetsRootElement();

		for (CSVRecord record : csvRecords) {
			DataSet dataSet = new DataSet();
			List<Setting> settings = dataSet.getSettings();

			for (String heading : headings) {
				Setting setting = new Setting();
				setting.setName(heading);
				setting.setValue(record.get(heading));

				settings.add(setting);
			}

			datasetsRootElement.getDataSets().getDataSets().add(dataSet);
		}

		return datasetsRootElement;
	}
}
