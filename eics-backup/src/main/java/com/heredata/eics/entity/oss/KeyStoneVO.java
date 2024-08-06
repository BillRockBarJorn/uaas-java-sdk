package com.heredata.eics.entity.oss;



/**
 * 
 * <p>
 * Title: KeyStoneVO
 * </p>
 * <p>
 * Description: KeyStone认证VO
 * </p>
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * <p>
 * Company: SI-TECH
 * </p>
 * 
 * @author huojla
 * @version 1.0
 * @createtime 2014-7-10 下午3:18:40
 * 
 */
public class KeyStoneVO extends ResultSet {
	// keystone认证服务Url 例：http://172.21.2.100:35357/v2.0
	private String keystone_auth_url;
	
	//域名称
	private String project_domain_name;
	
	//域名称
		private String user_domain_name;
	// 租户名称
	private String tenant_name;
	// 认证用户名
	private String username;
	// 认证密码
	private String password;
	//region (地区) 区别不同的swift服务地址
	private String region;
	// 用户keystone token
	private String tokenId;
	// 获取应用的连接地址
	private String swiftPublicURL;

	public String getKeystone_auth_url() {
		return keystone_auth_url;
	}

	public void setKeystone_auth_url(String keystone_auth_url) {
		this.keystone_auth_url = keystone_auth_url;
	}

	public String getTenant_name() {
		return tenant_name;
	}

	public void setTenant_name(String tenant_name) {
		this.tenant_name = tenant_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getSwiftPublicURL() {
		return swiftPublicURL;
	}

	public void setSwiftPublicURL(String swiftPublicURL) {
		this.swiftPublicURL = swiftPublicURL;
	}

	public String getProject_domain_name() {
		return project_domain_name;
	}

	public void setProject_domain_name(String project_domain_name) {
		this.project_domain_name = project_domain_name;
	}

	public String getUser_domain_name() {
		return user_domain_name;
	}

	public void setUser_domain_name(String user_domain_name) {
		this.user_domain_name = user_domain_name;
	}
	
}
