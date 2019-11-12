package com.uppermac.service;

import java.util.List;

import com.uppermac.entity.DeviceRelated;
import com.uppermac.entity.Devices;

public interface DevicesService {

	void save(Devices devices);
	
	List<Devices> findByDevName(String deviceName,String status);
	
	Devices findByDeviceIp(String deviceIp);
	
	List<Devices> findAll();
	
	List<Devices> findAllIPDevice();
	
	List<Devices> findBydeviceType(String deviceType);
}
