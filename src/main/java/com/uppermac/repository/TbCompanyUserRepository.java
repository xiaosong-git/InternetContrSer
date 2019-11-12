package com.uppermac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uppermac.entity.TbCompanyUser;

public interface TbCompanyUserRepository extends JpaRepository<TbCompanyUser, Integer>{

	@Query("select a from TbCompanyUser a where a.userName = ?1 and a.idNO = ?2 and a.currentStatus =?3")
	List<TbCompanyUser> findByNameAndIdNO(String name,String idCard,String status);
	
	@Query("select a from TbCompanyUser a where a.userName = ?1 and a.companyId = ?2")
	List<TbCompanyUser> findByNameAndCid(String userName,int companyId);
	
	@Query("select a from TbCompanyUser a where a.issued = ?1")
	List<TbCompanyUser> findByIssued(String issued);
	
	@Query("select a from TbCompanyUser a where a.currentStatus=?1")
	List<TbCompanyUser> findByStatus(String currentStatus);
	
	@Query("select a from TbCompanyUser a where a.companyUserId=?1")
	TbCompanyUser findOne(Integer id);
	
	@Query("select a from TbCompanyUser a where a.createDate < ?1 and a.issued = '1' and a.currentStatus='normal'")
	List<TbCompanyUser> findBeforeToDay(String today);

	@Query("select a from TbCompanyUser a where a.isdel = '1' and a.issued = '0' and a.currentStatus ='deleted'")
	List<TbCompanyUser> findFailDel();
}
