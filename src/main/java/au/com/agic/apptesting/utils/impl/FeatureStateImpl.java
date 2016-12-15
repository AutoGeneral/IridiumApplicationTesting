package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkArgument;

import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.profiles.configuration.UrlMapping;
import au.com.agic.apptesting.utils.FeatureState;
import au.com.agic.apptesting.utils.ProxyDetails;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Represents the details required by a feature to run
 */
public class FeatureStateImpl implements FeatureState {

	private int defaultKeyStrokeDelay = Constants.KEY_STROKE_DELAY;
	private boolean autoAlias = true;
	private final UrlMapping url;
	private final Map<String, String> dataset = new HashMap<>();
	private final String reportDirectory;
	private boolean failed;
	private List<ProxyDetails<?>> proxies;
	private long sleep = Constants.DEFAULT_WAIT_TIME;
	private long wait = Constants.WAIT;
	private boolean skip = false;

	public FeatureStateImpl(
		final UrlMapping url,
		@NotNull final Map<String, String> dataset,
		@NotNull final String reportDirectory,
		@NotNull final List<ProxyDetails<?>> proxies) {

		this.url = url;
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
	@Nullable
	public UrlMapping getUrlDetails() {
		return url;
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

	@Override
	public boolean getAutoAlias() {
		return autoAlias;
	}

	@Override
	public void setAutoAlias(final boolean autoAlias) {
		this.autoAlias = autoAlias;
	}

	@Override
	public boolean getSkipSteps() {
		return skip;
	}

	@Override
	public void setSkipSteps(final boolean skipSteps) {
		this.skip = skipSteps;
	}

	@Override
	public int getDefaultKeyStrokeDelay() {
		return defaultKeyStrokeDelay;
	}

	@Override
	public void setDefaultKeyStrokeDelay(final int defaultKeyStrokeDelay) {
		this.defaultKeyStrokeDelay = defaultKeyStrokeDelay;
	}

	@Override
	public long getDefaultWait() {
		return wait;
	}

	@Override
	public void setDefaultWait(final long myWait) {
		this.wait = myWait;
	}
}
