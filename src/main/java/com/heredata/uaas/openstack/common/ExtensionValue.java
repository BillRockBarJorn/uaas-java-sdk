package com.heredata.uaas.openstack.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.MoreObjects;
import com.heredata.uaas.model.common.Extension;
import com.heredata.uaas.model.common.Link;

import java.net.URI;
import java.util.Date;
import java.util.List;

/**
 * Represents an Extension which adds additional functionality to the OpenStack API
 *
 * @author wuzz
 */
public class ExtensionValue implements Extension {

	private static final long serialVersionUID = 1L;
	String name;
	URI namespace;
	String alias;
	Date updated;
	String description;
	List<GenericLink> links;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URI getNamespace() {
		return namespace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAlias() {
		return alias;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssX")
	public Date getUpdated() {
		return updated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends Link> getLinks() {
		return links;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(Extension.class).omitNullValues()
						.add("name", name)
						.add("namespace", namespace)
						.add("description", description)
						.add("alias", alias)
						.add("updated", updated)
						.add("links", links)
						.addValue("\n")
						.toString();
	}

	@JsonRootName("extensions")
	public static class ExtensionList extends ListResult<ExtensionValue> {
		private static final long serialVersionUID = 1L;

		@JsonProperty("values")
		private List<ExtensionValue> list;

		@Override
		public List<ExtensionValue> value() {
			return list;
		}
	}

	public static class Extensions extends ListResult<ExtensionValue> {
		private static final long serialVersionUID = 1L;

		@JsonProperty("extensions")
		private List<ExtensionValue> list;

		@Override
		public List<ExtensionValue> value() {
			return list;
		}
	}
}
