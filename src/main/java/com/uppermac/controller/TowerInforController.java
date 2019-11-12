package com.uppermac.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uppermac.config.BuildConfig;
import com.uppermac.entity.TowerInfor;
import com.uppermac.service.impl.TowerInforServiceImpl;

@Controller
public class TowerInforController {

	@Autowired
	private TowerInforServiceImpl towerService;
	
	@RequestMapping("/saveTower")
	public String saveTower(HttpServletRequest reuqest) {
		String orgId = reuqest.getParameter("orgId");
		String orgName = reuqest.getParameter("orgName");
		String pospCode = reuqest.getParameter("pospCode");
		String netType = reuqest.getParameter("netType");
		String faceCompareScope = reuqest.getParameter("faceCompareScope");
		String deceiveType =reuqest.getParameter("deceiveType");
		TowerInfor tower = new TowerInfor();
		tower.setId(1);
		tower.setOrgId(orgId);
		tower.setOrgName(orgName);
		tower.setPospCode(pospCode);
		tower.setNetType(netType);
		tower.setFaceComparesCope(faceCompareScope);
		tower.setDeviceType(deceiveType);
		towerService.save(tower);
		if(netType.equals("1")) {
			return "addsb";
		}else {
			return "orgset";
		}
	}
}
