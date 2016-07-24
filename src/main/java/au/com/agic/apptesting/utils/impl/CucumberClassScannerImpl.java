package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.CucumberClassScanner;
import cucumber.api.java.en.*;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A scanner based on the reflections library
 */
public class CucumberClassScannerImpl implements CucumberClassScanner {
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

		return methods.stream()
			.map(x -> x.getDeclaringClass().getPackage().getName())
			.collect(Collectors.toSet());
	}
}
