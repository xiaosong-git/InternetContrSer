package com.uppermac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uppermac.entity.TbScanrecode;

public interface TbScanrecodeRepository extends JpaRepository<TbScanrecode, String>{

	List<TbScanrecode> findByPersonType(String personType);
}
