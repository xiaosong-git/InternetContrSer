package com.uppermac.scheduler;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.uppermac.entity.Devices;
import com.uppermac.service.DevicesService;
import com.uppermac.service.HCNetSDKService;

public class GetHCRecord implements Job{

	@Autowired
	private HCNetSDKService hcNetSDKService;
	
	@Autowired
	private DevicesService deviceService;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		List<Devices> devices = deviceService.findBydeviceType("DS-K5671");
		for(Devices device:devices) {
			hcNetSDKService.sendAccessRecord(device.getDeviceIp());
		}
	}

	
}
