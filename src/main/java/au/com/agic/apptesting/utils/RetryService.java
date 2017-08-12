package au.com.agic.apptesting.utils;

import org.springframework.retry.support.RetryTemplate;

/**
 * A service for getting Spring Retry templates
 */
public interface RetryService {
	/**
	 * @return A Spring retry template preconfigured with standard options
	 */
	RetryTemplate getRetryTemplate();
}
