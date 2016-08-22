package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.profiles.FileProfileAccess;
import au.com.agic.apptesting.profiles.configuration.Configuration;
import au.com.agic.apptesting.profiles.configuration.FeatureGroup;
import au.com.agic.apptesting.profiles.configuration.UrlMapping;
import au.com.agic.apptesting.profiles.dataset.DataSet;
import au.com.agic.apptesting.profiles.dataset.DatasetsRootElement;
import au.com.agic.apptesting.profiles.dataset.Setting;
import au.com.agic.apptesting.utils.ApplicationUrlLoader;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Loads the application urls from configuration
 */
public class ApplicationUrlLoaderImpl implements ApplicationUrlLoader {

	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	private static final FileProfileAccess<Configuration> PROFILE_ACCESS = new FileProfileAccess<>(
		SYSTEM_PROPERTY_UTILS.getProperty(Constants.CONFIGURATION),
		Configuration.class);

	private static final FileProfileAccess<DatasetsRootElement> DATASET_ACCESS = new FileProfileAccess<>(
		SYSTEM_PROPERTY_UTILS.getProperty(Constants.DATA_SETS_PROFILE_SYSTEM_PROPERTY),
		DatasetsRootElement.class);

	private String getAppUrl() {
		final String appUrl = SYSTEM_PROPERTY_UTILS.getProperty(Constants.APP_URL_OVERRIDE_SYSTEM_PROPERTY);
		if (StringUtils.isNotBlank(appUrl)) {
			return appUrl;
		}

		return null;
	}

	@Override
	public List<UrlMapping> getAppUrls(final String featureGroup) {

        /*
			Deal with the override
         */
		final String appUrlOverride = getAppUrl();
		if (StringUtils.isNotBlank(appUrlOverride)) {
			return Arrays.asList(new UrlMapping(appUrlOverride));
		}

		final Optional<Configuration> configuration = PROFILE_ACCESS.getProfile();

		if (configuration.isPresent()) {
			final List<UrlMapping> retValue = getUrlMappings(configuration.get(), featureGroup);
			return getLimitedAppUrls(retValue);
		}

		/*
			There are no mappings to return
		 */
		return new ArrayList<>();
	}

	private List<UrlMapping> getLimitedAppUrls(@NotNull final List<UrlMapping> completeList) {
		checkNotNull(completeList);

		final String limitedUrls = SYSTEM_PROPERTY_UTILS.getProperty(Constants.NUMBER_URLS_SYSTEM_PROPERTY);
		if (StringUtils.isNoneBlank(limitedUrls)) {
			try {
				Collections.shuffle(completeList, SecureRandom.getInstance("SHA1PRNG"));

				final Integer limit = Integer.parseInt(limitedUrls);

				final List<UrlMapping> subList = new ArrayList<>();
				for (int i = 0; i < Math.min(limit, completeList.size()); ++i) {
					subList.add(completeList.get(i));
				}

				return subList;
			} catch (final NumberFormatException | NoSuchAlgorithmException ignored) {
				/*
					Invalid input that we ignore
				 */
			}
		}

		return completeList;
	}

	@Override
	public Map<Integer, Map<String, String>> getDatasets() {
		final Optional<DatasetsRootElement> dataset = DATASET_ACCESS.getProfile();

		/*
			It is possible that a profile does not exist with data sets for this featureGroup
		 */
		if (!dataset.isPresent()) {
			return new HashMap<>();
		}

		return getDatasets(dataset.get());

	}

	private Map<Integer, Map<String, String>> getDatasets(@NotNull final DatasetsRootElement profile) {
		checkNotNull(profile);

		final Map<String, String> commonDataSet = getCommonDataset(profile);

		final Map<Integer, Map<String, String>> dataSets = new HashMap<>();

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

	private List<UrlMapping> getUrlMappings(@NotNull final Configuration configuration, final String app) {
		checkNotNull(configuration);

		return configuration.getUrlMappings().getFeatureGroups().stream()
			.filter(e -> StringUtils.endsWithIgnoreCase(app, e.getName()))
			.findFirst()
			.map(FeatureGroup::getUrlMappings)
			.orElse(new ArrayList<>());
	}

	/**
	 * @param profile The combined profile
	 * @return The common data set values to be applied to all other datasets
	 */
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
