package com.heredata.hos.operation;

import com.heredata.comm.HttpMethod;
import com.heredata.comm.ServiceClient;
import com.heredata.auth.CredentialsProvider;
import com.heredata.hos.comm.HOSRequestMessage;
import com.heredata.hos.handler.HOSRequestMessageBuilder;
import com.heredata.hos.model.AccountInfo;
import com.heredata.hos.model.SetAccountQuotaRequest;
import com.heredata.model.VoidResult;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static com.heredata.hos.comm.HOSRequestParameters.QUOTA;
import static com.heredata.hos.parser.RequestMarshallers.setAccountInfoRequestMatshaller;
import static com.heredata.hos.parser.ResponseParsers.getAccountInfoResponseParser;

/**
 * <p>Title: HOSAccountOperation</p>
 * <p>Description: 账户操作类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:10
 */
public class HOSAccountOperation extends HOSOperation {

    public HOSAccountOperation(ServiceClient client, CredentialsProvider credsProvider) {
        super(client, credsProvider);
    }

    public AccountInfo getAccountInfo() {
        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setAccount(credsProvider.getCredentials().getAccount())
                .setMethod(HttpMethod.HEAD).build();

        return doOperation(request, getAccountInfoResponseParser, null, null, true);
    }

    public VoidResult setAccountQuota(SetAccountQuotaRequest setAccountQuotaRequest) {

        Map<String, String> map = new HashMap<>();
        map.put(QUOTA, null);
        byte[] marshall = setAccountInfoRequestMatshaller.marshall(setAccountQuotaRequest);
        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
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

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint()).setParameters(map)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setMethod(HttpMethod.GET).build();

        return doOperation(request, getAccountInfoResponseParser, null, null, true);
    }
}
