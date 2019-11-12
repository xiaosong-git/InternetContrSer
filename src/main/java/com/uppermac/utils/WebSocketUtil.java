package com.uppermac.utils;

import java.net.URI;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.uppermac.entity.Devices;
import com.uppermac.service.DevicesService;
import com.uppermac.service.TowerInforService;

public class WebSocketUtil extends WebSocketClient{

	private MyLog logger = new MyLog(WebSocketUtil.class);
	
	@Autowired
	private DevicesService devicesService;
	
	public WebSocketUtil(URI serverUri) {
		super(serverUri);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		// TODO Auto-generated method stub
		logger.info("连接上webSocket服务端");
		//List<Devices> des = devicesService.findAll();
		//String json = JSONArray.toJSONString(des);
		//this.send(json);
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		logger.info("-------- 接收到服务端数据： " + message + "--------");
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		logger.otherError("关闭webSocket，原因:"+reason);
	}

	@Override
	public void onError(Exception ex) {
		// TODO Auto-generated method stub
		logger.otherError("连接服务端webSocket失败");
	}
}
