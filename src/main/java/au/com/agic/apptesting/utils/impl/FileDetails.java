package au.com.agic.apptesting.utils.impl;

import java.io.File;

/**
 * Details of a file that may have been originally sourced from a URL. We will often
 * process the file differently if it is a copy of a remote file.
 */
public class FileDetails {
	private final File file;
	private final boolean localSource;

	public FileDetails(final File file, final boolean localSource) {
		this.file = file;
		this.localSource = localSource;
	}

	/**
	 *
	 * @return The file that this object represents
	 */
	public File getFile() {
		return file;
	}

	/**
	 *
	 * @return true if this file was sourced from a local file, and false if it is a copy of
	 * a remote file downloaded from a URL.
	 */
	public boolean isLocalSource() {
		return localSource;
	}
}
