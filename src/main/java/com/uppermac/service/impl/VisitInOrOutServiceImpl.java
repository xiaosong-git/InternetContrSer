package com.uppermac.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uppermac.entity.VisitInOrOut;
import com.uppermac.repository.VisitInOrOutRepository;
import com.uppermac.service.VisitInOrOutService;


@Service
public class VisitInOrOutServiceImpl implements VisitInOrOutService{

	@Autowired
	private VisitInOrOutRepository visitInOrOutRepository;
	
	
	@Override
	public List<VisitInOrOut> findAll() {
		// TODO Auto-generated method stub
		return visitInOrOutRepository.findAll();
	}

	@Override
	public VisitInOrOut findOne(int id) {
		// TODO Auto-generated method stub
		return visitInOrOutRepository.findById(id).get();
	}

	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub
		visitInOrOutRepository.deleteById(id);
	}

	@Override
	public void save(VisitInOrOut visitInOrOut) {
		// TODO Auto-generated method stub
		visitInOrOutRepository.save(visitInOrOut);
	}

	@Override
	public void update(VisitInOrOut visitInOrOut) {
		// TODO Auto-generated method stub
		visitInOrOutRepository.save(visitInOrOut);
	}

	@Override
	public List<VisitInOrOut> findByFlag(String isSendFlag) {
		// TODO Auto-generated method stub
		return visitInOrOutRepository.findByIsSendFlag(isSendFlag);
	}

}
