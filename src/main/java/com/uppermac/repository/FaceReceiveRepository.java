package com.uppermac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uppermac.entity.FaceReceive;
import com.uppermac.entity.FaceReceiveKey;

public interface FaceReceiveRepository extends JpaRepository<FaceReceive, FaceReceiveKey>{
	
	@Query("select a from FaceReceive a where a.receiveFlag = ?1 and a.userType =?2")
	List<FaceReceive> findByFlagAndUsertype(String receiveFlag,String type);
	
	@Query("select a from FaceReceive a where a.faceIp = ?1")
	List<FaceReceive> findByFaceIp(String faceIp);
	
	@Query("select a from FaceReceive a where a.userName = ?1 and a.receiveFlag = '1' and a.opera = ?2")
	List<FaceReceive> findByName(String name,String opera);
	
	@Query("select a from FaceReceive a where a.userName = ?1 and a.idCard = ?2 and a.userType =?3")
	List<FaceReceive> findByNameAndid(String name,String idCard,String personType);
	
	@Query("select a from FaceReceive a where a.visitorUUID = ?1")
	List<FaceReceive> findByVisitorUUId(String visitorUUID);
}
