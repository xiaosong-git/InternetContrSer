package com.uppermac.service;

import java.util.List;

import com.uppermac.entity.FaceReceive;

public interface FaceReceiveService {
	
	List<FaceReceive> findAll();
	
	List<FaceReceive> findByFaceIp(String faceIp);

	List<FaceReceive> findByFaceFlag(String faceFlag,String userType);
	
	void save(FaceReceive faceReceive);
	
	void update(FaceReceive faceReceive);
	
	void delete(FaceReceive faceReceive);

	List<FaceReceive> findByName(String name,String opera);
	
	List<FaceReceive> findByNameAndid(String name,String idCard,String personType);
	
	FaceReceive findByVisitorUUId(String visitorUUID);
}
