package au.com.agic.apptesting.junit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Represents the failure element
 */
public class Failure {

	private String message;
	private String content;

	@XmlAttribute
	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

	@XmlValue
	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}
}
