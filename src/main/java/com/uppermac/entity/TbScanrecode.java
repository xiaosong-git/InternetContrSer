package com.uppermac.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tb_sm")
public class TbScanrecode {

	@Id
	@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
	@Column(name="id",length = 64)
	private String scanId;         //刷卡记录编号
	

	private String solecode;		// 刷卡人员身份识别码
	private String staffname;		// 刷卡人员姓名
	private String staffidnumber;	// 刷卡人员身份证号
	private String phone;		    // 刷卡人员手机号码
	private String photopath;		// 刷卡人员头像路径
	private String datetype;		// 授权时间类型
	private String startdate;		// 授权有效开始时间
	private String enddate;		    // 授权有效结束时间
	private String turnover;		// 进出标志
	private String deviceid;		// 进出设备ID
	private String devicename;		// 进出设备名称
	private String orgcode;			//大楼标号
	private String checktype;       //校验方式
	private String visitorfacedel;  //访客人脸识别白名单删除标记
	
	@Column(name="persontype")
	private String personType;		//人员类型(员工staff/访客visitor)
	
	@Column(name="visitdate")
	private String visitDate;		//访问日期(yyyy-mm-dd)
	@Column(name="visittime")
	private String visitTime;       //访问时间(hh:mm:ss)

	
	
	
	public String getVisitorfacedel() {
		return visitorfacedel;
	}
	public void setVisitorfacedel(String visitorfacedel) {
		this.visitorfacedel = visitorfacedel;
	}
	public String getPersonType() {
		return personType;
	}
	public void setPersonType(String personType) {
		this.personType = personType;
	}
	public String getVisitDate() {
		return visitDate;
	}
	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}
	public String getVisitTime() {
		return visitTime;
	}
	public void setVisitTime(String visitTime) {
		this.visitTime = visitTime;
	}
	public String getScanId() {
		return scanId;
	}
	public void setScanId(String scanId) {
		this.scanId = scanId;
	}
	
	
	
	public String getSolecode() {
		return solecode;
	}
	public void setSolecode(String solecode) {
		this.solecode = solecode;
	}
	public String getStaffname() {
		return staffname;
	}
	public void setStaffname(String staffname) {
		this.staffname = staffname;
	}
	public String getStaffidnumber() {
		return staffidnumber;
	}
	public void setStaffidnumber(String staffidnumber) {
		this.staffidnumber = staffidnumber;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhotopath() {
		return photopath;
	}
	public void setPhotopath(String photopath) {
		this.photopath = photopath;
	}
	public String getDatetype() {
		return datetype;
	}
	public void setDatetype(String datetype) {
		this.datetype = datetype;
	}
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getTurnover() {
		return turnover;
	}
	public void setTurnover(String turnover) {
		this.turnover = turnover;
	}
	public String getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}
	public String getDevicename() {
		return devicename;
	}
	public void setDevicename(String devicename) {
		this.devicename = devicename;
	}
	public String getOrgcode() {
		return orgcode;
	}
	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}
	public String getChecktype() {
		return checktype;
	}
	public void setChecktype(String checktype) {
		this.checktype = checktype;
	}
	
	
}
