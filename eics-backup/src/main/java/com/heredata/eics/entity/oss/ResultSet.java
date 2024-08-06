package com.heredata.eics.entity.oss;

public class ResultSet {
	public Boolean isSuccess = false;
	public String log = "";

	public Boolean getIsSuccess() {
		return isSuccess;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

}
