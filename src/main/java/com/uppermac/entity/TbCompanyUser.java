package com.uppermac.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
/**
 * 	公司员工表
 * @author Administrator
 *
 */
@Entity
@Table(name = "tb_yg")
public class TbCompanyUser {
	
	@Id
	@Column(name="companyuserid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)  
	private Integer companyUserId;
	
	@Column(name="companyid")
	private int companyId;
	
	@Column(name="sectionid")
	private int sectionId;
	
	@Column(name="userid")
	private int userId;
	
	@Column(name="username")
	private String userName;
	
	@Column(name="createdate")
	private String createDate;
	
	@Column(name="createtime")
	private String createTime;
	
	@Column(name="roletype")
	private String roleType;
	
	@Column(name="status")
	private String status;
	
	@Column(name ="idtype")
	private String idType;
	
	@Column(name="idno")
	private String idNO;
	
	
	@Column(name="companyfloor")
	private String companyFloor;
	
	@Column(name="currentstatus")
	private String currentStatus;
	
	@Transient
	private String photo;
	
	@Column(name="issued")
	private String issued;
	
	@Column(name="isdel")
	private String isdel;
	
	@Column(name="idfrontimgurl")
	private String idFrontImgUrl;
	
	
	
	public String getIdFrontImgUrl() {
		return idFrontImgUrl;
	}
	public void setIdFrontImgUrl(String idFrontImgUrl) {
		this.idFrontImgUrl = idFrontImgUrl;
	}
	public String getIsdel() {
		return isdel;
	}
	public void setIsdel(String isdel) {
		this.isdel = isdel;
	}
	public String getIssued() {
		return issued;
	}
	public void setIssued(String issued) {
		this.issued = issued;
	}
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	public String getCompanyFloor() {
		return companyFloor;
	}
	public void setCompanyFloor(String companyFloor) {
		this.companyFloor = companyFloor;
	}
	
	
	public Integer getCompanyUserId() {
		return companyUserId;
	}
	public void setCompanyUserId(Integer companyUserId) {
		this.companyUserId = companyUserId;
	}
	public int getCompanyId() {
		return companyId;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	public int getSectionId() {
		return sectionId;
	}
	public void setSectionId(int sectionId) {
		this.sectionId = sectionId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNO() {
		return idNO;
	}
	public void setIdNO(String idNO) {
		this.idNO = idNO;
	}
	@Override
	public String toString() {
		return ",companyId=" + companyId + ", sectionId=" + sectionId + ", userId="
				+ userId + ", userName=" + userName + ", createDate=" + createDate + ", createTime=" + createTime
				+ ", roleType=" + roleType + ", status=" + status + ", idType=" + idType + ", idNO=" + idNO
				+ ", companyFloor=" + companyFloor + ", currentStatus=" + currentStatus + ", issued=" + issued
				+ ", isdel=" + isdel + ", idFrontImgUrl=" + idFrontImgUrl + "]";
	}

	
	
}
