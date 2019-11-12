package com.uppermac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uppermac.service.HCNetSDKService;

@RestController
public class testController {

	@Autowired
	private HCNetSDKService hcNetSDKService;
	
	@RequestMapping("/testLogin")
	public void init() {
		int s = hcNetSDKService.initAndLogin("192.168.2.102");
		System.out.println(s);
	}
}
