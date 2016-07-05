package au.com.agic.apptesting.profiles.dataset;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a {@code <profile>} element
 */
@XmlRootElement(name = "profile")
public class DatasetsRootElement {

	private DataSets dataSets = new DataSets();

	public DataSets getDataSets() {
		return dataSets;
	}

	public void setDataSets(final DataSets dataSets) {
		this.dataSets = dataSets;
	}
}
