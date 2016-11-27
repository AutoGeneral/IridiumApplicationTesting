package au.com.agic.apptesting.utils;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * When a JAR is run from WebStart, you don't get to know the local filename of the JAR. This is a problem for
 * applications like Cucumber and ZAP, both of which scan a JAR file for classes and other resources.
 *
 * This service downloads the JAR file and sets a system property value to let other libraries know
 * where the JAR file is. Both ZAP and Cucumber have been patched to look for the file downloaded
 * by this service when they can't find their local files.
 *
 * See http://bugs.java.com/bugdatabase/view_bug.do?bug_id=5025254
 *
 * See https://github.com/mcasperson/zaproxy and https://github.com/mcasperson/cucumber-jvm for the
 * customisations that were made to ZAP and Cucumber
 */
public interface JarDownloader {

	/**
	 * This system property will hold the absolute path of the local JAR download if it was needed.
	 */
	String LOCAL_JAR_FILE_SYSTEM_PROPERTY = "LocalIridiumJARFile";

	/**
	 * Download a local copy of the JAR file to a temporary location so libraries like
	 * ZAP and Cucumber can inspect it.
	 * @param tempFiles A list of temporary files to be cleaned up when the app finishes
	 */
	void downloadJar(@NotNull List<File> tempFiles);
}
