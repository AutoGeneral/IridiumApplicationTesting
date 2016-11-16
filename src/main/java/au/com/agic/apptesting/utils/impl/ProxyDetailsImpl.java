package au.com.agic.apptesting.utils.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import au.com.agic.apptesting.utils.ProxyDetails;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

/**
 * An data object that holds proxy details
 */
public class ProxyDetailsImpl<T> implements ProxyDetails<T> {

	private boolean mainProxy;
	private final int port;
	private final Optional<T> interfaceObject;
	private final String name;
	private final Map<String, Object> properties = new HashMap<>();

	public ProxyDetailsImpl(final int port) {
		this.port = port;
		this.mainProxy = false;
		this.name = null;
		this.interfaceObject = Optional.empty();
	}

	public ProxyDetailsImpl(final int port, @NotNull final T interfaceObject) {
		checkNotNull(interfaceObject);

		this.port = port;
		this.mainProxy = false;
		this.name = null;
		this.interfaceObject = Optional.of(interfaceObject);
	}

	public ProxyDetailsImpl(final int port, final boolean mainProxy, @NotNull final T interfaceObject) {
		checkNotNull(interfaceObject);

		this.port = port;
		this.mainProxy = mainProxy;
		this.name = null;
		this.interfaceObject = Optional.of(interfaceObject);
	}

	public ProxyDetailsImpl(
		final int port,
		final boolean mainProxy,
		@NotNull final String name,
		@NotNull final T interfaceObject) {
		checkNotNull(interfaceObject);
		checkArgument(StringUtils.isNotBlank(name));

		this.port = port;
		this.mainProxy = mainProxy;
		this.name = name;
		this.interfaceObject = Optional.of(interfaceObject);
	}

	@Override
	public String getProxyName() {
		return name;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public Optional<T> getInterface() {
		return interfaceObject;
	}

	@NotNull
	@Override
	public Map<String, Object> getProperties() {
		return new HashMap<>(properties);
	}

	@Override
	public void setProperties(@NotNull final Map<String, Object> properties) {
		checkNotNull(properties);

		this.properties.clear();
		this.properties.putAll(properties);
	}

	@Override
	public boolean isMainProxy() {
		return mainProxy;
	}

	@Override
	public void setMainProxy(boolean mainProxy) {
		this.mainProxy = mainProxy;
	}
}
