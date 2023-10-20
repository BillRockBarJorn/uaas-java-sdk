package com.heredata.s3.operation;

import com.heredata.comm.HttpMethod;
import com.heredata.comm.ServiceClient;
import com.heredata.model.VoidResult;
import com.heredata.auth.CredentialsProvider;
import com.heredata.s3.comm.S3RequestMessage;
import com.heredata.s3.handler.S3RequestMessageBuilder;
import com.heredata.s3.model.AccountInfo;
import com.heredata.s3.model.SetAccountQuotaRequest;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static com.heredata.s3.comm.S3RequestParameters.QUOTA;
import static com.heredata.s3.parser.RequestMarshallers.setAccountInfoRequestMatshaller;
import static com.heredata.s3.parser.ResponseParsers.getAccountInfoResponseParser;

/**
 * <p>Title: S3AccountOperation</p>
 * <p>Description: 账户操作类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:10
 */
public class S3AccountOperation extends S3Operation {

    public S3AccountOperation(ServiceClient client, CredentialsProvider credsProvider) {
        super(client, credsProvider);
    }

    public AccountInfo getAccountInfo() {
        S3RequestMessage request = new S3RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setAccount(credsProvider.getCredentials().getAccount())
                .setMethod(HttpMethod.HEAD).build();

        return doOperation(request, getAccountInfoResponseParser, null, null, true);
    }

    public VoidResult setAccountQuota(SetAccountQuotaRequest setAccountQuotaRequest) {

        Map<String, String> map = new HashMap<>();
        map.put(QUOTA, null);
        byte[] marshall = setAccountInfoRequestMatshaller.marshall(setAccountQuotaRequest);
        S3RequestMessage request = new S3RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setAccount(credsProvider.getCredentials().getAccount())
                .setMethod(HttpMethod.PUT).setParameters(map)
                .setInputStream(new ByteArrayInputStream(marshall)).setInputSize(marshall.length)
                .build();

        return doOperation(request, requestIdResponseParser, null, null, true);
    }


    public AccountInfo getAccountQuota() {

        Map<String, String> map = new HashMap<>();
        map.put("quota", null);

        S3RequestMessage request = new S3RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint()).setParameters(map)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setMethod(HttpMethod.GET).build();

        return doOperation(request, getAccountInfoResponseParser, null, null, true);
    }
}
