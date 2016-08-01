package au.com.agic.apptesting.junit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Represents a jUnit test case
 */
public class Testcase {

	private String systemOut;

	private Float time = 0.0f;

	private String classname;

	private String name;

	private Failure failure;

	@XmlElement(name = "system-out")
	public String getSystemOut() {
		return systemOut;
	}

	public void setSystemOut(final String systemOut) {
		this.systemOut = systemOut;
	}

	@XmlAttribute
	public Float getTime() {
		return time;
	}

	public void setTime(final Float time) {
		this.time = time;
	}

	@XmlAttribute
	public String getClassname() {
		return classname;
	}

	public void setClassname(final String classname) {
		this.classname = classname;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Failure getFailure() {
		return failure;
	}

	public void setFailure(final Failure failure) {
		this.failure = failure;
	}
}
