package au.com.agic.apptesting.utils;

/**
 * Utilities to return useful information about the OS
 */
public interface OSDetection {

	/**
	 *
	 * @return true if this is a 64 bit OS, and false otherwise
	 */
	boolean is64BitOS();
}
