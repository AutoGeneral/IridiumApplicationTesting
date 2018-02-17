package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.SleepUtils;
import io.vavr.control.Try;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Implementation of the sleep service
 */
@Component
public class SleepUtilsImpl implements SleepUtils {
	@Override
	public void sleep(final long sleep) {
		checkArgument(sleep >= 0);

		Try.run(() -> Thread.sleep(sleep));
	}
}
