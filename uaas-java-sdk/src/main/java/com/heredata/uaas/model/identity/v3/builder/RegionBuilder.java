package com.heredata.uaas.model.identity.v3.builder;


import com.heredata.uaas.common.Buildable.Builder;
import com.heredata.uaas.model.identity.v3.Region;

public interface RegionBuilder extends Builder<RegionBuilder, Region> {

    /**
     * @see Region#getId()
     */
    RegionBuilder id(String id);

    /**
     * @see Region#getDescription()
     */
    RegionBuilder description(String description);

    /**
     * @see Region#getParentRegionId()
     */
    RegionBuilder parentRegionId(String parentRegionId);

}
