package com.uppermac.service;

import java.util.List;

import com.uppermac.entity.AccessRecord;

public interface AccessRecordService {

	List<AccessRecord> findAll();
	
	List<AccessRecord> findByIsSendFlag(String isSendFlag);
	
	void save(AccessRecord accessRecord);
	
	void update(List<AccessRecord> accessRecordList);
	
	void updateSendFlag(Integer startId,Integer endId);
	
	List<AccessRecord> findByDateTimeAndUser(String date,String time,String idNo,String deviceIp);
}
