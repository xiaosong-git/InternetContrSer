package com.uppermac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uppermac.entity.TbVisitor;

public interface TbStaffRepository extends JpaRepository<TbVisitor, String>{

	@Query(value = "select a from TbVisitor a where a.solecode = ?1 and a.orgcode = ?2 and a.visitId =?3")
	List<TbVisitor> getStaff(String solecode,String orgcode,String visitId);
	
	@Query(value = "select a from TbVisitor a where a.visitId = ?1 and a.startdate = ?2 and a.enddate =?3")
	List<TbVisitor> findByVisitId(String visitId,String startDate,String endDate);
	
	List<TbVisitor> findByIssued(String issued);

	@Query(value = "select a from TbVisitor a where NOW() > a.enddate and a.issued = 0 and a.delflag = 1")
	List<TbVisitor> findByGoneDay();
	
	@Query(value = "select a from TbVisitor a where a.userrealname = ?1 order by a.visitdate desc,a.visittime desc")
	List<TbVisitor> findByNameAndNewTime(String name);
	
	@Query(value = "select a from TbVisitor a where a.userrealname = ?1 and a.idNO = ?2 and ?3 between a.prestartdate and a.enddate")
	List<TbVisitor> findByBetweenTime(String name,String idCard,String nowtime);
	
	@Query(value ="select a from TbVisitor a where a.staffId = ?1")
	TbVisitor findByUUID(String UUID);
	
	@Query(value ="select a from TbVisitor a where a.userrealname = ?1 and a.idNO = ?2 and a.delflag = '1'")
	List<TbVisitor> findByVisitor(String name, String idNo);
	
	@Query(value ="select a from TbVisitor a where a.visitId = ?1")
	List<TbVisitor> findVisitorId(String visitor_id);
}
