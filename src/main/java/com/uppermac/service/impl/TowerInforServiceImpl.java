package com.uppermac.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uppermac.entity.TowerInfor;
import com.uppermac.repository.TowerInforRepository;
import com.uppermac.service.TowerInforService;


@Service
public class TowerInforServiceImpl implements TowerInforService{
	
	@Autowired
	private TowerInforRepository towerInforRepository;

	Logger logger = LogManager.getLogger(TowerInforServiceImpl.class);
	
	@Override
	public List<TowerInfor> findAll() {
		// TODO Auto-generated method stub
		return towerInforRepository.findAll();
	}

	@Override
	public TowerInfor findone(Integer id) {
		// TODO Auto-generated method stub
		return towerInforRepository.getOne(id);
	}

	@Override
	public TowerInfor update(TowerInfor towerInfor) {
		// TODO Auto-generated method stub
		return towerInforRepository.save(towerInfor);
	}

	@Override
	public TowerInfor save(TowerInfor towerInfor) {
		// TODO Auto-generated method stub
		return towerInforRepository.save(towerInfor);
	}

	@Override
	public String findOrgId() {
		// TODO Auto-generated method stub
		return towerInforRepository.findByOrgId();
	}

	@Override
	public String findPospCode() {
		// TODO Auto-generated method stub
		return towerInforRepository.findPospCode();
	}

	@Override
	public String findFaceComparesCope() {
		// TODO Auto-generated method stub
		return towerInforRepository.findFaceComparesCope();
	}

	@Override
	public String findDeviceType() {
		// TODO Auto-generated method stub
		return towerInforRepository.findDeviceType();
	}

	@Override
	public String findKey() {
		// TODO Auto-generated method stub
		return towerInforRepository.findKey();
	}

	/**
	 * 	大楼所使用的设备类型分析
	 * 
	 */
	@Override
	public List<String> deviceByType() {
		// TODO Auto-ge	nerated method stub
		
		
		String deviceSelect = towerInforRepository.findDeviceSelection();
		if(null == deviceSelect) {
			return null;
		}else {
			List<String> deviceTypes = new ArrayList<>();
			deviceTypes =Arrays.asList(deviceSelect.split("\\|"));
			return deviceTypes;
		}
		
	}

	@Override
	public String findVisitorCheckType() {
		// TODO Auto-generated method stub
		return towerInforRepository.findVisitorCheckType();
	}

	@Override
	public String findStaffCheckType() {
		// TODO Auto-generated method stub
		return towerInforRepository.findStaffCheckType();
	}	
	
	@Override
	public String findNetType() {
		// TODO Auto-generated method stub
		return towerInforRepository.findNetType();
	}	
}
