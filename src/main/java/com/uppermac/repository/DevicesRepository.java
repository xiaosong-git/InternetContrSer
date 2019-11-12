package com.uppermac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uppermac.entity.Devices;

public interface DevicesRepository extends JpaRepository<Devices, String>{

	@Query("select a from Devices a where a.deviceName = ?1 and a.status = ?2")
	List<Devices> findByDevName(String deviceName,String status);
	
	List<Devices> findByDeviceIp(String deviceIp);
	
	@Query("select a from Devices a where a.deviceName != null")
	List<Devices> findAllIPDevice();
	
	@Query("select a from Devices a where a.deviceType = ?1")
	List<Devices> findByDeviceType(String deviceType);
}
