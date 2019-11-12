package com.uppermac.service;

import java.util.List;

import com.uppermac.entity.TbCompanyUser;

public interface TbCompanyUserService {

	public List<TbCompanyUser> findAll();
	
	public void save(TbCompanyUser companyUser);
	
	public TbCompanyUser findByNameAndIdNO(String name,String idCard,String status);
	
	TbCompanyUser findByNameAndCid(String name,int companyId);
	
	public void update(TbCompanyUser companyUser);
	
	public List<TbCompanyUser> findByIssued(String issued);
	
	public TbCompanyUser findOne(Integer id);
	
	List<TbCompanyUser> findByStatus(String currentStatus);
	
	/**
	 * 
	 * @param today	今日日期  yyyy-MM-dd
	 * @return
	 */
	List<TbCompanyUser> findBeforeToDay(String today);
	
	List<TbCompanyUser> findFailDel();
	
	void deleteAll();
	
	void deleteOne(TbCompanyUser user);
	
	/**
	 * 
	 * @param deviceIp	设备IP地址
	 * @param user		下发员工
	 * @param photo		下发员工的照片
	 * @return
	 */
	boolean sendWhiteList(String deviceIp, TbCompanyUser user, String photo);
	
	boolean sendDelWhiteList(String deviceIp, String name, String idNO);
}
