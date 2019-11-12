package com.uppermac.entity.responseModel;

public class GetStaffScheduleResponseSingleParentModel {

	private GetStaffScheduleVerifyResponseModel verify;
	private GetStaffScheduleVisitorResponseModel data;

	public GetStaffScheduleVerifyResponseModel getVerify() {
		return verify;
	}

	public void setVerify(GetStaffScheduleVerifyResponseModel verify) {
		this.verify = verify;
	}

	public GetStaffScheduleVisitorResponseModel getData() {
		return data;
	}

	public void setData(GetStaffScheduleVisitorResponseModel data) {
		this.data = data;
	}
}
