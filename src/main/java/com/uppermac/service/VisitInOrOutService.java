package com.uppermac.service;

import java.util.List;

import com.uppermac.entity.VisitInOrOut;

public interface VisitInOrOutService {

	List<VisitInOrOut> findAll();
	
	VisitInOrOut findOne(int id);
	
	void delete(int id);
	
	void save(VisitInOrOut visitInOrOut);
	
	void update(VisitInOrOut visitInOrOut);
	
	List<VisitInOrOut> findByFlag(String isSendFlag);
	
}
