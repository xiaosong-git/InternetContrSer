package com.uppermac.service;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.uppermac.config.HCNetSDK;
import com.uppermac.entity.TbCompanyUser;
import com.uppermac.entity.TbVisitor;

public interface HCNetSDKService {


	public boolean setCardInfo(String deviceIP,int dwEmployeeNo,String strCardName,String strCardNo,String isdel) throws UnsupportedEncodingException;
	
	public boolean setFace(String deviceIP,String strCardNo,TbCompanyUser companyUser) throws UnsupportedEncodingException;
	
	public boolean delFace(String deviceIP,String idCardNo) throws UnsupportedEncodingException;
	
	public boolean getCardInfo(String deviceIP) throws UnsupportedEncodingException;
	
	public boolean setVisitorCard(String deviceIP,String isdel,TbVisitor visitor);

	public boolean setVisitorFace(String deviceIP,TbVisitor visitor);
	
	public int initAndLogin(String hcDeviceIP);
	
	public void sendAccessRecord(String deviceIP);
	
	public boolean sendToIPC(String hcDeviceIP,File picture,File picAppendData,TbCompanyUser companyUser,TbVisitor visitor);
	
	public boolean delIPCpicture(String type,String picID);
	
	public void createIPCAlarm(String hcDeviceIP);
	
	public boolean getIPCRecord(String deviceIP,String dayInfo);
	
}
