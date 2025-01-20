package com.heredata.hos.parser;

import com.heredata.exception.ClientException;
import com.heredata.hos.model.*;
import com.heredata.hos.model.DeleteVersionsRequest.KeyVersion;
import com.heredata.parser.Marshaller;
import com.heredata.utils.DateUtil;
import com.heredata.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.heredata.utils.StringUtils.stringToByteArray;

/**
 * <p>Title: RequestMarshallers</p>
 * <p>Description: 将可读的数据处理成字节数组 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:14
 */
public final class RequestMarshallers {

    public static final DeleteObjectsRequestMarshaller deleteObjectsRequestMarshaller = new DeleteObjectsRequestMarshaller();
    public static final DeleteVersionsRequestMarshaller deleteVersionsRequestMarshaller = new DeleteVersionsRequestMarshaller();

    public static final SetBucketLifecycleRequestMarshaller setBucketLifecycleRequestMarshaller = new SetBucketLifecycleRequestMarshaller();
    public static final SetBucketTaggingRequestMarshaller setBucketTaggingRequestMarshaller = new SetBucketTaggingRequestMarshaller();
    public static final CompleteMultipartUploadRequestMarshaller completeMultipartUploadRequestMarshaller = new CompleteMultipartUploadRequestMarshaller();
    public static final SetBucketVersioningRequestMarshaller setBucketVersioningRequestMarshaller = new SetBucketVersioningRequestMarshaller();
    public static final SetAclRequestMarshaller setAclRequestMarshaller = new SetAclRequestMarshaller();
    public static final SetBucketQuotaRequestMarshaller setBucketQuotaRequestMarshaller = new SetBucketQuotaRequestMarshaller();
    public static final RestoreObjectRequestMarshaller restoreObjectRequestMarshaller = new RestoreObjectRequestMarshaller();

    public static final SetAccountInfoRequestMatshaller setAccountInfoRequestMatshaller = new SetAccountInfoRequestMatshaller();


    public interface RequestMarshallerByteArr<R> extends Marshaller<byte[], R> {

    }

    public static final class SetAccountInfoRequestMatshaller implements RequestMarshallerByteArr<SetAccountQuotaRequest> {
        @Override
        public byte[] marshall(SetAccountQuotaRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<QuotaConfiguration>");
            xmlBody.append("<StorageQuota>");
            xmlBody.append(request.getAccountQuota());
            xmlBody.append("</StorageQuota>");
            xmlBody.append("<StorageMaxCount>");
            xmlBody.append(request.getStorageMaxCount());
            xmlBody.append("</StorageMaxCount>");
            xmlBody.append("</QuotaConfiguration>");
            return stringToByteArray(xmlBody.toString());
        }
    }

    public static final class SetBucketLifecycleRequestMarshaller implements RequestMarshallerByteArr<SetBucketLifecycleRequest> {
        @Override
        public byte[] marshall(SetBucketLifecycleRequest request) {

            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<LifecycleConfiguration>");
            for (int i = 0; i < request.getLifecycleRules().size(); i++) {
                LifecycleRule rule = request.getLifecycleRules().get(i);
                xmlBody.append("<Rule>");
                if (!StringUtils.isNullOrEmpty(rule.getId())) {
                    xmlBody.append("<ID>" + rule.getId() + "</ID>");
                }
                xmlBody.append("<Filter>");
                if (rule.getFilter().getPrefix() != null) {
                    xmlBody.append("<Prefix>" + rule.getFilter().getPrefix() + "</Prefix>");
                }
                if (rule.getFilter().getTag() != null) {
                    xmlBody.append("<Tag>" + rule.getFilter().getTag() + "</Tag>");
                }
                xmlBody.append("</Filter>");
                if (rule.getStatus() == LifecycleRule.RuleStatus.Enabled) {
                    xmlBody.append("<Status>Enabled</Status>");
                } else {
                    xmlBody.append("<Status>Disabled</Status>");
                }
                if (rule.getExpiration() != null) {
                    xmlBody.append("<Expiration>");
                    if (rule.getExpiration().getDays() != null) {
                        if (rule.getTransitions() != null && !rule.getTransitions().isEmpty() && rule.getTransitions().get(0).getDays() != null) {
                            if (rule.getExpiration().getDays() <= rule.getTransitions().get(0).getDays()) {
                                throw new ClientException("expiration not <= transition");
                            }
                        }
                        xmlBody.append("<Days>" + rule.getExpiration().getDays() + "</Days>");
                    }
                    if (rule.getExpiration().getDate() != null) {
                        if (rule.getTransitions() != null && !rule.getTransitions().isEmpty() && rule.getTransitions().get(0).getDate() != null) {
                            if (rule.getExpiration().getDate().getTime() <= rule.getTransitions().get(0).getDate().getTime()) {
                                throw new ClientException("expiration not <= transition");
                            }
                        }
                        xmlBody.append("<Date>" + DateUtil.formatIso8601Date(rule.getExpiration().getDate()) + "</Date>");
                    }
                    if (rule.getExpiration().getExpiredObjectDeleteMarker() != null) {
                        xmlBody.append("<ExpiredObjectDeleteMarker>" + rule.getExpiration().getExpiredObjectDeleteMarker() + "</ExpiredObjectDeleteMarker>");
                    }
                    xmlBody.append("</Expiration>");
                }

                if (rule.getTransitions() != null && !rule.getTransitions().isEmpty()) {
                    xmlBody.append("<Transition>");
                    if (rule.getTransitions().get(0).getDays() != null) {
                        xmlBody.append("<Days>" + rule.getTransitions().get(0).getDays() + "</Days>");
                    }
                    if (rule.getTransitions().get(0).getDate() != null) {
                        xmlBody.append("<Date>" + DateUtil.formatIso8601Date(rule.getTransitions().get(0).getDate()) + "</Date>");
                    }
                    if (rule.getTransitions().get(0).getStorageClass() != null) {
                        xmlBody.append("<StorageClass>" + rule.getTransitions().get(0).getStorageClass().toString() + "</StorageClass>");
                    }
                    xmlBody.append("</Transition>");
                }

                xmlBody.append("</Rule>");
            }

            xmlBody.append("</LifecycleConfiguration>");
            return stringToByteArray(xmlBody.toString());
        }

    }

    public static final class CompleteMultipartUploadRequestMarshaller implements RequestMarshallerByteArr<CompleteMultipartUploadRequest> {
        @Override
        public byte[] marshall(CompleteMultipartUploadRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            List<PartETag> eTags = request.getPartETags();
            xmlBody.append("<CompleteMultipartUpload>");
            for (int i = 0; i < eTags.size(); i++) {
                PartETag part = eTags.get(i);
                xmlBody.append("<Part>");
                xmlBody.append("<PartNumber>" + part.getPartNumber() + "</PartNumber>");
                xmlBody.append("<ETag>" + part.getETag() + "</ETag>");
                xmlBody.append("</Part>");
            }
            xmlBody.append("</CompleteMultipartUpload>");
            return stringToByteArray(xmlBody.toString());
        }

    }

    public static final class DeleteObjectsRequestMarshaller implements RequestMarshallerByteArr<DeleteObjectsRequest> {

        @Override
        public byte[] marshall(DeleteObjectsRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            List<String> keysToDelete = request.getKeys();

            xmlBody.append("<Delete>");
            for (int i = 0; i < keysToDelete.size(); i++) {
                String key = keysToDelete.get(i);
                xmlBody.append("<Object>");
                xmlBody.append("<Key>" + escapeKey(key) + "</Key>");
                xmlBody.append("</Object>");
            }
            xmlBody.append("</Delete>");

            return stringToByteArray(xmlBody.toString());
        }

    }


    public static final class DeleteVersionsRequestMarshaller implements RequestMarshallerByteArr<DeleteVersionsRequest> {

        @Override
        public byte[] marshall(DeleteVersionsRequest request) {
            StringBuffer xmlBody = new StringBuffer();
//            boolean quiet = request.getQuiet();
            List<KeyVersion> keysToDelete = request.getKeys();

            xmlBody.append("<Delete>");
//            xmlBody.append("<Quiet>" + quiet + "</Quiet>");
            for (int i = 0; i < keysToDelete.size(); i++) {
                KeyVersion key = keysToDelete.get(i);
                xmlBody.append("<Object>");
                xmlBody.append("<Key>" +
                        escapeKey(key.getKey()) + "</Key>");
                if (key.getVersion() != null) {
                    xmlBody.append("<VersionId>" + key.getVersion() + "</VersionId>");
                }
                xmlBody.append("</Object>");
            }
            xmlBody.append("</Delete>");

            return stringToByteArray(xmlBody.toString());
        }

    }

    public static final class SetBucketTaggingRequestMarshaller implements RequestMarshallerByteArr<SetTaggingRequest> {

        @Override
        public byte[] marshall(SetTaggingRequest request) {
            StringBuffer xmlBody = new StringBuffer();
            TagSet tagSet = request.getTagSet();
            xmlBody.append("<Tagging><TagSet>");
            Map<String, String> tags = tagSet.getTags();
            if (!tags.isEmpty()) {
                for (Map.Entry<String, String> tag : tags.entrySet()) {
                    xmlBody.append("<Tag>");
                    xmlBody.append("<Key>" + tag.getKey() + "</Key>");
                    xmlBody.append("<Value>" + tag.getValue() + "</Value>");
                    xmlBody.append("</Tag>");
                }
            }
            xmlBody.append("</TagSet></Tagging>");
            return stringToByteArray(xmlBody.toString());
        }
    }

    public static final class SetBucketVersioningRequestMarshaller
            implements RequestMarshallerByteArr<SetBucketVersioningRequest> {

        @Override
        public byte[] marshall(SetBucketVersioningRequest setBucketVersioningRequest) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<VersioningConfiguration>");
            xmlBody
                    .append("<Status>" + setBucketVersioningRequest.getVersioningConfiguration().getStatus() + "</Status>");
            xmlBody.append("</VersioningConfiguration>");

            return stringToByteArray(xmlBody.toString());
        }

    }

    public static final class SetAclRequestMarshaller
            implements RequestMarshallerByteArr<SetAclRequest> {
        @Override
        public byte[] marshall(SetAclRequest setBucketAclRequest) {
            //<AccessControlPolicy>
            //    <Owner>
            //        <ID>c7a4c02ee77b11eaa1cc01cf93dcddb3</ID>
            //    </Owner>
            //    <AccessControlList>
            //        <Grant>
            //            <Grantee xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="CanonicalUser">
            //                <ID>user_id<ID>
            //            </Grantee>
            //            <Permission>permission</Permission>
            //        </Grant>
            //        <Grant>
            //            <Grantee xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="Group">
            //               <URI>http://www.heredata.com/groups/global/AllUsers</URI>                    </Grantee>
            //            <Permission>READ</Permission>
            //        </Grant>
            //    </AccessControlList>
            //</AccessControlPolicy>
            StringBuffer xmlBody = new StringBuffer();
//            xmlBody.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            xmlBody.append("<AccessControlPolicy>")
                    .append("<Owner>")
                    .append("<ID>").append(setBucketAclRequest.getOwner().getId()).append("</ID>")
                    .append("</Owner>")
                    .append("<AccessControlList>");
            Set<Grant> grants = setBucketAclRequest.getAccessControlList().getGrants();
            for (Grant grant : grants) {
                if (grant.getGrantee() instanceof CanonicalUserGrantee) {
                    xmlBody.append("<Grant>")
                            .append("<Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"CanonicalUser\">")
                            .append("<ID>" + ((CanonicalUserGrantee) grant.getGrantee()).getUserId() + "</ID>")
                            .append("</Grantee>")
                            .append("<Permission>" + grant.getPermission().getCannedAcl() + "</Permission>")
                            .append("</Grant>");
                } else if (grant.getGrantee() instanceof GroupGrantee) {
                    xmlBody.append("<Grant>")
                            .append("<Grantee xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"Group\">")
                            .append("<URI>" + ((GroupGrantee) grant.getGrantee()).getGroupUri() + "</URI>")
                            .append("</Grantee>")
                            .append("<Permission>" + grant.getPermission().getCannedAcl() + "</Permission>")
                            .append("</Grant>");
                }
            }
            xmlBody.append("</AccessControlList>")
                    .append("</AccessControlPolicy>");
            return stringToByteArray(xmlBody.toString());
        }

    }

    public static final class SetBucketQuotaRequestMarshaller
            implements RequestMarshallerByteArr<SetBucketQuotaRequest> {
        @Override
        public byte[] marshall(SetBucketQuotaRequest setBucketQuotaRequest) {
            StringBuffer xmlBody = new StringBuffer();
            xmlBody.append("<QuotaConfiguration><StorageQuota>");
            xmlBody.append(setBucketQuotaRequest.getStorageQuota() == null ? 0 : setBucketQuotaRequest.getStorageQuota());
            xmlBody.append("</StorageQuota><StorageMaxCount>");
            xmlBody.append(setBucketQuotaRequest.getStorageMaxCount() == null ? 0 : setBucketQuotaRequest.getStorageMaxCount());
            xmlBody.append("</StorageMaxCount></QuotaConfiguration>");
            return stringToByteArray(xmlBody.toString());
        }

    }

    public static final class RestoreObjectRequestMarshaller implements RequestMarshallerByteArr<RestoreObjectRequest> {

        @Override
        public byte[] marshall(RestoreObjectRequest request) {
            StringBuffer body = new StringBuffer();

            body.append("<RestoreRequest>");
            body.append("<Days>" + request.getRestoreConfiguration().getDays() + "</Days>");
            body.append("</RestoreRequest>");
            return stringToByteArray(body.toString());
        }

    }

    private static enum EscapedChar {
        // "\r"
        RETURN("&#x000D;"),

        // "\n"
        NEWLINE("&#x000A;"),

        // " "
        SPACE("&#x0020;"),

        // "\t"
        TAB("&#x0009;"),

        // """
        QUOT("&quot;"),

        // "&"
        AMP("&amp;"),

        // "<"
        LT("&lt;"),

        // ">"
        GT("&gt;");

        private final String escapedChar;

        private EscapedChar(String escapedChar) {
            this.escapedChar = escapedChar;
        }

        @Override
        public String toString() {
            return this.escapedChar;
        }
    }

    private static String escapeKey(String key) {
        if (key == null) {
            return "";
        }

        int pos;
        int len = key.length();
        StringBuilder builder = new StringBuilder();
        for (pos = 0; pos < len; pos++) {
            char ch = key.charAt(pos);
            EscapedChar escapedChar;
            switch (ch) {
                case '\t':
                    escapedChar = EscapedChar.TAB;
                    break;
                case '\n':
                    escapedChar = EscapedChar.NEWLINE;
                    break;
                case '\r':
                    escapedChar = EscapedChar.RETURN;
                    break;
                case '&':
                    escapedChar = EscapedChar.AMP;
                    break;
                case '"':
                    escapedChar = EscapedChar.QUOT;
                    break;
                case '<':
                    escapedChar = EscapedChar.LT;
                    break;
                case '>':
                    escapedChar = EscapedChar.GT;
                    break;
                default:
                    escapedChar = null;
                    break;
            }

            if (escapedChar != null) {
                builder.append(escapedChar.toString());
            } else {
                builder.append(ch);
            }
        }

        return builder.toString();
    }
}
