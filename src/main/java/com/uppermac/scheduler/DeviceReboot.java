package com.uppermac.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.uppermac.entity.Devices;
import com.uppermac.service.DevicesService;
import com.uppermac.service.TowerInforService;
import com.uppermac.utils.MyLog;

public class DeviceReboot implements Job{

	private MyLog logger = new MyLog(DeviceReboot.class);
	
	@Autowired
	private DevicesService devicesService;
	
	@Autowired
	private TowerInforService towerInforService;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		/*if(!towerInforService.findDeviceType().equals("1")) {
			reboot();
		}else {
			return;
		}*/
	}
	private void reboot() {
		List<Devices> faceDecs = devicesService.findByDevName("FACE", "running");
	
		for(Devices faceDec:faceDecs) {
			String disconnectCmd = "adb disconnect";
			String connectCmd ="adb connect "+faceDec.getDeviceIp();
			String rebootCmd = "adb reboot";
			BufferedReader br = null;
			try {
				Runtime.getRuntime().exec(disconnectCmd);
				new Thread().sleep(500);
				Process p = Runtime.getRuntime().exec(connectCmd);
				new Thread().sleep(500);
				
				br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = null;
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				String response = sb.toString();
				System.out.println(response);
				if(response.contains("connected to "+faceDec.getDeviceIp())) {
					logger.info("准备重启设备"+faceDec.getDeviceIp());
					Runtime.getRuntime().exec(rebootCmd);
					new Thread().sleep(500);
					logger.info("设备重启成功");
					Runtime.getRuntime().exec(disconnectCmd);
					new Thread().sleep(500);
				}else {
					
					logger.otherError("重启"+faceDec.getDeviceIp()+"失败");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
