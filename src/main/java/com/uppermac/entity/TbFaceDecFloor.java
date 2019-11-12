package com.uppermac.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tb_facefloor")
public class TbFaceDecFloor {
	
	@Id
	@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
	@Column(name="id",length = 33)
	private String id;
	
	@Column(name="faceip")
	private String faceip;
	
	@Column(name="contralfloor")
	private String contralFloor;
	
	@Column(name="status")
	private String status;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFaceip() {
		return faceip;
	}
	public void setFaceip(String faceip) {
		this.faceip = faceip;
	}
	public String getContralFloor() {
		return contralFloor;
	}
	public void setContralFloor(String contralFloor) {
		this.contralFloor = contralFloor;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
