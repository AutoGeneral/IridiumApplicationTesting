package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.SleepUtils;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Implementation of the sleep service
 */
public class SleepUtilsImpl implements SleepUtils {
	@Override
	public void sleep(final long sleep) {
		checkArgument(sleep >= 0);

		try {
			Thread.sleep(sleep);
		} catch (final InterruptedException ignored) {
			/*
				We don't actually care about this exception
			 */
		}
	}
}
