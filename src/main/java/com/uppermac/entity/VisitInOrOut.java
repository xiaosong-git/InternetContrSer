package com.uppermac.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tb_jc")
public class VisitInOrOut {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)  
	private Integer id;
	
	private String pospCode;	//上位机编号
	
	private String orgCode;		//大楼编号
	
	private int visitId;		//访客记录id
	
	private String inOrOut;		//出入类型
	
	private String visitDate;	//日期
	
	private String visitTime;	//时间
	
	private String termNo;		//终端号
	
	private String holdTermNo;	//手持设备号
	
	private String operator;	//操作员
	
	private String isSendFlag; 	//上传成功标记
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPospCode() {
		return pospCode;
	}
	public void setPospCode(String pospCode) {
		this.pospCode = pospCode;
	}
	public String getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	public int getVisitId() {
		return visitId;
	}
	public void setVisitId(int visitId) {
		this.visitId = visitId;
	}
	public String getInOrOut() {
		return inOrOut;
	}
	public void setInOrOut(String inOrOut) {
		this.inOrOut = inOrOut;
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
	public String getTermNo() {
		return termNo;
	}
	public void setTermNo(String termNo) {
		this.termNo = termNo;
	}
	public String getHoldTermNo() {
		return holdTermNo;
	}
	public void setHoldTermNo(String holdTermNo) {
		this.holdTermNo = holdTermNo;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getIsSendFlag() {
		return isSendFlag;
	}
	public void setIsSendFlag(String isSendFlag) {
		this.isSendFlag = isSendFlag;
	}
	@Override
	public String toString() {
		return "VisitInOrOut [id=" + id + ", pospCode=" + pospCode + ", orgCode=" + orgCode + ", visitId=" + visitId
				+ ", inOrOut=" + inOrOut + ", visitDate=" + visitDate + ", visitTime=" + visitTime + ", termNo="
				+ termNo + ", holdTermNo=" + holdTermNo + ", operator=" + operator + ", isSendFlag=" + isSendFlag + "]";
	}
	
	
	
}
