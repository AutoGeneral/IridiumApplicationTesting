package au.com.agic.apptesting.profiles.dataset;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * Represents a {@code <commonDataSet>} element
 */
public class CommonDataSet {

	private List<Setting> settings = new ArrayList<>();

	@XmlElement(name = "setting")
	public List<Setting> getSettings() {
		return settings;
	}

	public void setSettings(final List<Setting> settings) {
		this.settings = new ArrayList<>(settings);
	}
}
