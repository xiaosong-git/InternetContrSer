package com.uppermac.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tb_sb")
public class TbDevice {

	
	@Id
	@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
	@Column(name="id",length = 33)
	private String deviceId;
	private String devicename;		// 设备名称
	private String turnover;		// 设备进出标志
	private String orgcode;			// 大楼编号
	private String deviceip;		// 设备ip地址
	private String deviceport;		// 设备端口
	private String reader;		    // 读头识别编号
	
	@Column(name = "facerecogip")
	private String faceRecogIp;		//人像识别IP地址
	
	public String getFaceRecogIp() {
		return faceRecogIp;
	}
	public void setFaceRecogIp(String faceRecogIp) {
		this.faceRecogIp = faceRecogIp;
	}
	public String getReader() {
		return reader;
	}
	public void setReader(String reader) {
		this.reader = reader;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getDevicename() {
		return devicename;
	}
	public void setDevicename(String devicename) {
		this.devicename = devicename;
	}
	public String getTurnover() {
		return turnover;
	}
	public void setTurnover(String turnover) {
		this.turnover = turnover;
	}
	public String getOrgcode() {
		return orgcode;
	}
	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}

	public String getDeviceip() {
		return deviceip;
	}
	public void setDeviceip(String deviceip) {
		this.deviceip = deviceip;
	}
	public String getDeviceport() {
		return deviceport;
	}
	public void setDeviceport(String deviceport) {
		this.deviceport = deviceport;
	}
	@Override
	public String toString() {
		return "TbDevice [deviceId=" + deviceId + ", devicename=" + devicename + ", turnover=" + turnover + ", orgcode="
				+ orgcode  + ", deviceip=" + deviceip + ", deviceport=" + deviceport + "]";
	}
	
	
}
