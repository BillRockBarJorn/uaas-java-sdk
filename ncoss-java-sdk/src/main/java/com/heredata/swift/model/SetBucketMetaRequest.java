package com.heredata.swift.model;

import com.heredata.swift.model.bucket.BucketAclRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetBucketMetaRequest extends GenericRequest {

    /**
     * bucket的ACL元数据 {@link BucketAclRequest}
     */
    private BucketAclRequest bucketAclRequest;

    /**
     * bucket的配额元数据 {@link SetBucketQuotaRequest}
     */
    private SetBucketQuotaRequest setBucketQuotaRequest;

    /**
     * 自定义元数据
     */
    private Map<String, String> userMeta;

    private List<String> needRemoveMeta;
}
