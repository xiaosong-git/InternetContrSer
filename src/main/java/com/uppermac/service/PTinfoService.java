package com.uppermac.service;

import java.util.List;

import com.uppermac.entity.PingTelnetInfo;

public interface PTinfoService {

	List<PingTelnetInfo> findAll();
	
	void save(PingTelnetInfo info);
}
