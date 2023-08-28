package com.heredata.uaas.openstack.identity.v3.internal;

import com.heredata.uaas.api.identity.v3.RegionService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Region;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneRegion;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneRegion.Regions;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.heredata.uaas.core.transport.ClientConstants.PATH_REGIONS;

/**
 * Implementation of v3 region service
 *
 */
public class RegionServiceImpl extends BaseIdentityServices implements RegionService {

    @Override
    public Region create(Region region) {
        checkNotNull(region);
        return post(KeystoneRegion.class, uri(PATH_REGIONS)).entity(region).execute();
    }

    @Override
    public Region create(String regionId, String description, String parentRegionId) {
        checkNotNull(regionId);
        checkNotNull(description);
        checkNotNull(parentRegionId);
        return create(KeystoneRegion.builder().id(regionId).description(description).parentRegionId(parentRegionId).build());
    }

    @Override
    public Region get(String regionId) {
        checkNotNull(regionId);
        return get(KeystoneRegion.class, PATH_REGIONS, "/", regionId).execute();
    }

    @Override
    public Region update(Region region) {
        checkNotNull(region);
        String regionId = region.getId();
        region = region.toBuilder().id(null).build();
        return patch(KeystoneRegion.class, PATH_REGIONS, "/", regionId).entity(region).execute();
    }

    @Override
    public ActionResponse delete(String regionId) {
        checkNotNull(regionId);
        return deleteWithResponse(PATH_REGIONS, "/", regionId).execute();
    }

    @Override
    public List<? extends Region> list() {
        return get(Regions.class, uri(PATH_REGIONS)).execute().getList();
    }

}
