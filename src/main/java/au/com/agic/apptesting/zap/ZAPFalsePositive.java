package au.com.agic.apptesting.zap;

/**
 * Represents a false positive alert that can be returned by ZAP
 */
public class ZAPFalsePositive {
	private String url;
	private String parameter;
	private Integer cweId;
	private Integer wascId;

	public ZAPFalsePositive(final String url, final String parameter, final int cweId, final int wascId) {
		this.url = url;
		this.parameter = parameter;
		this.cweId = cweId;
		this.wascId = wascId;
	}

	public ZAPFalsePositive(final String url, final String parameter, final String cweId, final String wascId) {
		this.url = url;
		this.parameter = parameter;
		if (cweId == null || "".equals(cweId)) {
			this.cweId = null;
		} else {
			try {
				this.cweId = Integer.parseInt(cweId);
			} catch (final NumberFormatException ignore) {
				/*
					Ignored
				 */
			}
		}

		if (wascId == null || "".equals(wascId)) {
			this.wascId = null;
		} else {
			try {
				this.wascId = Integer.parseInt(cweId);
			} catch (final NumberFormatException ignore) {
				/*
					Ignored
				 */
			}
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(final String parameter) {
		this.parameter = parameter;
	}

	public int getCweId() {
		return cweId;
	}

	public void setCweId(final int cweId) {
		this.cweId = cweId;
	}

	public boolean matches(
			final String otherUrl,
			final String otherParameter,
			final int otherCweid,
			final int otherWascId) {
		if (url != null
				&& otherUrl != null
				&& otherUrl.matches(url)
				&& parameter != null
				&& otherParameter != null
				&& otherParameter.matches(parameter)) {
			if (cweId != null && cweId == otherCweid) {
				return true;
			}
			if (wascId != null && wascId == otherWascId) {
				return true;
			}
		}
		return false;
	}
}
