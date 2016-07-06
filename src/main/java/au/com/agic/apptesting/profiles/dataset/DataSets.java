package au.com.agic.apptesting.profiles.dataset;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Represents a {@code <dataSets>} element
 */
public class DataSets {

	private CommonDataSet commonDataSet = new CommonDataSet();
	private List<DataSet> dataSets = new ArrayList<>();

	public CommonDataSet getCommonDataSet() {
		return commonDataSet;
	}

	public void setCommonDataSet(final CommonDataSet commonDataSet) {
		this.commonDataSet = commonDataSet;
	}

	@XmlElement(name = "dataSet")
	public List<DataSet> getDataSets() {
		return dataSets;
	}

	public void setDataSets(final List<DataSet> dataSets) {
		this.dataSets = new ArrayList<>(dataSets);
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
	public List<Setting> getJsonSettings() {
		if (commonDataSet == null || commonDataSet.getSettings() == null) {
			return null;
		}

		return commonDataSet.getSettings();
	}

	public void setJsonSettings(final List<Setting> desiredCapabilities) {
		if (commonDataSet == null) {
			commonDataSet = new CommonDataSet();
		}

		commonDataSet.setSettings(desiredCapabilities);
	}
}
