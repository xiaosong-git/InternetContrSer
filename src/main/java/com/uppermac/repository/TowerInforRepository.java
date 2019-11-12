package com.uppermac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uppermac.entity.TowerInfor;

public interface TowerInforRepository extends JpaRepository<TowerInfor, Integer>{

	
	@Query(value = "select a.orgId from TowerInfor a where a.id = 1")
	String findByOrgId();
	
	
	@Query(value = "select a.pospCode from TowerInfor a where a.id = 1")
	String findPospCode();
	
	@Query(value = "select a.faceComparesCope from TowerInfor a where a.id = 1")
	String findFaceComparesCope();
	
	@Query(value = "select a.deviceType from TowerInfor a where a.id = 1")
	String findDeviceType();
	
	@Query(value = "select a.key from TowerInfor a where a.id = 1")
	String findKey();
	
	@Query(value = "select a.deviceSelect from TowerInfor a where a.id = 1")
	String findDeviceSelection();
	
	@Query(value = "select a.visitorCheckType from TowerInfor a where a.id = 1")
	String findVisitorCheckType();
	
	@Query(value = "select a.staffCheckType from TowerInfor a where a.id = 1")
	String findStaffCheckType();
	
	@Query(value="select a.netType from TowerInfor a where a.id = 1")
	String findNetType();
}
