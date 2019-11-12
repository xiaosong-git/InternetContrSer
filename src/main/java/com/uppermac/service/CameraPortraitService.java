package com.uppermac.service;

import com.uppermac.entity.CameraPortrait;

public interface CameraPortraitService {

	public CameraPortrait findByNameAndCard(String name,String idCard);
	
	public void delone(CameraPortrait portrait);
	
	public void save(CameraPortrait portrait);
	
}
