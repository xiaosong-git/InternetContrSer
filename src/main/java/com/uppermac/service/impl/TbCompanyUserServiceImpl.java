package com.uppermac.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uppermac.controller.InitCompanyUsers;
import com.uppermac.data.FaceDevResponse;
import com.uppermac.entity.TbCompanyUser;
import com.uppermac.repository.TbCompanyUserRepository;
import com.uppermac.service.TbCompanyUserService;
import com.uppermac.service.TowerInforService;
import com.uppermac.utils.HttpUtil;
import com.uppermac.utils.MyErrorException;
import com.uppermac.utils.MyLog;
import com.uppermac.utils.RedisUtils;
import com.uppermac.utils.ThirdResponseObj;

@SuppressWarnings("finally")
@Service
public class TbCompanyUserServiceImpl implements TbCompanyUserService{

	@Resource
	private RedisUtils redisUtils;
	
	@Autowired
	private TowerInforService towerService;
	
	private MyLog logger = new MyLog(TbCompanyUserServiceImpl.class);
	
	@Autowired
	private TbCompanyUserRepository companyUserRepository;
	
	
	@Override
	public List<TbCompanyUser> findAll() {
		/*List<TbCompanyUser> list=null;			//返回对象
		String key = "companyUser_all";		 	//redis key
		String companyUserValue = null;				//redis value
		
		try {
			companyUserValue = redisUtils.get(key);
			if(companyUserValue == null || StringUtils.isEmpty(companyUserValue)) {
				
				list = companyUserRepository.findAll();
				redisUtils.set(key, JSONObject.toJSONString(list));
			}else {
				list = JSONObject.parseArray(companyUserValue,TbCompanyUser.class);
			}
		}catch (Exception e) {
			
		}finally {
			return list;
		}*/
		return companyUserRepository.findAll();
	}

	@Override
	public void save(TbCompanyUser companyUser) {
		companyUserRepository.save(companyUser);
	}

	@Override
	public TbCompanyUser findByNameAndIdNO(String name, String idCard,String status) {
		if(companyUserRepository.findByNameAndIdNO(name, idCard,status) ==null||companyUserRepository.findByNameAndIdNO(name, idCard,status).size()==0) {
			return null;
		}else {
			return companyUserRepository.findByNameAndIdNO(name, idCard,status).get(0);
		}
		
	}

	@Override
	public TbCompanyUser findByNameAndCid(String name, int companyId) {
		
		return  companyUserRepository.findByNameAndCid(name,companyId).get(0);
	}

	@Override
	public void update(TbCompanyUser companyUser) {
		companyUserRepository.saveAndFlush(companyUser);
	}

	@Override
	public List<TbCompanyUser> findByIssued(String issued) {
		// TODO Auto-generated method stub
		return companyUserRepository.findByIssued(issued);
	}

	@Override
	public TbCompanyUser findOne(Integer id) {
		// TODO Auto-generated method stub
		return companyUserRepository.findOne(id);
	}

	@Override
	public List<TbCompanyUser> findByStatus(String currentStatus) {
		// TODO Auto-generated method stub
		return companyUserRepository.findByStatus(currentStatus);
	}

	@Override
	public List<TbCompanyUser> findBeforeToDay(String today) {
		// TODO Auto-generated method stub
		return companyUserRepository.findBeforeToDay(today);
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		companyUserRepository.deleteAll();
	}
	
	@Override
	public boolean sendWhiteList(String deviceIp, TbCompanyUser user, String photo) {
		// TODO Auto-generated method stub
		JSONObject paramsJson = new JSONObject();
		String URL = "http://" + deviceIp + ":8080/office/addOrDelUser";
		String option = user.getCurrentStatus().equals("normal") ? "save" : "delete";
		paramsJson.put("name", user.getUserName());
		paramsJson.put("idCard", user.getIdNO());
		paramsJson.put("op", option);
		if ("save".equals(option)) {
			paramsJson.put("type", "staff");
			paramsJson.put("imageFile", photo);
		}

		StringEntity entity = new StringEntity(paramsJson.toJSONString(), "UTF-8");
		ThirdResponseObj thirdResponseObj = null;
		entity.setContentType("aaplication/json");
		try {
			thirdResponseObj = HttpUtil.http2Se(URL, entity, "UTF-8");
		} catch (MyErrorException e) {
			
			logger.requestError(URL, "");
			e.sendLog(towerService.findOrgId());
			return false;
		}
		if (thirdResponseObj == null) {
			logger.otherError("人脸识别仪器" + deviceIp + "接收"+user.getUserName()+"失败,");
			return false;
		}
		FaceDevResponse faceResponse = JSON.parseObject(thirdResponseObj.getResponseEntity(),FaceDevResponse.class);
		
		if ("success".equals(thirdResponseObj.getCode())) {
			logger.info(user.getUserName()+"下发"+deviceIp+"成功");
		} else {
			logger.otherError(user.getUserName()+"下发"+deviceIp+"失败");
			return false;
		}
		if("001".equals(faceResponse.getResult())) {
			logger.info("人脸设备接收"+user.getUserName()+"成功");
			return true;
		}else {
			logger.otherError("人脸设备接收"+user.getUserName()+"失败，失败原因："+faceResponse.getMessage());
			return false;
		}
	}

	@Override
	public void deleteOne(TbCompanyUser user) {
		// TODO Auto-generated method stub
		companyUserRepository.delete(user);
	}

	@Override
	public List<TbCompanyUser> findFailDel() {
		// TODO Auto-generated method stub
		return companyUserRepository.findFailDel();
	}

	@Override
	public boolean sendDelWhiteList(String deviceIp, String name, String idNO) {
		boolean allSuccess = true;
		JSONObject paramsJson = new JSONObject();
		String URL = "http://" + deviceIp + ":8080/office/addOrDelUser";
		//System.out.println(name+"///////////"+idNO);
		paramsJson.put("name", name);
		paramsJson.put("idCard", idNO);
		paramsJson.put("op", "delete");

		StringEntity entity = new StringEntity(paramsJson.toJSONString(), "UTF-8");
		ThirdResponseObj thirdResponseObj = null;
		entity.setContentType("aaplication/json");
		try {
			thirdResponseObj = HttpUtil.http2Se(URL, entity, "UTF-8");
		} catch (MyErrorException e) {
			allSuccess = false;
			//e.sendLog(towerInforService.findOrgId());
			logger.otherError(deviceIp+"删除访客过期数据失败");
			//e.printStackTrace();
		}
		if(thirdResponseObj == null) {
			logger.otherError("人脸识别仪器" + deviceIp + "接收失败");
			return false;
		}
		if ("success".equals(thirdResponseObj.getCode())) {
			System.out.println("人脸识别仪器" + deviceIp + "接收成功");
			logger.info("人脸识别仪器" + deviceIp + "接收成功");
		} else {
			logger.otherError("人脸识别仪器" + deviceIp + "接收失败");
			allSuccess = false;
		}
		return allSuccess;
	}
}
