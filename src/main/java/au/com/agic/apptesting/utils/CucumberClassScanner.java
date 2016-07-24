package au.com.agic.apptesting.utils;

import java.util.Set;

/**
 * A service for finding glue classes to be used by Cucumber
 */
public interface CucumberClassScanner {
	/**
	 *
	 * @return packages that contain classes with methods annotated with cucumber
	 * annotations like When, And, Then, But, Given
     */
	Set<String> getClassesContainingCucumberAnnotations();
}
