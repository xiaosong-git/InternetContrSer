package com.uppermac.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.uppermac.entity.AccessRecord;

public interface AccessRecordRepository extends JpaRepository<AccessRecord, Integer>{

	
	List<AccessRecord> findByIsSendFlag(String isSendFlag);
	
	@Transactional
	@Modifying
	@Query("update AccessRecord a set a.isSendFlag = 'T' where a.id between ?1 and ?2")
	void updateSendFlag(Integer startId,Integer endId);
	
	
	@Query("select a from AccessRecord a where a.scanDate =?1 and a.scanTime =?2 and a.idCard =?3 and a.deviceIp =?4")
	List<AccessRecord> findByDateTimeAndUser(String date,String time,String idNo,String deviceIp);
}
