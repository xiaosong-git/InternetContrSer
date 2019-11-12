package com.uppermac.service;

import java.util.List;

import com.uppermac.entity.DeviceRelated;

public interface DevRelatedService {

	void save(DeviceRelated deviceRelated);
	
	List<DeviceRelated> findFIPbyFloor(String floor);
	
	List<String> getAllFaceDeviceIP(String companyFloor);
	
	DeviceRelated findByFaceIP(String faceIP);
}
