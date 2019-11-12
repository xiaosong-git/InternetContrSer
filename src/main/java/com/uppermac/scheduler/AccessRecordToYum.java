package com.uppermac.scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uppermac.data.Constants;
import com.uppermac.entity.AccessRecord;
import com.uppermac.service.AccessRecordService;
import com.uppermac.service.TowerInforService;
import com.uppermac.utils.MD5Util;
import com.uppermac.utils.MyLog;
import com.uppermac.utils.OkHttpUtil;


@DisallowConcurrentExecution
public class AccessRecordToYum implements Job{

	@Autowired
	private TowerInforService towerInforService;
	
	@Autowired
	private AccessRecordService accessRecordService;
	
	MyLog logger = new MyLog(AccessRecordToYum.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		try {
			sendAccessRecord();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendAccessRecord() throws Exception {
		
		logger.info("搜索堆积的扫描通过数据记录------------------");
		//List<VisitInOrOut>
		OkHttpUtil okHttpUtil = new OkHttpUtil();
		
		List<AccessRecord> accessRecordList = accessRecordService.findByIsSendFlag("F");
		if(accessRecordList.size() <= 0) {
			System.out.println("无堆积的扫描结果数据，已全部发送");
			logger.info("无堆积的扫描结果数据，已全部发送");
			return;
		}
		File filepath = new File(Constants.AccessRecPath);
		if(!filepath.exists()) {
			filepath.mkdirs();
		}
		File file = new File(Constants.AccessRecPath,"access1.txt");
		if(!file.exists()) {
			try {
				file.createNewFile();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Integer startId = accessRecordList.get(0).getId();
		Integer endId= accessRecordList.get(accessRecordList.size()-1).getId();
		for (int i = 0; i < accessRecordList.size(); i++) {
			
			StringBuilder context = new StringBuilder();
			context.append(accessRecordList.get(i).getOrgCode());
			context.append("|" + accessRecordList.get(i).getPospCode());
			context.append("|" + accessRecordList.get(i).getScanDate());
			context.append("|" + accessRecordList.get(i).getScanTime());
			context.append("|" + accessRecordList.get(i).getInOrOut());
			context.append("|" + accessRecordList.get(i).getOutNumber());
			context.append("|" + accessRecordList.get(i).getDeviceType());
			context.append("|" + accessRecordList.get(i).getDeviceIp());
			context.append("|" + accessRecordList.get(i).getUserType());
			context.append("|" + accessRecordList.get(i).getUserName());
			context.append("|" + accessRecordList.get(i).getIdCard());
			writeInFile(file, context.toString()); // 写入文件
		}
		Map<String,Object> map = new HashMap<>();
		map.put("pospCode", towerInforService.findPospCode());
		
		map.put("orgCode", towerInforService.findOrgId());
		String keyStr = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
		String sign = MD5Util.MD5(keyStr);
		map.put("sign", sign);
		map.put("file", file);
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Constants.baseFileURl);
		stringBuilder.append(Constants.accessRecordByBatch);
		//stringBuilder.append(Constants.ceshi2);
		String url = stringBuilder.toString();
		String sendResponse =okHttpUtil.postFile(url, map, "multipart/form-data");
		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write("");
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info(sendResponse);
		if(sendResponse.isEmpty() || sendResponse == null) {
			logger.otherError("发送通行记录失败");
			//logger.sendErrorLog(towerInforService.findOrgId(), "发送通行记录失败", "","网络错误", Constants.errorLogUrl,keyStr);
			return;
		}
		
	    JSONObject  jsonObject = JSONObject.parseObject(sendResponse);
		Map<String,Object> verifyReceive = (Map<String,Object>)jsonObject;
		JSONObject  verify = (JSONObject) verifyReceive.get("verify");
		if(verify.get("sign").equals("success")) {
			accessRecordService.updateSendFlag(startId, endId);
			logger.info("通行记录发送成功");
		}
		
	}
	private void writeInFile(File file, String content) {
		OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(file, true),"UTF-8");
			StringBuilder outputString = new StringBuilder();
			outputString.append(content + "\r\n");
			out.write(outputString.toString());
		
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				out.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		
	}
}
