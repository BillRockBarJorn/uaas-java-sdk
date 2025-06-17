package com.heredata.uaas.model.common;


import com.heredata.uaas.common.Buildable;
import com.heredata.uaas.model.ModelEntity;
import com.heredata.uaas.model.common.builder.LinkBuilder;

/**
 * Model for a generic link
 *
 * @author wuzz
 */
public interface Link extends ModelEntity, Buildable<LinkBuilder> {

	/**
	 * @return the relative URL or null
	 */
	String getRel();

	/**
	 * @return the href URL
	 */
	String getHref();

	/**
	 * @return the type of link or null
	 */
	String getType();

}
