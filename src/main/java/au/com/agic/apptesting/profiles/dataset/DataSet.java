package au.com.agic.apptesting.profiles.dataset;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a {@code <dataset>} element
 */
public class DataSet {

	private List<Setting> settings = new ArrayList<>();

	@XmlElement(name = "setting")
	public List<Setting> getSettings() {
		return settings;
	}

	public void setSettings(final List<Setting> settings) {
		this.settings = new ArrayList<>(settings);
	}
}
