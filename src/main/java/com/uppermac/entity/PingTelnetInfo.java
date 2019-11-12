package com.uppermac.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@IdClass(PingTelnetInfoKey.class)
@Table(name ="tb_ptinfo")
public class PingTelnetInfo {

	@Id
	@Column(name="devicename")
	private String deviceName;	//设备名称
	
	@Id
	@Column(name="deviceip")
	private String deviceIP;
	
	@Transient
	private String orgCode;
	
	@Column(name="pingstatus")
	private String pingStatus;	//ping网络状态（normal/error）
	private double pingavg;		//Ping平均速度（50ms）
	private String pingloss;	//丢包率	（0%）
	
	@Column(name="telstatus")
	private String telStatus;	//telnet状态	（normal/error）

	private String cpu;			//上位机CPU使用率
	
	private String memory;		//上位机内存使用率
	
	@Column(name="freshtime")
	private String freshTime;	//刷新时间
	
	private String expt1;	//拓展字段
	private String expt2;	//拓展字段


	
	
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	
	
	public String getDeviceIP() {
		return deviceIP;
	}

	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}

	public String getPingStatus() {
		return pingStatus;
	}

	public void setPingStatus(String pingStatus) {
		this.pingStatus = pingStatus;
	}

	public double getPingavg() {
		return pingavg;
	}

	public void setPingavg(double pingavg) {
		this.pingavg = pingavg;
	}

	public String getPingloss() {	
		return pingloss;
	}

	public void setPingloss(String pingloss) {
		this.pingloss = pingloss;
	}

	public String getTelStatus() {
		return telStatus;
	}

	public void setTelStatus(String telStatus) {
		this.telStatus = telStatus;
	}

	public String getExpt1() {
		return expt1;
	}

	public void setExpt1(String expt1) {
		this.expt1 = expt1;
	}

	public String getExpt2() {
		return expt2;
	}

	public void setExpt2(String expt2) {
		this.expt2 = expt2;
	}

	public String getFreshTime() {
		return freshTime;
	}

	public void setFreshTime(String freshTime) {
		this.freshTime = freshTime;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	
	
}
