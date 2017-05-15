package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.profiles.FileProfileAccess;
import au.com.agic.apptesting.profiles.configuration.*;
import au.com.agic.apptesting.profiles.dataset.DataSet;
import au.com.agic.apptesting.profiles.dataset.DatasetsFactory;
import au.com.agic.apptesting.profiles.dataset.DatasetsRootElement;
import au.com.agic.apptesting.profiles.dataset.Setting;
import au.com.agic.apptesting.utils.ApplicationUrlLoader;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Loads the application urls from configuration
 */
public class ApplicationUrlLoaderImpl implements ApplicationUrlLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationUrlLoaderImpl.class);

	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	private static final DatasetsFactory DATASETS_FACTORY = new DatasetsFactory();

	private FileProfileAccess<Configuration> profileAccess;

	private Optional<DatasetsRootElement> datasets;

	public void initialise() {
		final String configFile = SYSTEM_PROPERTY_UTILS.getProperty(
			Constants.CONFIGURATION);

		final String datsetsFile = SYSTEM_PROPERTY_UTILS.getProperty(
			Constants.DATA_SETS_PROFILE_SYSTEM_PROPERTY);

		profileAccess = new FileProfileAccess<>(
			configFile,
			Configuration.class);

		datasets = DATASETS_FACTORY.getDatasets(datsetsFile);
	}

	private String getAppUrl() {
		final String appUrl = SYSTEM_PROPERTY_UTILS.getProperty(Constants.APP_URL_OVERRIDE_SYSTEM_PROPERTY);
		if (StringUtils.isNotBlank(appUrl)) {
			return appUrl;
		}

		return null;
	}

	@Override
	public List<UrlMapping> getAppUrls(final String featureGroup) {

		checkState(profileAccess != null, "initialise() must be called");
		checkState(datasets != null, "initialise() must be called");

        /*
			Deal with the override. This system property takes precedence over
			all other options.
         */
		final String appUrlOverride = getAppUrl();
		if (StringUtils.isNotBlank(appUrlOverride)) {
			LOGGER.info("Getting URL from global system property");
			return Arrays.asList(new UrlMapping(appUrlOverride));
		}

		/*
			We can also define a collection of URLs as system properties.
		 */
		final List<String> normalisedKeys = SYSTEM_PROPERTY_UTILS.getNormalisedProperties();
		final List<Url> systemPropValues = normalisedKeys.stream()
			.map(Constants.APP_URL_OVERRIDE_SYSTEM_PROPERTY_REGEX::matcher)
			.filter(Matcher::matches)
			.map(x -> new Url(SYSTEM_PROPERTY_UTILS.getProperty(x.group(0)), x.group(1)))
			.collect(Collectors.toList());
		if (!systemPropValues.isEmpty()) {
			LOGGER.info("Getting URL from specific system property");
			return Arrays.asList(new UrlMapping(systemPropValues));
		}

		/*
			The final option is to get the mappins from the csv or xml file
		 */
		final Optional<Configuration> configuration = profileAccess.getProfile();

		if (configuration.isPresent()) {
			LOGGER.info("Getting URL config file");
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

		checkState(profileAccess != null, "initialise() must be called");
		checkState(datasets != null, "initialise() must be called");

		/*
			It is possible that a profile does not exist with data sets for this featureGroup
		 */
		if (!datasets.isPresent()) {
			return new HashMap<>();
		}

		return getDatasets(datasets.get());
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

		 return Optional.ofNullable(configuration)
			.map(Configuration::getUrlMappings)
			.map(URLMappings::getFeatureGroups)
			.map(featureGroups ->
				featureGroups.stream().filter(e -> StringUtils.endsWithIgnoreCase(app, e.getName()))
					.findFirst()
					.map(FeatureGroup::getUrlMappings)
					.orElse(new ArrayList<>())
			)
			.orElse(new ArrayList<UrlMapping>());
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
