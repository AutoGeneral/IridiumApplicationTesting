package au.com.agic.apptesting.junit;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a test suite from a jUnit result xml
 */
@XmlRootElement
public class Testsuite {

	private Integer failures = 0;

	private Float time = 0.0f;

	private Integer skipped = 0;

	private Integer tests = 0;

	private String name;

	private List<Testcase> testcase = new ArrayList<>();

	@XmlAttribute
	public Integer getFailures() {
		return failures;
	}

	public void setFailures(final Integer failures) {
		this.failures = failures;
	}

	@XmlAttribute
	public Float getTime() {
		return time;
	}

	public void setTime(final Float time) {
		this.time = time;
	}

	@XmlAttribute
	public Integer getSkipped() {
		return skipped;
	}

	public void setSkipped(final Integer skipped) {
		this.skipped = skipped;
	}

	@XmlAttribute
	public Integer getTests() {
		return tests;
	}

	public void setTests(final Integer tests) {
		this.tests = tests;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public List<Testcase> getTestcase() {
		return testcase;
	}

	public void setTestcase(final List<Testcase> testcase) {
		this.testcase = new ArrayList<>(testcase);
	}
}
