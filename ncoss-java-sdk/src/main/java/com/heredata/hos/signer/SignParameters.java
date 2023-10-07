package com.heredata.hos.signer;

import java.util.Arrays;
import java.util.List;

import static com.heredata.hos.comm.HOSRequestParameters.*;


public class SignParameters {

    public static final String AUTHORIZATION_PREFIX = "HOS ";

    public static final String AUTHORIZATION_ACCESS_KEY_ID = "AccessKeyId";

    public static final String AUTHORIZATION_ADDITIONAL_HEADERS = "AdditionalHeaders";

    public static final String AUTHORIZATION_SIGNATURE = "Signature";

    public static final String NEW_LINE = "\n";

    public static final List<String> SUB_RESOURCES = Arrays.asList(new String[]{
            SUBRESOURCE_ACL, SUBRESOURCE_CORS, SUBRESOURCE_LIFECYCLE, SUBRESOURCE_POLICY, SUBRESOURCE_QUOTA, SUBRESOURCE_TAGGING
            , SUBRESOURCE_VRESIONING, SUBRESOURCE_UPLOADS, SUBRESOURCE_RESTORE, PART_NUMBER, UPLOAD_ID, SUBRESOURCE_VRESION_ID
            , SUBRESOURCE_VRESIONS});

}
