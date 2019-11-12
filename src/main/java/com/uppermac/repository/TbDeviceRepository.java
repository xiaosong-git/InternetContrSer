package com.uppermac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uppermac.entity.TbDevice;

public interface TbDeviceRepository extends JpaRepository<TbDevice, String>{
	
	@Query("select a from TbDevice a where a.deviceId = ?1")
	TbDevice findByDeviceId(String deviceId);
	
	List<TbDevice> findByOrgcode(String orgcode);
	
	@Query("select a from TbDevice a where a.reader = ?1")
	List<TbDevice> findOUTByReaderIp(String readerIp);
	
	@Query("select a from TbDevice a where a.faceRecogIp = ?1")
	List<TbDevice> findOUTByfaceRecogIp(String faceRecogIp);
	
}
