package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.RetryService;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component
public class RetryServiceImpl implements RetryService {
	@Override
	public RetryTemplate getRetryTemplate() {
		final SimpleRetryPolicy policy = new SimpleRetryPolicy();
		policy.setMaxAttempts(Constants.WEBDRIVER_ACTION_RETRIES);

		final FixedBackOffPolicy backoff = new FixedBackOffPolicy();
		backoff.setBackOffPeriod(2000l);

		final RetryTemplate template = new RetryTemplate();
		template.setRetryPolicy(policy);
		template.setBackOffPolicy(backoff);
		return template;
	}
}
