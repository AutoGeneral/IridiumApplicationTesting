package au.com.agic.apptesting;

import au.com.agic.apptesting.profiles.dataset.DataSet;
import au.com.agic.apptesting.profiles.dataset.DatasetsFactory;
import au.com.agic.apptesting.profiles.dataset.DatasetsRootElement;
import au.com.agic.apptesting.profiles.dataset.Setting;
import org.junit.Assert;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class XmlToCsvDatasetConverter {
	private static final DatasetsFactory DATASETS_FACTORY = new DatasetsFactory();

	public static void main(String[] args) {
		Assert.assertEquals(1, args.length);
		String xmlDatasetsFile = args[0];
		String xmlDatasetsFileUrl = XmlToCsvDatasetConverter.class.getResource(xmlDatasetsFile).toString();

		new XmlToCsvDatasetConverter().run(xmlDatasetsFileUrl);
	}

	public void run(String xmlDatasetsFileUrl) {
		Optional<DatasetsRootElement> datasets = DATASETS_FACTORY.getDatasets(xmlDatasetsFileUrl);

		if(datasets.isPresent()) {
			Map<Integer, Map<String, String>> datasetsMap = getDatasets(datasets.get());

			List<String> allKeys = datasetsMap.values().stream()
				.flatMap(i -> i.keySet().stream())
				.collect(Collectors.toList());

			System.out.println(allKeys);
		}
	}

	private Map<Integer, Map<String, String>> getDatasets(@NotNull final DatasetsRootElement profile) {
		checkNotNull(profile);

		final Map<String, String> commonDataSet = getCommonDataset(profile);

		final Map<Integer, Map<String, String>> dataSets = new TreeMap<>();

		int index = 0;
		for (final DataSet dataSet : profile.getDataSets().getDataSets()) {

			if (!dataSets.containsKey(index)) {
				final Map<String, String> newMap = new HashMap<>(commonDataSet);
				dataSets.put(index, newMap);
			}

			for (final Setting setting : dataSet.getSettings()) {
				dataSets.get(index).put(setting.getName(), setting.getValue());
			}

			++index;
		}

		return dataSets;
	}

	private Map<String, String> getCommonDataset(@NotNull final DatasetsRootElement profile) {
		checkNotNull(profile);

		final Map<String, String> commonDataSet = new HashMap<>();

		profile.getDataSets().getCommonDataSet().getSettings().stream()
			/*
				Ensure we add the data set to a sequential index
			 */
			.forEach(e -> commonDataSet.put(e.getName(), e.getValue()));

		return commonDataSet;
	}
}
