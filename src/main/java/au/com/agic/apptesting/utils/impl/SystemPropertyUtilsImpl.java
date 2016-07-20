package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkArgument;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.SystemPropertyUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Implementation that deals with the restrictions imposed by web start
 */
public class SystemPropertyUtilsImpl implements SystemPropertyUtils {

	/**
	 * These are the prefixes that can be applied to any system property. This is mostly to
	 * facilitate web start, which has restrictions on system properties. See
	 * http://stackoverflow.com/questions/19400725/with-java-7-update-45-the-system-properties-no-longer-set-from-jnlp-tag-proper
	 * for details.
	 */
	private static final List<String> SYSTEM_PROPERTY_PREFIXES = Arrays.asList("", "jnlp.", "javaws.");

	@Override
	public String getProperty(final String name) {
		checkArgument(StringUtils.isNotBlank(name));

		return SYSTEM_PROPERTY_PREFIXES.stream()
			.map(e -> System.getProperty(e + name))
			.filter(Objects::nonNull)
			.findFirst()
			.orElse(null);
	}

	@Override
	public String getPropertyEmptyAsNull(String name) {
		checkArgument(StringUtils.isNotBlank(name));

		return SYSTEM_PROPERTY_PREFIXES.stream()
			.map(e -> System.getProperty(e + name))
			.filter(StringUtils::isNotBlank)
			.findFirst()
			.orElse(null);
	}

	@Override
	public void copyVariableToDefaultLocation(final String name) {

		SYSTEM_PROPERTY_PREFIXES.stream()
			.map(e -> System.getProperty(e + name))
			.filter(StringUtils::isNotBlank)
			.findFirst()
			.ifPresent(e -> System.setProperty(name, e));
	}

	@Override
	public void copyDependentSystemProperties() {
		copyVariableToDefaultLocation(Constants.CHROME_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY);
		copyVariableToDefaultLocation(Constants.OPERA_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY);
		copyVariableToDefaultLocation(Constants.PHANTOM_JS_BINARY_PATH_SYSTEM_PROPERTY);
		copyVariableToDefaultLocation(Constants.IE_WEB_DRIVER_LOCATION_SYSTEM_PROPERTY);
		copyVariableToDefaultLocation(Constants.FIREFOX_PROFILE_SYSTEM_PROPERTY);
	}
}
