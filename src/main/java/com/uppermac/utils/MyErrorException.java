package com.uppermac.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.uppermac.data.Constants;
import com.uppermac.service.TowerInforService;

public class MyErrorException extends Exception{
	
	protected Logger log = LoggerFactory.getLogger(OkHttpUtil.class);
	
	public MyErrorException() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	public MyErrorException(String message) {
		// TODO Auto-generated constructor stub
		super(message);
	}
	
	public MyErrorException(Throwable cause) {
		// TODO Auto-generated constructor stub
		super(cause);
	}
	
	
	
	public void sendLog(String orgId) {
		File logFile = new File("E:\\logs\\error.log");
		if(!logFile.exists()) {
			log.error("error.log不存在");
		}else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("companyName", orgId);
			params.put("logfiles", new File("E://logs//error.log"));
			params.put("type", "1");
			if(new File("E://logs//error.log").exists()) {
				OkHttpUtil okhttp = new OkHttpUtil();
				new Thread(new Runnable() { 
					@Override 
					public void run() {
						// TODO Auto-generated method stub
						String s =okhttp.postFile(Constants.sendLogFile, params, "multipart/form-data");
						log.info(s);
					}
				}).start();
			}
			
		}
	}
	public void sendError(String orgCode,String conntext,String deviceId,String url) throws Exception {
		
		Map<String,String> map=new HashMap<>();
		map.put("orgCode", orgCode);
		map.put("deviceId", deviceId);
		map.put("logContext", conntext);
		OkHttpUtil okhttp = new OkHttpUtil();
		
		String s =okhttp.post(url, map);
		log.info("发送结果："+s);
			
		
		
	}

}
