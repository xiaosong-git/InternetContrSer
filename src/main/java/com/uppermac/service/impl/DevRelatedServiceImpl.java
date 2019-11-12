package com.uppermac.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uppermac.entity.DeviceRelated;
import com.uppermac.entity.Devices;
import com.uppermac.repository.DevRelatedRepository;
import com.uppermac.service.DevRelatedService;
import com.uppermac.service.DevicesService;
import com.uppermac.service.TowerInforService;

@Service
public class DevRelatedServiceImpl implements DevRelatedService{

	@Autowired
	private DevRelatedRepository repository;
	
	@Autowired
	private TowerInforService towerInforService;
	
	@Autowired
	private DevicesService deviceService;

	@Override
	public void save(DeviceRelated deviceRelated) {
		// TODO Auto-generated method stub
		repository.save(deviceRelated);
	}

	@Override
	public List<DeviceRelated> findFIPbyFloor(String floor) {
		// TODO Auto-generated method stub
		return repository.findFIPbyFloor(floor);
	}

	/**
	 * 
	 * 	根据楼层查找所有通行设备IP地址
	 */
	@Override
	public List<String> getAllFaceDeviceIP(String companyFloor) {
		// TODO Auto-generated method stub
		List<String> allFaceIP = new ArrayList<String>();
		List<String> deviceType = towerInforService.deviceByType();
		if (StringUtils.isEmpty(companyFloor)) {
			List<DeviceRelated> list = repository.findAllFaceRelated();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					String faceIp = ((DeviceRelated) list.get(i)).getFaceIP();
					Devices device =deviceService.findByDeviceIp(faceIp);
					if(deviceType.contains(device.getDeviceType())) {
						allFaceIP.add(faceIp);
					}
					
				}
			}
			
		}else {
			if (companyFloor.contains("|")) {
				String[] floors = companyFloor.split("\\|");
				for (String floor : floors) {
					List<DeviceRelated> list = repository.findFIPbyFloor(floor);
					if (list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							String faceIp =  ((DeviceRelated) list.get(i)).getFaceIP();
							// 排查重复 当设备包含大楼N次时，只取一次
							Devices device =deviceService.findByDeviceIp(faceIp);
							if(deviceType.contains(device.getDeviceType())) {
								if (!allFaceIP.contains(faceIp)) {
									allFaceIP.add(faceIp);
								}
							}
							
						}
					}
				}
				
			}else {
				List<DeviceRelated> list = repository.findFIPbyFloor(companyFloor);
				if (list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						String faceIp =  ((DeviceRelated) list.get(i)).getFaceIP();
						Devices device =deviceService.findByDeviceIp(faceIp);
						if(deviceType.contains(device.getDeviceType())) {
							allFaceIP.add(faceIp);
						}
					}
				}
			}
		}
		return allFaceIP;
	}

	@Override
	public DeviceRelated findByFaceIP(String faceIP) {
		// TODO Auto-generated method stub
		List<DeviceRelated> list = repository.findByFaceIP(faceIP);
		if(list.size()<=0 || list == null) {
			return null;
		}
		return list.get(0);
	}
	
	

}
