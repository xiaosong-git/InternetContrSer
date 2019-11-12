package com.uppermac.entity.responseModel;

import java.util.List;

public class CameraObjectResponse {
	
	private String result;
	
	private String message;

	private List<CameraInfos> data;
	

	public List<CameraInfos> getData() {
		return data;
	}

	public void setData(List<CameraInfos> data) {
		this.data = data;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
