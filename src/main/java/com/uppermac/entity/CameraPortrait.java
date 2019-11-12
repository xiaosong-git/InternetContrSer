package com.uppermac.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/*
 * 人像识别白名单
 * 
 */
@Entity
@Table(name="tb_bmd")
public class CameraPortrait {

	@Id
	@Column(name = "portraitid")
	private String portraitId;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "idcard")
	private String idCard;
	
	@Column(name = "persontype")
	private String personType; 
	
	@Column(name = "picture")
	private String picture;
	
	@Column(name = "companyname")
	private String companyName;
	
	@Column(name = "companyid")
	private int companyId;

	public String getPortraitId() {
		return portraitId;
	}

	public void setPortraitId(String portraitId) {
		this.portraitId = portraitId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getPersonType() {
		return personType;
	}

	public void setPersonType(String personType) {
		this.personType = personType;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

}
