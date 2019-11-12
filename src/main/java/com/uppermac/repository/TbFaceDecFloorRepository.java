package com.uppermac.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uppermac.entity.TbFaceDecFloor;

public interface TbFaceDecFloorRepository extends JpaRepository<TbFaceDecFloor, String> {
	
	@Query("select a from TbFaceDecFloor a where a.contralFloor  LIKE CONCAT('%|',:floor,'|%')")
	List<TbFaceDecFloor> findDecIP(@Param("floor")String floor);
	
	

}
