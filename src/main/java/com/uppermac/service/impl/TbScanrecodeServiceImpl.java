package com.uppermac.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uppermac.entity.TbScanrecode;
import com.uppermac.repository.TbScanrecodeRepository;
import com.uppermac.service.TbScanrecodeService;

@Service
public class TbScanrecodeServiceImpl implements TbScanrecodeService{

	
	@Autowired
	private TbScanrecodeRepository tbScanrecodeRepository;
	
	@Override
	public List<TbScanrecode> findAll() {
		// TODO Auto-generated method stub
		return tbScanrecodeRepository.findAll();
	}

	@Override
	public void save(TbScanrecode tbScanrecode) {
		// TODO Auto-generated method stub
		tbScanrecodeRepository.save(tbScanrecode);
	}

	@Override
	public void update(TbScanrecode tbScanrecode) {
		// TODO Auto-generated method stub
		tbScanrecodeRepository.save(tbScanrecode);
	}

	@Override
	public TbScanrecode findOne(String id) {
		// TODO Auto-generated method stub
		return tbScanrecodeRepository.getOne(id);
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		tbScanrecodeRepository.deleteById(id);
	}

	@Override
	public List<TbScanrecode> findByPersonType(String personType) {
		// TODO Auto-generated method stub
		return tbScanrecodeRepository.findByPersonType(personType);
	}

}
