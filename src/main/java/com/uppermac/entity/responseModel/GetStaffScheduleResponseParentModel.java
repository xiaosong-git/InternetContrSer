package com.uppermac.entity.responseModel;

public class GetStaffScheduleResponseParentModel {

	/*
	 * 
	 * @verify 结果
	 * @data   数据
	 * 
	 */
	private GetStaffScheduleVerifyResponseModel verify;
	private GetStaffScheduleDataResponseModel data;

	public GetStaffScheduleVerifyResponseModel getVerify() {
		return verify;
	}

	public void setVerify(GetStaffScheduleVerifyResponseModel verify) {
		this.verify = verify;
	}

	public GetStaffScheduleDataResponseModel getData() {
		return data;
	}

	public void setData(GetStaffScheduleDataResponseModel data) {
		this.data = data;
	}
}
