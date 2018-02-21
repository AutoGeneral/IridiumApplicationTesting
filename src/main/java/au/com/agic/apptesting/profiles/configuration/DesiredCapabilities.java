package au.com.agic.apptesting.profiles.configuration;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a {@code <desiredCapabilities>} element
 */
public class DesiredCapabilities {

	private List<Capability> capability = new ArrayList<>();
	private String enabled;
	private String group;

	@XmlElement(name = "capability")
	public List<Capability> getCapability() {
		return capability;
	}

	public void setCapability(final List<Capability> capability) {
		this.capability = new ArrayList<>(capability);
	}

	@XmlAttribute
	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(final String enabled) {
		this.enabled = enabled;
	}

	@XmlAttribute
	public String getGroup() {
		return group;
	}

	public void setGroup(final String group) {
		this.group = group;
	}
}
