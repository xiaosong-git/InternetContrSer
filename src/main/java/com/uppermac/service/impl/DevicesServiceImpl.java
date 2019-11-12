package com.uppermac.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uppermac.entity.Devices;
import com.uppermac.repository.DevicesRepository;
import com.uppermac.service.DevicesService;

@Service
public class DevicesServiceImpl implements DevicesService{

	@Autowired
	private DevicesRepository repository;
	
	@Override
	public void save(Devices devices) {
		// TODO Auto-generated method stub
		repository.save(devices);
	}
	/**
	 * 
	 * 通过设备名称与设备状态查找设备
	 * 
	 * 
	 */
	@Override
	public List<Devices> findByDevName(String deviceName,String status) {
		// TODO Auto-generated method stub
		return repository.findByDevName(deviceName,status);
	}
	
	/**
	 * 	查找人像设备和二维码设备
	 * 
	 * 
	 */
	
	@Override
	public List<Devices> findAllIPDevice() {
		// TODO Auto-generated method stub
		return repository.findAllIPDevice();
	}

	/***
	 * 
	 * 通过设备IP查找设备
	 *
	 */
	@Override
	public Devices findByDeviceIp(String deviceIp) {
		// TODO Auto-generated method stub
		List<Devices> list = repository.findByDeviceIp(deviceIp);
		if(list.size()<=0 || list==null) {
			return null;
		}
		return list.get(0);
	}
	@Override
	public List<Devices> findAll() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}
	@Override
	public List<Devices> findBydeviceType(String deviceType) {
		// TODO Auto-generated method stub
		return repository.findByDeviceType(deviceType);
	}

}
