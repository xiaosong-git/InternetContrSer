package com.uppermac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uppermac.entity.VisitInOrOut;

public interface VisitInOrOutRepository extends JpaRepository<VisitInOrOut, Integer>{

	@Query("select a from VisitInOrOut a where a.isSendFlag = ?1")
	List<VisitInOrOut> findByIsSendFlag(String isSendFlag);
	
}
