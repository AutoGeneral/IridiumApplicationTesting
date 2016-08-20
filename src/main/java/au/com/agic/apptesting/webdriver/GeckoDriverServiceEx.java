package au.com.agic.apptesting.webdriver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.firefox.internal.Executable;
import org.openqa.selenium.remote.service.DriverService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * An extension of org.openqa.selenium.firefox.GeckoDriverService fixed to deal
 * with the bug https://github.com/mozilla/geckodriver/issues/158
 * <p>
 * Manages the life and death of an GeckoDriver aka 'wires'.
 */
public class GeckoDriverServiceEx extends GeckoDriverService {

	/**
	 * @param executable  The GeckoDriver executable.
	 * @param port        Which port to start the GeckoDriver on.
	 * @param args        The arguments to the launched server.
	 * @param environment The environment for the launched server.
	 * @throws IOException If an I/O error occurs.
	 */
	public GeckoDriverServiceEx(File executable, int port, ImmutableList<String> args,
								ImmutableMap<String, String> environment) throws IOException {
		super(executable, port, args, environment);
	}

	/**
	 * Configures and returns a new {@link GeckoDriverServiceEx} using the default configuration. In
	 * this configuration, the service will use the GeckoDriver executable identified by the
	 * {@link #GECKO_DRIVER_EXE_PROPERTY} system property. Each service created by this method will
	 * be configured to use a free port on the current system.
	 *
	 * @return A new GeckoDriverService using the default configuration.
	 */
	public static GeckoDriverServiceEx createDefaultService() {
		return new Builder().usingAnyFreePort().build();
	}

	/**
	 * Builder used to configure new {@link GeckoDriverServiceEx} instances.
	 */
	public static class Builder extends DriverService.Builder<
		GeckoDriverServiceEx, GeckoDriverServiceEx.Builder> {

		@Override
		protected File findDefaultExecutable() {
			return findExecutable("wires", GECKO_DRIVER_EXE_PROPERTY,
				"https://github.com/jgraham/wires",
				"https://github.com/jgraham/wires");
		}

		@Override
		protected ImmutableList<String> createArgs() {
			final ImmutableList.Builder<String> argsBuilder = ImmutableList.builder();
			/*
				This is changed from --webdriver-port
			 */
			argsBuilder.add(String.format("--port=%d", getPort()));
			if (getLogFile() != null) {
				argsBuilder.add(String.format("--log-file=\"%s\"", getLogFile().getAbsolutePath()));
			}
			argsBuilder.add("-b");
			argsBuilder.add(new Executable(null).getPath());
			return argsBuilder.build();
		}

		@Override
		protected GeckoDriverServiceEx createDriverService(
				final File exe,
				final int port,
				final ImmutableList<String> args,
				final ImmutableMap<String, String> environment) {

			try {
				return new GeckoDriverServiceEx(exe, port, args, environment);
			} catch (IOException e) {
				throw new WebDriverException(e);
			}
		}
	}
}
