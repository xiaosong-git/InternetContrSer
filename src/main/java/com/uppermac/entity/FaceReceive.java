package com.uppermac.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(FaceReceiveKey.class)
@Table(name ="tb_facereceive")
public class FaceReceive {

	@Id
	@Column(name="faceip")
	private String faceIp;
	
	@Id
	@Column(name="idcard")
	private String idCard;
	
	@Id
	@Column(name="usertype")
	private String userType;
	
	@Column(name="receivetime")
	private String receiveTime;
	
	@Column(name="receiveflag")
	private String receiveFlag;

	@Column(name="visitoruuid")
	private String visitorUUID;
	
	@Id
	@Column(name="opera")
	private String opera;

	@Column(name="username")
	private String userName;
	
	
	
	public String getVisitorUUID() {
		return visitorUUID;
	}

	public void setVisitorUUID(String visitorUUID) {
		this.visitorUUID = visitorUUID;
	}

	public String getFaceIp() {
		return faceIp;
	}

	public void setFaceIp(String faceIp) {
		this.faceIp = faceIp;
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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getReceiveFlag() {
		return receiveFlag;
	}

	public void setReceiveFlag(String receiveFlag) {
		this.receiveFlag = receiveFlag;
	}

	public String getOpera() {
		return opera;
	}

	public void setOpera(String opera) {
		this.opera = opera;
	}

}
