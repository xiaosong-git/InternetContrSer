package com.uppermac.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.fastjson.JSON;
import com.uppermac.entity.Devices;
import com.uppermac.service.DevicesService;
import com.uppermac.service.HCNetSDKService;
import com.uppermac.utils.RedisUtils;


public class HCRecord implements Job {

	@Resource
	private RedisUtils redisUtils;
	
	@Autowired
	private HCNetSDKService hcNetSDKService;

	@Autowired
	private DevicesService devicesService;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		//查找所有运行的人脸设备
				List<Devices> faceDecs = devicesService.findByDevName("FACE", "running");
				for(Devices device: faceDecs) {
					
					//型号是DS-2CD8627FWD（网络摄像头）的设备
					if(device.getDeviceType().equals("DS-2CD8627FWD")) {
						
						
						Map<String,String> map = new HashMap<>();
						String IPCDateRedis = redisUtils.get("IPC"+device.getDeviceIp());
						if(null != IPCDateRedis) {
							map = JSON.parseObject(IPCDateRedis,map.getClass());
						}
					
						map.put(getYesterdayByDate(), "false");
						hcNetSDKService.initAndLogin(device.getDeviceIp());
						
						for(String key : map.keySet()){
						    if(map.get(key).equals("false")) {
						    	boolean result = hcNetSDKService.getIPCRecord(device.getDeviceIp(), key);
						    	if(result) {
						    		map.remove(key);
						    	}
						    }
						}
						String strMap = JSON.toJSONString(map);
						redisUtils.set("IPC"+device.getDeviceIp(),strMap);
					}
				}
	}

	public String getYesterdayByDate() {
		// 实例化当天的日期
		Date today = new Date();
		// 用当天的日期减去昨天的日期
		Date yesterdayDate = new Date(today.getTime() - 86400000L);
		String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(yesterdayDate);
		return yesterday;
	}
}
