package com.uppermac.data;

public class ResponseData {
	
	private String code;
	private String message;

	
	public ResponseData(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	
}
