package com.heredata.uaas.openstack.common;

import com.google.common.base.MoreObjects;
import com.heredata.uaas.model.common.Link;
import com.heredata.uaas.model.common.builder.LinkBuilder;

/**
 * A Link holds information about a URL, Relative URL and the type of the link
 *
 * @author wuzz
 */
public class GenericLink implements Link {

	private static final long serialVersionUID = 1L;

	private String rel;
	private String href;
	private String type;

	public GenericLink() { }

	public GenericLink(String rel, String href, String type) {
		this.rel = rel;
		this.type = type;
		this.href = href;
	}

	/**
	 * @return the link builder
	 */
	public static LinkBuilder builder() {
		return new LinkConcreteBuilder();
	}

	@Override
	public LinkBuilder toBuilder() {
		return new LinkConcreteBuilder(this);
	}

	/**
	 * @return the relative URL or null
	 */
	@Override
	public String getRel() {
		return rel;
	}

	/**
	 * @return the href URL
	 */
	@Override
	public String getHref() {
		return href;
	}

	/**
	 * @return the type of link or null
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).omitNullValues()
						.add("href", href).add("rel", rel).add("type", type)
						.toString();
	}

	public static class LinkConcreteBuilder implements LinkBuilder {

		GenericLink model;

		LinkConcreteBuilder() {
			this(new GenericLink());
		}

		LinkConcreteBuilder(GenericLink link) {
			this.model = link;
		}

		/**
		 * @see GenericLink#getRel()
		 */
		@Override
		public LinkConcreteBuilder rel(String rel) {
			model.rel = rel;
			return this;
		}

		/**
		 * @see GenericLink#getHref()
		 */
		@Override
		public LinkConcreteBuilder href(String href) {
			model.href = href;
			return this;
		}

		/**
		 * @see GenericLink#getType()
		 */
		@Override
		public LinkConcreteBuilder type(String type) {
			model.type = type;
			return this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Link build() {
			return model;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public LinkBuilder from(Link in) {
			this.model = (GenericLink)in;
			return this;
		}
	}

}
