package au.com.agic.apptesting.profiles.configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * Represents a {@code <profile>} element
 */
@XmlRootElement(name = "profile")
public class Configuration {

	private URLMappings urlMappings;
	private BrowserStack browserstack = new BrowserStack();

	private Settings settings;

	public BrowserStack getBrowserstack() {
		return browserstack;
	}

	public void setBrowserstack(final BrowserStack browserstack) {
		this.browserstack = browserstack;
	}

	@XmlElement
	public URLMappings getUrlMappings() {
		return urlMappings;
	}

	public void setUrlMappings(final URLMappings urlMappings) {
		this.urlMappings = urlMappings;
	}

	@XmlElement
	public Settings getSettings() {
		return settings;
	}

	public void setSettings(final Settings settings) {
		this.settings = settings;
	}

	/**
	 * The valid commands takes a bit of juggling between the XML and the JSON. In XML,
	 * validCommands is an object that contains a list. In JSON, validCommands is a list.
	 *
	 * Here we accept the JSON list and put it into the ValidCommands object
	 *
	 * @return A collection used when parsing a JSON representation of the data stucture
	 */
	@XmlTransient
	public List<DesiredCapabilities> getJsonSettings() {
		if (settings == null || settings.getDesiredCapabilities() == null) {
			return null;
		}

		return settings.getDesiredCapabilities();
	}

	public void setJsonSettings(final List<DesiredCapabilities> desiredCapabilities) {
		if (settings == null) {
			settings = new Settings();
		}

		settings.setDesiredCapabilities(desiredCapabilities);
	}

	@XmlTransient
	public List<FeatureGroup> getJsonUrlMappings() {
		if (urlMappings == null || urlMappings.getFeatureGroups() == null) {
			return null;
		}

		return urlMappings.getFeatureGroups();
	}

	public void setJsonUrlMappings(final List<FeatureGroup> myUrlMappings) {
		if (urlMappings == null) {
			urlMappings = new URLMappings();
		}

		urlMappings.setFeatureGroups(myUrlMappings);
	}
}
