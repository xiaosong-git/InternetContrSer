package com.uppermac.service;

import java.util.List;

import com.uppermac.entity.TbScanrecode;

public interface TbScanrecodeService {

	List<TbScanrecode> findByPersonType(String personType);
	
	List<TbScanrecode> findAll();
	
	void save(TbScanrecode tbScanrecode);
	
	void update(TbScanrecode tbScanrecode);
	
	TbScanrecode findOne(String id); 
	
	void delete(String id);	

}
