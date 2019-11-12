package com.uppermac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uppermac.entity.PingTelnetInfo;
import com.uppermac.entity.PingTelnetInfoKey;

public interface PTinfoRepository extends JpaRepository<PingTelnetInfo, PingTelnetInfoKey>{

}
