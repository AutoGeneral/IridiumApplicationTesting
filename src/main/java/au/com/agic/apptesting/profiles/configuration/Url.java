package au.com.agic.apptesting.profiles.configuration;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents a {@code <url>} element
 */
public class Url {

	private String url;
	private String name;

	public Url() {

	}

	public Url(@NotNull final String url, @NotNull final String name) {
		checkArgument(StringUtils.isNotEmpty(url));
		checkArgument(StringUtils.isNotEmpty(name));

		this.url = url;
		this.name = name;
	}

	@XmlValue
	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
