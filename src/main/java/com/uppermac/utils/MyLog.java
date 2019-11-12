package com.uppermac.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.uppermac.service.TowerInforService;

public class MyLog {

	private Logger logger;
	
	private Class<?> className;

	@Autowired
	private TowerInforService towerInforService;
	
	
	public MyLog( Class<?> className) {
		super();
		this.className = className;
		logger = LoggerFactory.getLogger(className);
	}
	
	/**
	 * @param url		链接地址
	 * @param content	错误说明
	 */
	public void requestError(String url,String content) {
		if(content == ""||null == content) {
			logger.error("连接"+url+"异常，数据无法下发");
		}else {
			logger.error("连接"+url+"异常,原因"+content);
		}
		
	}

	/**
	 * 
	 * @param strings
	 */
	public void stringNull(String...strings) {
		String nullstr = "参数";
		for(int 	i=0;i<strings.length;i++) {
			if(i==strings.length-1) {
				nullstr =nullstr + strings[i];
			}else {
				nullstr =nullstr + strings[i]+",";
			}
			
		}
		nullstr +="为空";
		logger.error(nullstr);
	}
	
	public void otherError(String content) {
		logger.error(content);
	}
	
	public void urlRequsetNull(String url) {
		logger.error("请求网址"+url+"获取的数据为空");
	}
	
	public void info(String content) {
		logger.info(content);
	}
	public void warn(String content) {
		logger.warn(content);
	}
	
	public void sendErrorLog(String orgCode,String conntext,String deviceId,String errorType,String url,String key) throws Exception {
		
		Map<String,String> map=new HashMap<>();
		map.put("orgCode", orgCode);
		map.put("deviceId", deviceId);
		map.put("logContext", conntext);
		map.put("errorTime", getDateTime());
		map.put("errorType", errorType);
		map.put("sign", MD5Util.MD5(key));
		OkHttpUtil okhttp = new OkHttpUtil();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					okhttp.post(url, map);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	private String getDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}
}
