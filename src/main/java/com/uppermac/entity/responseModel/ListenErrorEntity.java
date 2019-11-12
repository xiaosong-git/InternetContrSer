package com.uppermac.entity.responseModel;

public class ListenErrorEntity {

	private String dateTime;	//发送时间
	
	private String orgId;		//大楼编号
	
	private String pospCode;	//上位机编号
	
	private String errorType;	//错误类型
	
	private String deviceType;	//设备类型
	
	private String deviceIP;	//设备ip地址
	
	private String context;		//详细通信信息

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getPospCode() {
		return pospCode;
	}

	public void setPospCode(String pospCode) {
		this.pospCode = pospCode;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceIP() {
		return deviceIP;
	}

	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@Override
	public String toString() {
		return "ListenErrorEntity [dateTime=" + dateTime + ", orgId=" + orgId + ", pospCode=" + pospCode
				+ ", deviceType=" + deviceType + ", deviceIP=" + deviceIP + ", context=" + context + "]";
	}
	
	
}
