package com.heredata.swift.operation;

import com.heredata.comm.HttpMethod;
import com.heredata.comm.RequestMessage;
import com.heredata.comm.ServiceClient;
import com.heredata.auth.CredentialsProvider;
import com.heredata.swift.comm.SWIFTRequestMessage;
import com.heredata.swift.comm.SwiftRequestParameters;
import com.heredata.swift.internal.RequestMessageBuilder;
import com.heredata.swift.model.AccountInfo;
import com.heredata.swift.model.bucket.BucketListRequest;
import com.heredata.swift.model.bucket.BukcetListResult;
import com.heredata.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.heredata.swift.parser.ResponseParsers.getAccountInfoResponseParser;
import static com.heredata.swift.parser.ResponseParsers.getBukcetListResponseParser;

/**
 * account operation.
 */
public class SwiftAccountOperation extends SwiftOperation {

    public SwiftAccountOperation(ServiceClient client, CredentialsProvider credsProvider) {
        super(client, credsProvider);
    }

    public BukcetListResult bukcetList(BucketListRequest bucketListRequest) {

        Map<String, String> query = new HashMap<>();
        query.put("format", "json");

        /**
         * 添加条件查询参数
         */
        if (bucketListRequest.getLimit() != null) {
            query.put("limit", bucketListRequest.getLimit() + "");
        }
        if (!StringUtils.isNullOrEmpty(bucketListRequest.getStartAfter())) {
            query.put("marker", bucketListRequest.getStartAfter());
        }
        if (!StringUtils.isNullOrEmpty(bucketListRequest.getPrefix())) {
            query.put(SwiftRequestParameters.PREFIX, bucketListRequest.getPrefix());
        }

        RequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setAccount(credsProvider.getCredentials().getAccount()).setParameters(query)
                .setMethod(HttpMethod.GET).build();

        BukcetListResult bukcetListResult = doOperation(request, getBukcetListResponseParser, null, null, true);
        bukcetListResult.setLimit(bucketListRequest.getLimit());
        bukcetListResult.setPrefix(bucketListRequest.getPrefix());
        bukcetListResult.setStartAfter(bucketListRequest.getStartAfter());
        return bukcetListResult;
    }

    public AccountInfo getAccountMeta() {
        Map<String, String> query = new HashMap<>();
        query.put("format", "json");

        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setAccount(credsProvider.getCredentials().getAccount()).setParameters(query)
                .setMethod(HttpMethod.HEAD).build();

        return doOperation(request, getAccountInfoResponseParser, null, null, true);
    }
}
