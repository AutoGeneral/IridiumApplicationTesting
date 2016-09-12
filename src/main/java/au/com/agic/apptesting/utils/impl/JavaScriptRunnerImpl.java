package au.com.agic.apptesting.utils.impl;

import au.com.agic.apptesting.utils.JavaScriptRunner;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * An implementation of the JavaScript runner service
 */
@Component
public class JavaScriptRunnerImpl implements JavaScriptRunner {

	@Override
	public void interactHiddenElementMouseEvent(
		@NotNull final WebElement element,
		@NotNull final String event,
		@NotNull final JavascriptExecutor js) {
		/*
			PhantomJS doesn't support the click method, so "element.click()" won't work
			here. We need to dispatch the event instead.
		 */
		js.executeScript("var ev = document.createEvent('MouseEvent');"
			+ "    ev.initMouseEvent("
			+ "        '" + event + "',"
			+ "        true /* bubble */, true /* cancelable */,"
			+ "        window, null,"
			+ "        0, 0, 0, 0, /* coordinates */"
			+ "        false, false, false, false, /* modifier keys */"
			+ "        0 /*left*/, null"
			+ "    );"
			+ "    arguments[0].dispatchEvent(ev);", element);
	}

	@Override
	public void interactHiddenElementKeyEvent(
		@NotNull final WebElement element,
		@NotNull final String event,
		@NotNull final JavascriptExecutor js) {
		/*
			PhantomJS doesn't support the click method, so "element.click()" won't work
			here. We need to dispatch the event instead.
		 */
		js.executeScript("var ev = document.createEvent('KeyboardEvent');"
			+ "    (ev.initKeyEvent || ev.initKeyboardEvent).call(ev,"
			+ "        '" + event + "',"
			+ "        true /* bubble */, true /* cancelable */,"
			+ "        window, null,"
			+ "        0, 0, 0, 0, /* coordinates */"
			+ "        false, false, false, false, /* modifier keys */"
			+ "        0 /*left*/, null"
			+ "    );"
			+ "    arguments[0].dispatchEvent(ev);", element);
	}
}
