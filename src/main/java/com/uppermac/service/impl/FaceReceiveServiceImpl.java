package com.uppermac.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uppermac.entity.FaceReceive;
import com.uppermac.entity.TbVisitor;
import com.uppermac.repository.FaceReceiveRepository;
import com.uppermac.service.FaceReceiveService;

@Service
public class FaceReceiveServiceImpl implements FaceReceiveService{

	@Autowired
	private FaceReceiveRepository repository;
	
	@Override
	public List<FaceReceive> findAll() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}

	@Override
	public List<FaceReceive> findByFaceIp(String faceIp) {
		// TODO Auto-generated method stub
		return repository.findByFaceIp(faceIp);
	}

	@Override
	public List<FaceReceive> findByFaceFlag(String faceFlag,String userType) {
		// TODO Auto-generated method stub
		return repository.findByFlagAndUsertype(faceFlag, userType);
	}

	@Override
	public void save(FaceReceive faceReceive) {
		// TODO Auto-generated method stub
		repository.save(faceReceive);
	}

	@Override
	public void update(FaceReceive faceReceive) {
		// TODO Auto-generated method stub
		repository.save(faceReceive);
	}

	@Override
	public void delete(FaceReceive faceReceive) {
		// TODO Auto-generated method stub
		repository.delete(faceReceive);
	}

	@Override
	public List<FaceReceive> findByName(String name,String opera) {
		// TODO Auto-generated method stub
		return repository.findByName(name,opera);
	}

	@Override
	public List<FaceReceive> findByNameAndid(String name, String idCard, String personType) {
		// TODO Auto-generated method stub
		return repository.findByNameAndid(name, idCard, personType);
	}

	@Override
	public FaceReceive findByVisitorUUId(String visitorUUID) {
		// TODO Auto-generated method stub
		return repository.findByVisitorUUId(visitorUUID).get(0);
	}


}
