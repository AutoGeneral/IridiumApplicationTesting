package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.junit.Testsuite;
import au.com.agic.apptesting.utils.JUnitReportMerge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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

			for (final String report : reports) {
				final Testsuite testSuite = (Testsuite) jaxbUnmarshaller.unmarshal(new File(report));
				masterTestSuite.setFailures(masterTestSuite.getFailures() + testSuite.getFailures());
				masterTestSuite.setSkipped(masterTestSuite.getSkipped() + testSuite.getSkipped());
				masterTestSuite.setTests(masterTestSuite.getTests() + testSuite.getTests());
				masterTestSuite.setTime(masterTestSuite.getTime() + testSuite.getTime());
				masterTestSuite.getTestcase().addAll(testSuite.getTestcase());
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
