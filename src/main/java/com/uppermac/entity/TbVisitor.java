package com.uppermac.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tb_fw")
public class TbVisitor {

	@Id
	@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
	@Column(name="id",length = 33)
	private String staffId;
	//private static final long serialVersionUID = 1L;
	private String createby;		// 创建者
	private String updateby;		// 更新者
	private String updatedate;		// 更新时间
	private String orgcode;			// 大楼编号
	private String visitdate;		// 访问日期
	private String visittime;		// 访问时间
	private String startdate;		// 开始时间
	private String enddate;			// 结束时间
	private String userrealname;	// 访客姓名
	
	@Column(name ="idno")
	private String idNO;			// 证件号
	private String solecode;		// 访客标识码
	private String vistorrealname;	// 被访人姓名
	private String province;		// 省
	private String city;			// 市
	private String datetype;		// 时间类型
	private String visitId;			// 访客记录ID
	private String issued;			// 是否下发白名单成功0-成功 1-失败
	private String delflag;			// 删除标志0-成功 1-失败
	private String isposted;		//是否有下发过设备
	private String prestartdate;	//预约时间提前半小时有效
	
	@Column(name ="visitoridno")
	private String visitorIdNO;			//被访者证件号码
	
	@Transient
	private String photo;			//访客照片
	
	@Column(name ="picid")
	private String picID;
	
	public String getPrestartdate() {
		return prestartdate;
	}
	public void setPrestartdate(String prestartdate) {
		this.prestartdate = prestartdate;
	}
	public String getIsposted() {
		return isposted;
	}
	public void setIsposted(String isposted) {
		this.isposted = isposted;
	}
	public String getVisitorIdNO() {
		return visitorIdNO;
	}
	public void setVisitorIdNO(String visitorIdNO) {
		this.visitorIdNO = visitorIdNO;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getIdNO() {
		return idNO;
	}
	public void setIdNO(String idNO) {
		this.idNO = idNO;
	}
	public String getIssued() {
		return issued;
	}
	public void setIssued(String issued) {
		this.issued = issued;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getStaffId() {
		return staffId;
	}
	public String getCreateby() {
		return createby;
	}
	public void setCreateby(String createby) {
		this.createby = createby;
	}
	public String getUpdateby() {
		return updateby;
	}
	public void setUpdateby(String updateby) {
		this.updateby = updateby;
	}
	public String getUpdatedate() {
		return updatedate;
	}
	public void setUpdatedate(String updatedate) {
		this.updatedate = updatedate;
	}
	public String getDelflag() {
		return delflag;
	}
	public void setDelflag(String delflag) {
		this.delflag = delflag;
	}
	public String getOrgcode() {
		return orgcode;
	}
	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}
	public String getVisitdate() {
		return visitdate;
	}
	public void setVisitdate(String visitdate) {
		this.visitdate = visitdate;
	}
	public String getVisittime() {
		return visittime;
	}
	public void setVisittime(String visittime) {
		this.visittime = visittime;
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
	public String getUserrealname() {
		return userrealname;
	}
	public void setUserrealname(String userrealname) {
		this.userrealname = userrealname;
	}
	public String getSolecode() {
		return solecode;
	}
	public void setSolecode(String solecode) {
		this.solecode = solecode;
	}
	public String getVistorrealname() {
		return vistorrealname;
	}
	public void setVistorrealname(String vistorrealname) {
		this.vistorrealname = vistorrealname;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDatetype() {
		return datetype;
	}
	public void setDatetype(String datetype) {
		this.datetype = datetype;
	}
	public String getVisitId() {
		return visitId;
	}
	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}
	
	
	public String getPicID() {
		return picID;
	}
	public void setPicID(String picID) {
		this.picID = picID;
	}
	@Override
	public String toString() {
		return "TbStaff [staffId=" + staffId + ", createby=" + createby + ", updateby=" + updateby + ", updatedate="
				+ updatedate + ", delflag=" + delflag + ", orgcode=" + orgcode + ", visitdate=" + visitdate
				+ ", visittime=" + visittime + ", startdate=" + startdate + ", enddate=" + enddate + ", userrealname="
				+ userrealname + ", solecode=" + solecode + ", vistorrealname=" + vistorrealname + ", province="
				+ province + ", city=" + city + ", datetype=" + datetype + ", visitId=" + visitId + "]";
	}
	
}
