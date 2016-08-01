package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.profiles.configuration.UrlMapping;
import au.com.agic.apptesting.utils.ProxyDetails;
import au.com.agic.apptesting.utils.ThreadDetails;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.zaproxy.clientapi.core.ClientApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * Represents the details required by a feature to run
 */
public class ThreadDetailsImpl implements ThreadDetails {

	private final UrlMapping url;
	private final WebDriver webDriver;
	private final Map<String, String> dataset = new HashMap<>();
	private final String reportDirectory;
	private boolean failed;
	private List<ProxyDetails<?>> proxies;
	private long sleep;

	/**
	 * The ZAP client api interface
	 */
	private ClientApi zapClientApi;

	public ThreadDetailsImpl(
		@NotNull final UrlMapping url,
		@NotNull final Map<String, String> dataset,
		@NotNull final String reportDirectory,
		@NotNull final List<ProxyDetails<?>> proxies,
		@NotNull final WebDriver webDriver) {
		checkNotNull(url);
		checkNotNull(webDriver);

		this.url = url;
		this.webDriver = webDriver;
		this.dataset.clear();
		this.dataset.putAll(dataset);
		this.reportDirectory = reportDirectory;
		this.proxies = new ArrayList<>(proxies);
	}

	@Override
	public void setDefaultSleep(final long defaultSleep) {
		this.sleep = defaultSleep;
	}

	@Override
	public long getDefaultSleep() {
		return sleep;
	}

	@Override
	public UrlMapping getUrlDetails() {
		return url;
	}

	@Override
	public WebDriver getWebDriver() {
		return webDriver;
	}

	@Override
	public Map<String, String> getDataSet() {
		return new HashMap<>(dataset);
	}

	@Override
	public void setDataSet(final Map<String, String> dataSet) {
		dataset.clear();
		if (dataSet != null) {
			dataset.putAll(dataSet);
		}
	}

	@Override
	public boolean getFailed() {
		return failed;
	}

	@Override
	public void setFailed(final boolean failed) {
		this.failed = failed;
	}

	@Override
	public String getReportDirectory() {
		return reportDirectory;
	}

	@Override
	public List<ProxyDetails<?>> getProxyInterface() {
		return proxies;
	}

	@Override
	public Optional<ProxyDetails<?>> getProxyInterface(@NotNull final String name) {
		checkArgument(StringUtils.isNotBlank(name));

		return proxies.stream()
			.filter(x -> name.equals(x.getProxyName()))
			.findFirst();
	}

	@Override
	public void setProxyInterface(final List<ProxyDetails<?>> myProxies) {
		this.proxies = new ArrayList<>(myProxies);
	}
}
