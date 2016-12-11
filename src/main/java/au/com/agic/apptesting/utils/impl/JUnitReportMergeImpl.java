package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.junit.Testcase;
import au.com.agic.apptesting.junit.Testsuite;
import au.com.agic.apptesting.utils.JUnitReportMerge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.xml.bind.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * An implementation of JUnitReportMerge that uses JAXB to merge reports
 */
public class JUnitReportMergeImpl implements JUnitReportMerge {

	private static final Logger LOGGER = LoggerFactory.getLogger(JUnitReportMergeImpl.class);

	@Override
	public Optional<String> mergeReports(@NotNull final List<String> reports) {
		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(Testsuite.class);
			final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// The master test suite
			final Testsuite masterTestSuite = new Testsuite();

			int index = 1;
			for (final String report : reports) {
				try {
					final Testsuite testSuite = (Testsuite) jaxbUnmarshaller.unmarshal(new File(report));
					masterTestSuite.setFailures(masterTestSuite.getFailures() + testSuite.getFailures());
					masterTestSuite.setSkipped(masterTestSuite.getSkipped() + testSuite.getSkipped());
					masterTestSuite.setTests(masterTestSuite.getTests() + testSuite.getTests());
					masterTestSuite.setTime(masterTestSuite.getTime() + testSuite.getTime());

					/*
						Bamboo requires that test names be unique, so we prefix each with
						an index.
						https://answers.atlassian.com/questions/192018/bamboo-junit-parser-with-duplicate-testcase-names
					 */
					for (final Testcase testCase : testSuite.getTestcase()) {
						testCase.setName(index + ": " + testCase.getName());
					}

					masterTestSuite.getTestcase().addAll(testSuite.getTestcase());

					++index;
				} catch (final UnmarshalException ignored) {
					/*
						This will mean that an XML file that was not a jUnit report was parsed. We
						ignore these files.
					 */
				}
			}

			final ByteArrayOutputStream output = new ByteArrayOutputStream();
			jaxbMarshaller.marshal(masterTestSuite, output);
			return Optional.of(output.toString());
		} catch (final JAXBException ex) {
			LOGGER.error("There was an exception thrown while merging the jUnit XML "
				+ "report files. No merged result will be available.", ex);
		}

		/*
			A failure means we save an empty file.
		 */
		return Optional.empty();
	}
}
