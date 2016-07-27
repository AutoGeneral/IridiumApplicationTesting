package au.com.agic.apptesting.browsermob;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.filters.RequestFilter;
import net.lightbody.bmp.filters.RequestFilterAdapter;

/**
 * We extend the BrowserMob server to override some behaviour
 */
public class ExtendedBrowserMobProxyServer extends BrowserMobProxyServer {
	private static final int BUFFER_SIZE = 16777216;

	/**
	 * https://github.com/lightbody/browsermob-proxy/issues/372
	 */
	@Override
	public void addRequestFilter(RequestFilter filter) {
		addFirstHttpFilterFactory(new RequestFilterAdapter.FilterSource(filter, BUFFER_SIZE));
	}
}
