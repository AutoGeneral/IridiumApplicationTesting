package au.com.agic.apptesting.aspects;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.utils.FeatureState;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Contains aspects that will modify how steps are run
 */
@Aspect
@Component
public class EarlyExitAspect {
	/**
	 * This aspect will return from any step once an early exit has been flagged
	 * @param joinPoint AspectJ pointcut details
	 * @throws Throwable Any exception thrown by the wrapped method call
	 */
	@Around("execution(public void au.com.agic.apptesting.steps..*(..))")
	public void aroundStep(final ProceedingJoinPoint joinPoint) throws Throwable {
		final FeatureState featureState =
			State.getThreadDesiredCapabilityMap().getDesiredCapabilitiesForThread();

		if (!featureState.getSkipSteps()) {
			joinPoint.proceed();
		}
	}
}
