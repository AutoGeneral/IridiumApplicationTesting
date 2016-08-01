package au.com.agic.apptesting.profiles.dataset;

/**
 * This is used when deserialising from JSON with a wrapped root value.
 */
public class DatasetsRootWrapper {

	private DatasetsRootElement profile;

	public DatasetsRootElement getProfile() {
		return profile;
	}

	public void setProfile(final DatasetsRootElement profile) {
		this.profile = profile;
	}
}
