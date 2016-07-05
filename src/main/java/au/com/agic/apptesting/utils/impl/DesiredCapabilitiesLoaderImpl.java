package au.com.agic.apptesting.utils.impl;


import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.profiles.FileProfileAccess;
import au.com.agic.apptesting.profiles.configuration.Configuration;
import au.com.agic.apptesting.utils.DesiredCapabilitiesLoader;
import au.com.agic.apptesting.utils.SystemPropertyUtils;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

/**
 * Provides a service for loading desired capability profiles
 */
public class DesiredCapabilitiesLoaderImpl implements DesiredCapabilitiesLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(DesiredCapabilitiesLoaderImpl.class);
	private static final SystemPropertyUtils SYSTEM_PROPERTY_UTILS = new SystemPropertyUtilsImpl();

	private static final FileProfileAccess<Configuration> PROFILE_ACCESS = new FileProfileAccess<>(
		SYSTEM_PROPERTY_UTILS.getProperty(Constants.CONFIGURATION),
		Configuration.class);

	@NotNull
	@Override
	public List<DesiredCapabilities> getCapabilities() {

		final Optional<Configuration> configuration = PROFILE_ACCESS.getProfile();

		if (configuration != null
			&& configuration.isPresent()
			&& configuration.get().getSettings() != null
			&& configuration.get().getSettings().getDesiredCapabilities() != null) {

			return configuration.get().getSettings().getDesiredCapabilities().stream()
				.map(this::generateFromXML)
				.collect(Collectors.toList());
		}

		return new ArrayList<>();
	}

	private DesiredCapabilities generateFromXML(
		@NotNull final au.com.agic.apptesting.profiles.configuration.DesiredCapabilities desiredCapabilities) {
		checkNotNull(desiredCapabilities);

		final DesiredCapabilities seleniumDesiredCapabilities = new DesiredCapabilities();

		if (desiredCapabilities.getCapability() != null) {
			desiredCapabilities.getCapability().stream()
				.filter(e -> StringUtils.isNotBlank(e.getName()) && StringUtils.isNotBlank(e.getValue()))
				.forEach(e -> seleniumDesiredCapabilities.setCapability(e.getName(), e.getValue()));
		}

		return seleniumDesiredCapabilities;
	}
}
