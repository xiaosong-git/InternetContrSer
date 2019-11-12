package com.uppermac.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uppermac.entity.PingTelnetInfo;
import com.uppermac.repository.PTinfoRepository;
import com.uppermac.service.PTinfoService;

@Service
public class PTinfoServiceImpl implements PTinfoService{

	@Autowired
	private PTinfoRepository repository;
	
	@Override
	public List<PingTelnetInfo> findAll() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}

	@Override
	public void save(PingTelnetInfo info) {
		// TODO Auto-generated method stub
		repository.save(info);
	}

	

	
}
