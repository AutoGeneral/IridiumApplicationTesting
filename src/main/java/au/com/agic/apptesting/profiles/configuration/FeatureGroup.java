package au.com.agic.apptesting.profiles.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Represents a {@code <featureGroup>} element
 */
public class FeatureGroup {

	private String name;
	private List<UrlMapping> urlMappings = new ArrayList<>();
	private String enabled;
	private String featureGroup;
	private String group;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@XmlElement(name = "urlMapping")
	public List<UrlMapping> getUrlMappings() {
		return urlMappings;
	}

	public void setUrlMappings(final List<UrlMapping> urlMappings) {
		this.urlMappings = new ArrayList<>(urlMappings);
	}

	@XmlAttribute
	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(final String enabled) {
		this.enabled = enabled;
	}

	@XmlAttribute
	public String getFeatureGroup() {
		return featureGroup;
	}

	public void setFeatureGroup(final String featureGroup) {
		this.featureGroup = featureGroup;
	}

	@XmlAttribute
	public String getGroup() {
		return group;
	}

	public void setGroup(final String group) {
		this.group = group;
	}
}
