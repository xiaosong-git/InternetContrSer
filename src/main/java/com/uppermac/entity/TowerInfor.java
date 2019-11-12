package com.uppermac.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_dl")
public class TowerInfor {

	@Id
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "orgid")
	private String orgId;				//大楼编号
	
	@Column(name = "orgname")
	private String orgName;				//大楼名称
	
	@Column(name = "pospcode")
	private String pospCode;			//上位机编号
	
	@Column(name = "nettype")
	private String netType;				//联网方式（1-外网  2-内网）
	
	@Column(name = "facecomparescope")
	private String faceComparesCope;	//人脸比对阀值
	
	@Column(name = "devicetype")
	private String deviceType;			//设备配置方式（1-二维码，2-人像识别，3-二维码+人像识别，4-二维码或人像识别）
	
	@Column(name = "visitorchecktype")
	private String visitorCheckType;	//访客进出验证方式（FACE-人脸验证、QRCODE-二维码验证、FQ-人像与二维码共用）
	
	@Column(name = "staffchecktype")
	private String staffCheckType;		//员工进出验证方式（FACE-人脸验证、QRCODE-二维码验证、FQ-人像与二维码共用）
	
	@Column(name = "key")
	private String key;					//秘钥
	
	@Column(name = "deviceselect")
	private String deviceSelect;		//设备选型
	
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public int getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	public String getPospCode() {
		return pospCode;
	}
	public void setPospCode(String pospCode) {
		this.pospCode = pospCode;
	}
	public String getNetType() {
		return netType;
	}
	public void setNetType(String netType) {
		this.netType = netType;
	}
	public String getFaceComparesCope() {
		return faceComparesCope;
	}
	public void setFaceComparesCope(String faceComparesCope) {
		this.faceComparesCope = faceComparesCope;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getDeviceSelect() {
		return deviceSelect;
	}
	public void setDeviceSelect(String deviceSelect) {
		this.deviceSelect = deviceSelect;
	}
	public String getVisitorCheckType() {
		return visitorCheckType;
	}
	public void setVisitorCheckType(String visitorCheckType) {
		this.visitorCheckType = visitorCheckType;
	}
	public String getStaffCheckType() {
		return staffCheckType;
	}
	public void setStaffCheckType(String staffCheckType) {
		this.staffCheckType = staffCheckType;
	}
	
}
