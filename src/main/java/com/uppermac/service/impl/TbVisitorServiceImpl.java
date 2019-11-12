
package com.uppermac.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uppermac.entity.TbVisitor;
import com.uppermac.repository.TbStaffRepository;
import com.uppermac.service.TbVisitorService;


@Service
public class TbVisitorServiceImpl implements TbVisitorService{

	
	@Autowired
	private TbStaffRepository tbStaffReposiroey;
	
	@Override
	public List<TbVisitor> findAll() {
		// TODO Auto-generated method stub
		return tbStaffReposiroey.findAll();
	}

	

	@Override
	public void save(TbVisitor tbStaff) {
		// TODO Auto-generated method stub
		tbStaffReposiroey.save(tbStaff);
	}

	@Override
	public void update(TbVisitor tbStaff) {
		// TODO Auto-generated method stub
		tbStaffReposiroey.save(tbStaff);
	}



	@Override
	public TbVisitor findOne(String id) {
		// TODO Auto-generated method stub
		return tbStaffReposiroey.getOne(id);
	}



	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		tbStaffReposiroey.deleteById(id);
	}



	@Override
	public TbVisitor getStaff(String solecode,String orgcode,String visitId) {
		// TODO Auto-generated method stub
		List<TbVisitor> tbStafflist = tbStaffReposiroey.getStaff(solecode,orgcode,visitId);
		if(tbStafflist.size()<=0) {
			return null;
		}
		TbVisitor tbStaff = tbStafflist.get(0);
		return tbStaff;
	}



	@Override
	public List<TbVisitor> findByVisitId(String visitId,String startDate,String endDate) {
		// TODO Auto-generated method stub
		return tbStaffReposiroey.findByVisitId(visitId,startDate,endDate);
	}



	@Override
	public List<TbVisitor> findByIssued(String issued) {
		// TODO Auto-generated method stub
		return tbStaffReposiroey.findByIssued(issued);
	}



	@Override
	public List<TbVisitor> findByGoneDay() {
		// TODO Auto-generated method stub
		return tbStaffReposiroey.findByGoneDay();
	}



	@Override
	public TbVisitor findByNameAndNewTime(String name) {
		// TODO Auto-generated method stub
		return tbStaffReposiroey.findByNameAndNewTime(name).get(0);
	}



	@Override
	public List<TbVisitor> findByBetweenTime(String name,String idCard, String nowtime) {
		// TODO Auto-generated method stub
		return tbStaffReposiroey.findByBetweenTime(name,idCard,nowtime);
	}



	@Override
	public TbVisitor findByUUID(String UUID) {
		// TODO Auto-generated method stub
		return tbStaffReposiroey.findByUUID(UUID);
	}



	@Override
	public List<TbVisitor> findByVisitor(String name, String idNo) {
		// TODO Auto-generated method stub
		return tbStaffReposiroey.findByVisitor(name, idNo);
	}



	@Override
	public TbVisitor findVisitorId(String visitor_id) {
		// TODO Auto-generated method stub
		List<TbVisitor> list = tbStaffReposiroey.findVisitorId(visitor_id);
		if(list.size()>0) {
			return list.get(0);
		}else {
			return null;
		}
		
	}

}
