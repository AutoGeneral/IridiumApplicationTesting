package au.com.agic.apptesting.profiles.configuration;

/**
 * This is used when deserialising from JSON with a wrapped root value.
 */
public class RootWrapper {

	private Configuration profile;

	public Configuration getProfile() {
		return profile;
	}

	public void setProfile(final Configuration profile) {
		this.profile = profile;
	}
}
