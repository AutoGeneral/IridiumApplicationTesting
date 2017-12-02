package au.com.agic.apptesting.utils.impl;


import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.profiles.FileProfileAccess;
import au.com.agic.apptesting.profiles.configuration.Configuration;
import au.com.agic.apptesting.profiles.configuration.Settings;
import au.com.agic.apptesting.utils.DesiredCapabilitiesLoader;
import au.com.agic.apptesting.utils.SystemPropertyUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Provides a service for loading desired capability profiles
 */
public class DesiredCapabilitiesLoaderImpl implements DesiredCapabilitiesLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(DesiredCapabilitiesLoaderImpl.class);
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	private FileProfileAccess<Configuration> profileAccess = new FileProfileAccess<>(
		SYSTEM_PROPERTY_UTILS.getProperty(Constants.CONFIGURATION),
		Configuration.class);

	@Override
	public void initialise() {
		profileAccess = new FileProfileAccess<>(
			SYSTEM_PROPERTY_UTILS.getProperty(Constants.CONFIGURATION),
			Configuration.class);
	}

	@NotNull
	@Override
	public List<DesiredCapabilities> getCapabilities() {

		checkState(profileAccess != null, "initialise() must be called first");

		final String groupName = SYSTEM_PROPERTY_UTILS.getProperty(
			Constants.GROUP_NAME_SYSTEM_PROPERTY);

		final Optional<Configuration> configuration = profileAccess.getProfile();

		return configuration
			.map(Configuration::getSettings)
			.map(Settings::getDesiredCapabilities)
			.map(x -> x.stream()
				.filter(y -> BooleanUtils.toBooleanDefaultIfNull(BooleanUtils.toBooleanObject(y.getEnabled()), true))
				.filter(y -> StringUtils.isBlank(groupName) || Lists.newArrayList(Splitter.on(',')
					.trimResults()
					.omitEmptyStrings()
					.split(y.getGroup()))
					.contains(groupName)
				)
				.map(this::generateFromXML)
				.collect(Collectors.toList()))
			.orElse(new ArrayList<>());
	}

	private DesiredCapabilities generateFromXML(
		@NotNull final au.com.agic.apptesting.profiles.configuration.DesiredCapabilities desiredCapabilities) {
		checkNotNull(desiredCapabilities);

		final DesiredCapabilities seleniumDesiredCapabilities = new DesiredCapabilities();

		if (desiredCapabilities.getCapability() != null) {
			desiredCapabilities.getCapability().stream()
				.filter(e ->
					StringUtils.isNotBlank(e.getName()) && StringUtils.isNotBlank(e.getValue()))
				.forEach(e -> seleniumDesiredCapabilities.setCapability(e.getName(), e.getValue()));
		}

		return seleniumDesiredCapabilities;
	}
}
