package au.com.agic.apptesting.profiles.configuration;

/**
 * Represents a browserstack element
 */
public class BrowserStack {

	private String accessToken;
	private String username;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(final String accessToken) {
		this.accessToken = accessToken;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}
}
