package com.uppermac.service;

import java.util.List;

import com.uppermac.entity.TowerInfor;

public interface TowerInforService {

	List<TowerInfor> findAll();
	
	TowerInfor findone(Integer id);
	
	TowerInfor update(TowerInfor towerInfor);
	
	TowerInfor save(TowerInfor towerInfor);
	
	String findOrgId();
	
	String findPospCode();
	
	String findFaceComparesCope();
	
	String findDeviceType();
	
	String findKey();
	
	List<String> deviceByType();
	
	String findVisitorCheckType();
	
	String findStaffCheckType();
	
	String findNetType();
}
