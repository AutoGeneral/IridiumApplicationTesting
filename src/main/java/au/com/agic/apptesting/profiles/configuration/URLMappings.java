package au.com.agic.apptesting.profiles.configuration;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of URL Mappings
 */
public class URLMappings {

	private List<FeatureGroup> featureGroups = new ArrayList<>();

	@XmlElement(name = "featureGroup")
	public List<FeatureGroup> getFeatureGroups() {
		return featureGroups;
	}

	public void setFeatureGroups(final List<FeatureGroup> featureGroups) {
		this.featureGroups = new ArrayList<>(featureGroups);
	}
}
