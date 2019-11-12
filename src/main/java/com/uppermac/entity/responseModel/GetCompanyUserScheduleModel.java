package com.uppermac.entity.responseModel;

import java.util.List;

import com.uppermac.entity.TbCompanyUser;

public class GetCompanyUserScheduleModel {

	private GetStaffScheduleVerifyResponseModel verify;
	private List<TbCompanyUser> data;
	public GetStaffScheduleVerifyResponseModel getVerify() {
		return verify;
	}
	public void setVerify(GetStaffScheduleVerifyResponseModel verify) {
		this.verify = verify;
	}
	public List<TbCompanyUser> getData() {
		return data;
	}
	public void setData(List<TbCompanyUser> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "GetCompanyUserScheduleModel [verify=" + verify + ", data=" + data + "]";
	}
	
	
}
