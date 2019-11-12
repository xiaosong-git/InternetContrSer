package com.uppermac.service;

import java.util.List;

import com.uppermac.entity.TbVisitor;


public interface TbVisitorService {

	List<TbVisitor> findAll();
	
	void save(TbVisitor tbStaff);
	
	void update(TbVisitor tbStaff);
	
	TbVisitor findOne(String id); 
	
	void delete(String id);
	
	TbVisitor getStaff(String solecode,String orgcode,String visitId);
	
	List<TbVisitor> findByVisitId(String visitId,String startDate,String endDate);
	
	List<TbVisitor> findByIssued(String issued);
	
	List<TbVisitor> findByGoneDay();
	
	TbVisitor findByNameAndNewTime(String name);
	
	List<TbVisitor> findByBetweenTime(String name,String idCard,String nowtime);
	
	TbVisitor findByUUID(String UUID);
	
	List<TbVisitor> findByVisitor(String name,String idNo);
	
	TbVisitor findVisitorId(String visitor_id);
	
}
