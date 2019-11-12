package com.uppermac.utils;

/**
 * Created by Administrator on 2017/10/30.
 */
public class ThirdResponseObj {


    private String code;
    private String responseEntity;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(String responseEntity) {
        this.responseEntity = responseEntity;
    }

	@Override
	public String toString() {
		return "ThirdResponseObj [code=" + code + ", responseEntity=" + responseEntity + ", getCode()=" + getCode()
				+ ", getResponseEntity()=" + getResponseEntity() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}


    
}
