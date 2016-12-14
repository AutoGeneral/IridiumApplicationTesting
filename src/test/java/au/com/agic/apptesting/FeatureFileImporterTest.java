package au.com.agic.apptesting;

import au.com.agic.apptesting.utils.impl.FeatureFileImporterImpl;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by mcasperson on 14/12/16.
 */
public class FeatureFileImporterTest {

	private static final String FEATURE_FILE =
		"Feature: Test of the steps provided by Iridium\n"
			+ "Some text under the feature file\n"
			+ "\n"
			+ "  @test\n"
			+ "  Scenario: Open App\n"
			+ "    # Load the page from the appUrlOverride value\n"
			+ "    Given I open the application";

	private static final String FEATURE_FILE_2 =
		"@tag\n"
			+ "Feature: Test of the steps provided by Iridium\n"
			+ "Some text under the feature file\n"
			+ "\n"
			+ "  @test\n"
			+ "  Scenario: Open App\n"
			+ "    # Load the page from the appUrlOverride value\n"
			+ "    Given I open the application";

	private static final String FEATURE_FILE_STRIPPED =
		"  @test\n"
			+ "  Scenario: Open App\n"
			+ "    # Load the page from the appUrlOverride value\n"
			+ "    Given I open the application";

	private static final String FEATURE_FILE_3 =
		"@tag\n"
			+ "Feature: Test of the steps provided by Iridium\n"
			+ "Some text under the feature file\n"
			+ "\n"
			+ "  Scenario: Open App\n"
			+ "    # Load the page from the appUrlOverride value\n"
			+ "    Given I open the application";

	private static final String FEATURE_FILE_STRIPPED_3 =
		"  Scenario: Open App\n"
			+ "    # Load the page from the appUrlOverride value\n"
			+ "    Given I open the application";

	private static final String FEATURE_FILE_4 =
		"@tag\n"
			+ "Feature: Test of the steps provided by Iridium\n"
			+ "Some text under the feature file\n"
			+ "\n"
			+ "  Scenario Outline: Open App\n"
			+ "    # Load the page from the appUrlOverride value\n"
			+ "    Given I open the application";

	private static final String FEATURE_FILE_STRIPPED_4 =
		"  Scenario Outline: Open App\n"
			+ "    # Load the page from the appUrlOverride value\n"
			+ "    Given I open the application";

	private static final String FEATURE_FILE_5 =
		"@tag\n"
			+ "Feature: Test of the steps provided by Iridium\n"
			+ "Some text under the feature file\n"
			+ "\n"
			+ "  Background: Open App\n"
			+ "    # Load the page from the appUrlOverride value\n"
			+ "    Given I open the application";

	private static final String FEATURE_FILE_STRIPPED_5 =
		"  Background: Open App\n"
			+ "    # Load the page from the appUrlOverride value\n"
			+ "    Given I open the application";

	@Test
	public void testClearToScenario() {
		final FeatureFileImporterImpl featureFileImporter = new FeatureFileImporterImpl();
		final String clearedFile = featureFileImporter.clearContentToFirstScenario(FEATURE_FILE);

		Assert.assertEquals(FEATURE_FILE_STRIPPED, clearedFile);
	}

	@Test
	public void testClearToScenario2() {
		final FeatureFileImporterImpl featureFileImporter = new FeatureFileImporterImpl();
		final String clearedFile = featureFileImporter.clearContentToFirstScenario(FEATURE_FILE_2);

		Assert.assertEquals(FEATURE_FILE_STRIPPED, clearedFile);
	}

	@Test
	public void testClearToScenario3() {
		final FeatureFileImporterImpl featureFileImporter = new FeatureFileImporterImpl();
		final String clearedFile = featureFileImporter.clearContentToFirstScenario(FEATURE_FILE_3);

		Assert.assertEquals(FEATURE_FILE_STRIPPED_3, clearedFile);
	}

	@Test
	public void testClearToScenario4() {
		final FeatureFileImporterImpl featureFileImporter = new FeatureFileImporterImpl();
		final String clearedFile = featureFileImporter.clearContentToFirstScenario(FEATURE_FILE_4);

		Assert.assertEquals(FEATURE_FILE_STRIPPED_4, clearedFile);
	}

	@Test
	public void testClearToScenario5() {
		final FeatureFileImporterImpl featureFileImporter = new FeatureFileImporterImpl();
		final String clearedFile = featureFileImporter.clearContentToFirstScenario(FEATURE_FILE_5);

		Assert.assertEquals(FEATURE_FILE_STRIPPED_5, clearedFile);
	}
}
