package com.heredata.hos.model;

import lombok.Data;

/**
 * <p>Title: SetBucketPolicyRequest</p>
 * <p>Description: 设置账户策略请求实体
 *                  默认情况下资源（桶和对象）都是私有的，只有资源拥有者可以访问资源，
 *                  其他用户在未经授权的情况下均无HOS访问权限。
 *                  通过编写访问策略向其他帐户或者UAAS用户授予资源的控制权限。</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:29
 */
@Data
public class SetBucketPolicyRequest extends GenericRequest {
    /**
     * 策略Json字符串
     */
    private String policyText;

    /**
     * @param bucketName 桶名称
     * @param policyText 策略字符串
     */
    public SetBucketPolicyRequest(String bucketName, String policyText) {
        super(bucketName);
        this.policyText = policyText;
    }
}
