package au.com.agic.apptesting.profiles.dataset;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Represents a {@code <setting>} element
 */
public class Setting {

	private String name;
	private String value;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@XmlValue
	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}
}
