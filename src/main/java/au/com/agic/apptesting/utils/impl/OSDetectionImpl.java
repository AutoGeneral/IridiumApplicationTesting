package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.OSDetection;
import org.springframework.stereotype.Component;

/**
 * Created by mcasperson on 1/12/16.
 */
@Component
public class OSDetectionImpl implements OSDetection {

	/**
	 * http://stackoverflow.com/questions/1856565/how-do-you-determine-32-or-64-bit-architecture-of-windows-using-java
	 */
	@Override
	public boolean is64BitOS() {
		if (System.getProperty("os.name").contains("Windows")) {
			return (System.getenv("ProgramFiles(x86)") != null);
		} else {
			return (System.getProperty("os.arch").indexOf("64") != -1);
		}
	}
}
