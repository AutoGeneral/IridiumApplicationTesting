package au.com.agic.apptesting.webdriver;

import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.*;

import java.util.concurrent.TimeUnit;

/**
 * A wait class that works with units other than seconds
 */
public class WebDriverWaitEx extends FluentWait<WebDriver> {
	public final static long DEFAULT_SLEEP_TIMEOUT = 500;
	private final WebDriver driver;

	public WebDriverWaitEx(WebDriver driver, long timeOutInSeconds) {
		this(driver, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeOutInSeconds, DEFAULT_SLEEP_TIMEOUT);
	}

	public WebDriverWaitEx(WebDriver driver, long timeOut, TimeUnit time) {
		this(driver, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeOut, DEFAULT_SLEEP_TIMEOUT, time);
	}

	public WebDriverWaitEx(WebDriver driver, long timeOutInSeconds, long sleepInMillis) {
		this(driver, new SystemClock(), Sleeper.SYSTEM_SLEEPER, timeOutInSeconds, sleepInMillis);
	}

	public WebDriverWaitEx(WebDriver driver, Clock clock, Sleeper sleeper, long timeOutInSeconds, long sleepTimeOut) {
		this(driver, clock, sleeper, timeOutInSeconds, sleepTimeOut, TimeUnit.SECONDS);
	}

	public WebDriverWaitEx(WebDriver driver, Clock clock, Sleeper sleeper, long timeOutInSeconds, long sleepTimeOut, TimeUnit time) {
		super(driver, clock, sleeper);
		withTimeout(timeOutInSeconds, time);
		pollingEvery(sleepTimeOut, TimeUnit.MILLISECONDS);
		ignoring(NotFoundException.class);
		this.driver = driver;
	}

	@Override
	protected RuntimeException timeoutException(String message, Throwable lastException) {
		TimeoutException ex = new TimeoutException(message, lastException);
		ex.addInfo(WebDriverException.DRIVER_INFO, driver.getClass().getName());
		if (driver instanceof RemoteWebDriver) {
			RemoteWebDriver remote = (RemoteWebDriver) driver;
			if (remote.getSessionId() != null) {
				ex.addInfo(WebDriverException.SESSION_ID, remote.getSessionId().toString());
			}
			if (remote.getCapabilities() != null) {
				ex.addInfo("Capabilities", remote.getCapabilities().toString());
			}
		}
		throw ex;
	}
}
