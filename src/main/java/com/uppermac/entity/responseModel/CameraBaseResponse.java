package com.uppermac.entity.responseModel;

public class CameraBaseResponse {

	private String result;
	
	private String message;

	
	
	public CameraBaseResponse() {
		super();
	}

	public CameraBaseResponse(String result, String message) {
		super();
		this.result = result;
		this.message = message;
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
