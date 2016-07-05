package au.com.agic.apptesting.utils;

/**
 * Utilities for casting objects
 */
public class CastUtils {
	private CastUtils() {

	}

	public static <T> T as(final Class<T> type, final Object object) {
		return type.isInstance(object) ? type.cast(object) : null;
	}
}
