package com.uppermac.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tb_devicerelated")
public class DeviceRelated {

	@Id
	@Column(name="relatedid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer relatedId;		//编号
	
	@Column(name="pospip")
	private String pospIP;			//上位机IP
	
	@Column(name="faceip")
	private String faceIP;			//人脸设备IP
	
	@Column(name="qrcodeip")
	private String QRCodeIP;		//二维码识别器IP
	
	@Column(name="relayip")
	private String relayIP;			//继电器IP
	
	@Column(name="relayport")
	private String relayPort;		//继电器端口
	
	@Column(name="relayout")
	private String relayOUT;		//继电器电源输出口
	
	@Column(name="contralfloor")
	private String contralFloor;	//关联楼层
	
	@Column(name="channelno")
	private String channelNO;		//通道编号
	
	@Column(name="turnover")
	private String turnOver;		//进出标识

	private String expt1;			//拓展字段1
	
	private String expt2;			//拓展字段2
	
	public Integer getRelatedId() {
		return relatedId;
	}

	public void setRelatedId(Integer relatedId) {
		this.relatedId = relatedId;
	}

	public String getPospIP() {
		return pospIP;
	}

	public void setPospIP(String pospIP) {
		this.pospIP = pospIP;
	}

	
	public String getFaceIP() {
		return faceIP;
	}

	public void setFaceIP(String faceIP) {
		this.faceIP = faceIP;
	}

	public String getQRCodeIP() {
		return QRCodeIP;
	}

	public void setQRCodeIP(String qRCodeIP) {
		QRCodeIP = qRCodeIP;
	}

	public String getRelayIP() {
		return relayIP;
	}

	public void setRelayIP(String relayIP) {
		this.relayIP = relayIP;
	}

	public String getRelayPort() {
		return relayPort;
	}

	public void setRelayPort(String relayPort) {
		this.relayPort = relayPort;
	}

	public String getRelayOUT() {
		return relayOUT;
	}

	public void setRelayOUT(String relayOUT) {
		this.relayOUT = relayOUT;
	}

	public String getContralFloor() {
		return contralFloor;
	}

	public void setContralFloor(String contralFloor) {
		this.contralFloor = contralFloor;
	}

	public String getChannelNO() {
		return channelNO;
	}

	public void setChannelNO(String channelNO) {
		this.channelNO = channelNO;
	}

	public String getTurnOver() {
		return turnOver;
	}

	public void setTurnOver(String turnOver) {
		this.turnOver = turnOver;
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
	
	
}
