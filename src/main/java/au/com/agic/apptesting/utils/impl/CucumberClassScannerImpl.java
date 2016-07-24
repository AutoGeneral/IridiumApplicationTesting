package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.CucumberClassScanner;
import cucumber.api.java.en.*;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A scanner based on the reflections library
 */
public class CucumberClassScannerImpl implements CucumberClassScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(CucumberClassScannerImpl.class);

	@Override
	public Set<String> getClassesContainingCucumberAnnotations() {
		final Reflections reflections = new Reflections(
			new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forJavaClassPath())
				.setScanners(new MethodAnnotationsScanner())
			);

		final Set<Method> methods = reflections.getMethodsAnnotatedWith(When.class);
		methods.addAll(reflections.getMethodsAnnotatedWith(And.class));
		methods.addAll(reflections.getMethodsAnnotatedWith(Then.class));
		methods.addAll(reflections.getMethodsAnnotatedWith(Given.class));
		methods.addAll(reflections.getMethodsAnnotatedWith(But.class));

		final Set<String> packages = methods.stream()
			.map(x -> x.getDeclaringClass().getPackage().getName())
			.collect(Collectors.toSet());

		packages.stream()
			.forEach(x -> LOGGER.info("WEBAPPTESTER-INFO-0006: Found package with Cucumber steps {}", x));

		return packages;
	}
}
