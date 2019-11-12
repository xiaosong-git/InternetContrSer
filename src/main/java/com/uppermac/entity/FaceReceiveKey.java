package com.uppermac.entity;

import java.io.Serializable;

public class FaceReceiveKey implements Serializable{

	private String faceIp;
	
	private String idCard;
	
	private String userType;
	
	private String opera;
	
	

	public String getFaceIp() {
		return faceIp;
	}

	public void setFaceIp(String faceIp) {
		this.faceIp = faceIp;
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

	public String getOpera() {
		return opera;
	}

	public void setOpera(String opera) {
		this.opera = opera;
	}
	
	
}
