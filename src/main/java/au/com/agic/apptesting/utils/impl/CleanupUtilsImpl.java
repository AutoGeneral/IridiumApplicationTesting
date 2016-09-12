package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.CleanupUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Iterator;

/**
 * An implementation of the cleanup service
 */
public class CleanupUtilsImpl implements CleanupUtils {

	private static final String[] CLEANUP_EXTENSIONS = {"xml", "txt"};

	@Override
	public void cleanupOldReports() {
		final Iterator<File> iterator =
			FileUtils.iterateFiles(new File("."), CLEANUP_EXTENSIONS, false);
		while (iterator.hasNext()) {
			final File file = iterator.next();
			file.delete();
		}
	}
}
