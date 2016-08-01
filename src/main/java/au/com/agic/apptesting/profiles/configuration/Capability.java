package au.com.agic.apptesting.profiles.configuration;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Represents a {@code <capability>} element
 */
public class Capability {

	private String name;
	private String value;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}
}
