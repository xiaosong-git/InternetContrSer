package com.uppermac.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uppermac.entity.AccessRecord;
import com.uppermac.repository.AccessRecordRepository;
import com.uppermac.service.AccessRecordService;

@Service
public class AccessRecordServiceImpl implements AccessRecordService{

	@Autowired
	private AccessRecordRepository repository;
	
	@Override
	public List<AccessRecord> findAll() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}

	@Override
	public List<AccessRecord> findByIsSendFlag(String isSendFlag) {
		// TODO Auto-generated method stub
		return repository.findByIsSendFlag(isSendFlag);
	}

	@Override
	public void save(AccessRecord accessRecord) {
		// TODO Auto-generated method stub
		repository.save(accessRecord);
	}

	@Override
	public void update(List<AccessRecord> accessRecordList) {
		// TODO Auto-generated method stub
		repository.saveAll(accessRecordList);
	}

	@Override
	public void updateSendFlag(Integer startId, Integer endId) {
		// TODO Auto-generated method stub
		repository.updateSendFlag(startId, endId);
	}

	@Override
	public List<AccessRecord> findByDateTimeAndUser(String date, String time, String idNo, String deviceIp) {
		// TODO Auto-generated method stub
		return repository.findByDateTimeAndUser(date, time, idNo, deviceIp);
	}

}
