package au.com.agic.apptesting.profiles.configuration;

import static com.google.common.base.Preconditions.checkArgument;

import au.com.agic.apptesting.constants.Defaults;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Represents a {@code <urlMapping>} element
 */
public class UrlMapping {

	private String tags;
	private List<Url> urls = new ArrayList<>();

	public UrlMapping() {

	}

	public UrlMapping(@NotNull final String url) {
		urls = new ArrayList<>();
		final Url urlEntity = new Url(url, Defaults.DEFAULT_URL_NAME);
		urls.add(urlEntity);
	}

	@XmlAttribute
	public String getTags() {
		return tags;
	}

	public void setTags(final String tags) {
		this.tags = tags;
	}

	@XmlElement(name = "url")
	public List<Url> getUrls() {
		return urls;
	}

	public void setUrls(final List<Url> urls) {
		this.urls = new ArrayList<>(urls);
	}

	@XmlTransient
	public String getDefaultUrl() {
		for (final Url url : urls) {
			if (Defaults.DEFAULT_URL_NAME.equalsIgnoreCase(url.getName())) {
				return url.getUrl();
			}
		}

		return null;
	}

	public String getUrl(@NotNull final String name) {
		checkArgument(StringUtils.isNotBlank(name));
		for (final Url url : urls) {
			if (name.equalsIgnoreCase(url.getName())) {
				return url.getUrl();
			}
		}

		return null;
	}
}
