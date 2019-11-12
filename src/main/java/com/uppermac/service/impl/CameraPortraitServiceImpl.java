package com.uppermac.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uppermac.entity.CameraPortrait;
import com.uppermac.repository.CameraPortraitRepository;
import com.uppermac.service.CameraPortraitService;

@Service
public class CameraPortraitServiceImpl implements CameraPortraitService{

	@Autowired
	private CameraPortraitRepository portraitRepository;
	
	@Override
	public CameraPortrait findByNameAndCard(String name, String idCard) {
		// TODO Auto-generated method stub
		return portraitRepository.findByNameAndidCard(name, idCard);
	}

	@Override
	public void delone(CameraPortrait portrait) {
		// TODO Auto-generated method stub
		portraitRepository.delete(portrait);
	}

	@Override
	public void save(CameraPortrait portrait) {
		// TODO Auto-generated method stub
		portraitRepository.save(portrait);
	}

}
