package au.com.agic.apptesting.utils;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * A service that merges individual junit reports
 */
public interface JUnitReportMerge {

	/**
	 * @param reports A collection if junit reports
	 * @return A single, merged junit report
	 */
	Optional<String> mergeReports(@NotNull List<String> reports);
}
