package com.uppermac.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.uppermac.data.Constants;
import com.uppermac.data.ResponseData;
import com.uppermac.entity.Devices;
import com.uppermac.service.DevicesService;
import com.uppermac.service.TowerInforService;
import com.uppermac.utils.MyLog;
import com.uppermac.utils.OkHttpUtil;

@RestController
public class UpdateDeviceAPP {

	OkHttpUtil okhttp = new OkHttpUtil();

	@Autowired
	private DevicesService devicesService;
	
	@Autowired
	private TowerInforService towerInforService;

	MyLog logger = new MyLog(UpdateDeviceAPP.class);
	
	
	@RequestMapping("/receiveAPK")
	public ResponseData updateDeviceAPP(MultipartFile apk, HttpServletRequest request) throws Exception {
		
		String version = request.getParameter("version");
		if (apk.isEmpty() || !apk.getName().contains("apk")) {
			logger.otherError("更新APK失败，apk文件错误");
			String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(), "更新APK失败，apk文件错误", "","文件错误", Constants.errorLogUrl,keysign);
			return new ResponseData("500", "fail");
		}
		
		File parentFile = new File(Constants.ApksPath);
		if (!parentFile.exists()) {
			parentFile.mkdir();
		}
		String fileName = apk.getOriginalFilename();
		File targetFile = new File(Constants.ApksPath, fileName);
		if (!targetFile.exists()) {
			try {
				targetFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			FileOutputStream out;
			out = new FileOutputStream(targetFile.getAbsolutePath());
			out.write(apk.getBytes());
			out.flush();
			out.close();

			// TODO Auto-generated method stub
			List<Devices> decList = new ArrayList<>();
			decList = devicesService.findByDevName("FACE", "running");
			Map<String, Object> param = new HashMap<>();
			param.put("apk", targetFile);
			param.put("version", version);
			for (Devices facedec : decList) {
				System.out.println(facedec.getDeviceIp() + "开始更新APP");
				String url = "http://" + facedec.getDeviceIp() + ":8080/office/upgradeApk";
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String response = okhttp.postFile(url, param, "multipart/form-data");
					}
				}).start();
				
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.otherError("APK更新失败");
			String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(), "APK更新失败", "","更新错误", Constants.errorLogUrl,keysign);
			e.printStackTrace();
		}

		return new ResponseData("200", "success");
	}

}
