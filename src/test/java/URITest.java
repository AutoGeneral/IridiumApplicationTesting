import au.com.agic.apptesting.utils.impl.FileSystemUtilsImpl;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDateTime;


/**
 * Tests of the file hanlding libraries
 */
public class URITest {

	private static final FileSystemUtilsImpl FILE_SYSTEM_UTILS = new FileSystemUtilsImpl();

	/**
	 * Make sure we can download a JAR file when ZAP attempts to find a folder from the uberjar
	 * @throws IOException
	 */
	@Test
	public void testFileAccess() throws IOException {
		final URI uri = URI.create("jar:http://bamboodev/webapptesting/webstart/webapptesting-signed.jar!/zap");
		String tmpFile = "/tmp/" + LocalDateTime.now() + ".jar";
		FILE_SYSTEM_UTILS.copyFromJar(uri, Paths.get(tmpFile));
		Assert.assertTrue(new File(tmpFile).exists());
	}


}
