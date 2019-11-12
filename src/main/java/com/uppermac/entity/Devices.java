package com.uppermac.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_devices")
public class Devices {
	@Id
	@Column(name ="deviceid")
	private String deviceId;			//设备标识(人脸-F，继电器-E，二维码-Q,上位机-S)
	
	@Column(name ="devicename")
	private String deviceName;			//设备名称
	
	@Column(name ="deviceip")
	private String deviceIp;			//设备IP地址
			
	@Column(name ="deviceport")
	private String devicePort;			//设备端口
	
	@Column(name ="fq_turnover")
	private String FQ_turnover;			//进出标识(针对人脸设备及二维码设备)
	
	@Column(name ="e_out")	
	private String E_out;				//继电器输出口(4路或24路)
	
	@Column(name ="status")
	private String status;				//设备状态（running-运行中、free-闲置、bad-损坏）

	@Column(name ="expt1")
	private String expt1;				//拓展字段1  设备选型
	
	@Column(name ="devicetype")
	private String deviceType;			//设备类型

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getDevicePort() {
		return devicePort;
	}

	public void setDevicePort(String devicePort) {
		this.devicePort = devicePort;
	}

	public String getFQ_turnover() {
		return FQ_turnover;
	}

	public void setFQ_turnover(String fQ_turnover) {
		FQ_turnover = fQ_turnover;
	}

	public String getE_out() {
		return E_out;
	}

	public void setE_out(String e_out) {
		E_out = e_out;
	}

	public String getExpt1() {
		return expt1;
	}

	public void setExpt1(String expt1) {
		this.expt1 = expt1;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	
	
}