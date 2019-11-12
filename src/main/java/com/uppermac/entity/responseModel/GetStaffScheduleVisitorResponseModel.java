package com.uppermac.entity.responseModel;

public class GetStaffScheduleVisitorResponseModel {
	
	
	private String visitDate;		  	//访问日期
	private String visitTime;       	//访问时间
	private String orgCode;				//大楼编号
	private String startDate;			//开始时间
	private String endDate; 			//结束时间
	private String userRealName;		//访问者名字
	private String soleCode;			//访客唯一标识码
	private String vistorRealName;		//被访者名字
	private String province;			//省
	private String city;				//市
	private String dateType;			//时间类型
	private String visitId;				//访问ID编号
	private String photo;				//照片
	private String visitorIdNO;			//被访者证件号码
	private String userIdNO;			//访客证件号
	
	
	
	

	public String getUserIdNO() {
		return userIdNO;
	}

	public void setUserIdNO(String userIdNO) {
		this.userIdNO = userIdNO;
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

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getUserRealName() {
		return userRealName;
	}

	public void setUserRealName(String userRealName) {
		this.userRealName = userRealName;
	}

	public String getSoleCode() {
		return soleCode;
	}

	public void setSoleCode(String soleCode) {
		this.soleCode = soleCode;
	}

	public String getVistorRealName() {
		return vistorRealName;
	}

	public void setVistorRealName(String vistorRealName) {
		this.vistorRealName = vistorRealName;
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

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}
}
