package com.uppermac.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tb_accessrecord")
public class AccessRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "orgcode")
	private String orgCode;		//大楼编号
	
	@Column(name = "pospcode")
	private String pospCode;	//上位机编号
	
	@Column(name = "scandate")
	private String scanDate;	//设备扫描日期：yyyy:MM:dd
	
	@Column(name = "scantime")
	private String scanTime;	//设备扫描时间：HH:mm:ss
	
	@Column(name = "inorout")
	private String inOrOut;		//进出类型：in/out
	
	@Column(name = "outnumber")
	private String outNumber;	//通道编号
	
	@Column(name = "devicetype")
	private String deviceType;	//通行设备的类型：QRCODE/FACE
	
	@Column(name = "deviceip")
	private String deviceIp;	//设备的IP地址
	
	@Column(name = "usertype")
	private String userType;	//通行人员类型：staff/visitor
	
	@Column(name = "username")
	private String userName;	//通行人员名字
	
	@Column(name = "idcard")
	private String idCard;		//通行人员证件号
	
	@Column(name = "issendflag")
	private String isSendFlag;	//发送记录的标识：已发送(T)/未发送(F)
	
	@Column(name = "expt1")
	private String expt1;		//卡号
	
	@Column(name = "expt2")
	private String expt2;		//拓展字段2

	@Column(name = "expt3")
	private String expt3;		//拓展字段2
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getPospCode() {
		return pospCode;
	}

	public void setPospCode(String pospCode) {
		this.pospCode = pospCode;
	}

	public String getScanDate() {
		return scanDate;
	}

	public void setScanDate(String scanDate) {
		this.scanDate = scanDate;
	}

	public String getScanTime() {
		return scanTime;
	}

	public void setScanTime(String scanTime) {
		this.scanTime = scanTime;
	}

	public String getInOrOut() {
		return inOrOut;
	}

	public void setInOrOut(String inOrOut) {
		this.inOrOut = inOrOut;
	}

	
	
	public String getOutNumber() {
		return outNumber;
	}

	public void setOutNumber(String outNumber) {
		this.outNumber = outNumber;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getIsSendFlag() {
		return isSendFlag;
	}

	public void setIsSendFlag(String isSendFlag) {
		this.isSendFlag = isSendFlag;
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

	public String getExpt3() {
		return expt3;
	}

	public void setExpt3(String expt3) {
		this.expt3 = expt3;
	}
	
	
}
