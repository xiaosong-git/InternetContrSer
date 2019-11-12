package com.uppermac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uppermac.entity.CameraPortrait;

public interface CameraPortraitRepository extends JpaRepository<CameraPortrait, String>{

	
	@Query(value = "select a from CameraPortrait a where a.username = ?1 and a.idCard = ?2")
	CameraPortrait findByNameAndidCard(String name,String idCard);
}
