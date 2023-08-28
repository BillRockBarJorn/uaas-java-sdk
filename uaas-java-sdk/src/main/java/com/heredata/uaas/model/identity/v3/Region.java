package com.heredata.uaas.model.identity.v3;

import com.heredata.uaas.common.Buildable;
import com.heredata.uaas.model.ModelEntity;
import com.heredata.uaas.model.identity.v3.builder.RegionBuilder;

public interface Region extends ModelEntity, Buildable<RegionBuilder> {

    /**
     * @return the user-defined id of the region
     */
    String getId();

    /**
     * @return the description of the region
     */
    String getDescription();

    /**
     * @return the id of the parent region
     */
    String getParentRegionId();

}
