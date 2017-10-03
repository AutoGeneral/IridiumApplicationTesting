package au.com.agic.apptesting.drivers;

import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;

import java.lang.reflect.Field;
import java.util.Map;

import static org.openqa.selenium.remote.CapabilityType.SUPPORTS_JAVASCRIPT;

/**
 * This driver provides a work around for the bug at
 * https://github.com/SeleniumHQ/selenium/issues/4781
 */
public class PhantomJSFixedDriver extends PhantomJSDriver {
	public PhantomJSFixedDriver(Capabilities desiredCapabilities) {
		super(desiredCapabilities);
	}

	protected void startSession(Capabilities desiredCapabilities) {
		Map<String, ?> parameters = ImmutableMap.of("desiredCapabilities", desiredCapabilities);

		Response response = execute(DriverCommand.NEW_SESSION, parameters);

		Map<String, Object> rawCapabilities = (Map<String, Object>) response.getValue();
		MutableCapabilities returnedCapabilities = new MutableCapabilities();
		for (Map.Entry<String, Object> entry : rawCapabilities.entrySet()) {
			// Handle the platform later
			if (CapabilityType.PLATFORM.equals(entry.getKey()) || "platformName".equals(entry.getKey())) {
				continue;
			}
			returnedCapabilities.setCapability(entry.getKey(), entry.getValue());
		}
		String platformString = (String) rawCapabilities.getOrDefault(CapabilityType.PLATFORM, rawCapabilities.get("platformName"));
		Platform platform;
		try {
			if (platformString == null || "".equals(platformString)) {
				platform = Platform.ANY;
			} else {
				platform = Platform.fromString(platformString);
			}
		} catch (WebDriverException e) {
			/*
			 	Phantom JS returned a platform string that is not recognised. Try splitting
			 	the string to get the first part of the platform, which is the OS name.
			  */
			try {
				platform = Platform.fromString(platformString.split("-")[0]);
			} catch (IllegalArgumentException e2) {
				// The server probably responded with a name matching the os.name
				// system property. Try to recover and parse this.
				platform = Platform.extractFromSysProperty(platformString);
			}
		}
		returnedCapabilities.setCapability(CapabilityType.PLATFORM, platform);
		returnedCapabilities.setCapability("platformName", platform);

		if (rawCapabilities.containsKey(SUPPORTS_JAVASCRIPT)) {
			Object raw = rawCapabilities.get(SUPPORTS_JAVASCRIPT);
			if (raw instanceof String) {
				returnedCapabilities.setCapability(SUPPORTS_JAVASCRIPT, Boolean.parseBoolean((String) raw));
			} else if (raw instanceof Boolean) {
				returnedCapabilities.setCapability(SUPPORTS_JAVASCRIPT, ((Boolean) raw).booleanValue());
			}
		} else {
			returnedCapabilities.setCapability(SUPPORTS_JAVASCRIPT, true);
		}

		try {
			Field field = RemoteWebDriver.class.getDeclaredField("capabilities");
			field.setAccessible(true);
			field.set(this, returnedCapabilities);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}

		setSessionId(response.getSessionId());
	}
}
