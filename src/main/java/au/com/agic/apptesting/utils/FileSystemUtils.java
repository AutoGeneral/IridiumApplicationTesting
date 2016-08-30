package au.com.agic.apptesting.utils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import javax.validation.constraints.NotNull;

/**
 * Defines utility methods for working with local files and folders
 */
public interface FileSystemUtils {

	/**
	 * @return the location of a directory that should be used to hold the report files
	 */
	String buildReportDirectoryName();

	/**
	 * recursivly copies a directory embedded in the jar file to a location on the disk
	 * @param sourcePath The source directory
	 * @param target The target directory
	 * @throws IOException exception thrown when the file could not be copied
	 */
	void copyFromJar(final String sourcePath, final Path target) throws IOException;

	/**
	 * recursivly copies a directory embedded in the jar file to a location on the disk
	 * @param sourcePath The source directory
	 * @param target The target directory
	 * @throws IOException exception thrown when the file could not be copied
	 */
	void copyFromJar(@NotNull final URI sourcePath, @NotNull final Path target) throws IOException;
}
