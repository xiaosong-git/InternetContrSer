package com.uppermac.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uppermac.data.Constants;
import com.uppermac.data.ResponseData;
import com.uppermac.entity.AccessRecord;
import com.uppermac.entity.DeviceRelated;
import com.uppermac.entity.Devices;
import com.uppermac.entity.TbVisitor;
import com.uppermac.service.AccessRecordService;
import com.uppermac.service.DevRelatedService;
import com.uppermac.service.DevicesService;
import com.uppermac.service.TbVisitorService;
import com.uppermac.service.TowerInforService;
import com.uppermac.utils.Control24DeviceUtil;
import com.uppermac.utils.ControlDeviceUtil;
import com.uppermac.utils.IPUtil;
import com.uppermac.utils.MyLog;
import com.uppermac.utils.RedisUtils;

@RestController
public class FaceResultController {

	private MyLog logger = new MyLog(FaceResultController.class);

	/*
	 * 
	 * 	接收人脸比对结果
	 * 
	 */
	@Resource
	private RedisUtils redisUtils;

	@Autowired
	private TowerInforService towerInforService;

	@Autowired
	private TbVisitorService staffService;

	@Autowired
	private AccessRecordService accessRecordService;
	
	@Autowired
	private DevicesService deviceService;

	@Autowired
	private DevRelatedService devRelatedService;

	/*	facecomparescope:设备所传比对阀值
	 * 	name:比对人名字
	 *  idCard:比对人证件号
	 *  towerFaceCope:大楼设置的阈值
	 *  type:人员类型
	 *  
	 */
	
	@RequestMapping("/sendCamRes")
	public ResponseData faceResu(HttpServletRequest request) throws Exception {

		
		String facecomparescope = request.getParameter("facecomparescope");
		String name = request.getParameter("name");
		String idCard = request.getParameter("idCard");
		String towerFaceCope = towerInforService.findFaceComparesCope();
		String type = request.getParameter("type");
		String faceRecogIp = IPUtil.getIp(request);
		logger.info(name+"***"+type+"***"+faceRecogIp+"***"+facecomparescope);
		
		if (towerFaceCope == null) {
			logger.stringNull(towerFaceCope);
			String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(), "大楼人脸比对阀值未设置", "","数据错误", Constants.errorLogUrl,keysign);
			return new ResponseData("500", "大楼人脸比对阀值未设置");
		}
		if (StringUtils.isEmpty(type)) {
			logger.stringNull(type);
			String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(), "人员类型参数缺失", "","数据错误", Constants.errorLogUrl,keysign);
			return new ResponseData("500", "人员类型参数缺失");
		}
		if (StringUtils.isEmpty(faceRecogIp)) {
			logger.stringNull(faceRecogIp);
			String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(), "比对值识别器IP地址缺失", "","数据错误", Constants.errorLogUrl,keysign);
			return new ResponseData("500", "比对值识别器IP地址缺失");
		}
		if (StringUtils.isEmpty(facecomparescope)) {
			logger.stringNull(facecomparescope);
			String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(), "比对值参数缺失", "", "数据错误",Constants.errorLogUrl,keysign);
			return new ResponseData("500", "比对值参数缺失");
		}
		if (StringUtils.isEmpty(name)) {
			logger.stringNull(name);
			String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(), "名字缺失", "","数据错误", Constants.errorLogUrl,keysign);
			return new ResponseData("500", "名字缺失");
		}
		if (StringUtils.isEmpty(idCard)) {
			logger.stringNull(idCard);
			String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(), "身份号码缺失", "", "数据错误",Constants.errorLogUrl,keysign);
			return new ResponseData("500", "身份号码缺失");
		}
		if (Double.parseDouble(facecomparescope) < Double.parseDouble(towerFaceCope)) {
			logger.otherError("比对值未达到阀值");
			return new ResponseData("500", "比对值未达到阀值");
		} else {
			Devices device = deviceService.findByDeviceIp(faceRecogIp);
			if(null == device) {
				return new ResponseData("500", "失败");
			}
			if(device.getFQ_turnover().equals("out")) {
				open(faceRecogIp, name, idCard,type);
				return new ResponseData("200", "成功");
			}else {
				if (type.equals("visitor")) {
					// 获取访客有无访问数据
					List<TbVisitor> staffs = staffService.findByBetweenTime(name, idCard, getDateTime());

					if (staffs.size() > 0) {
						String key = "v_" + name + "_" + idCard;
						if (redisUtils.get(key)== null) {
							redisUtils.set("v_" + name + "_" + idCard, "locked");
							redisUtils.expire(key, 4);
							open(faceRecogIp, name, idCard, type);
							return new ResponseData("200", "成功");
						} else {
							return new ResponseData("500", "已锁定");
						}
					} else {
						logger.info("该访客访问时间过期，访问无效");
						return new ResponseData("500", "访问无效");
					}

				} else {
					
					String key = "c_" + name + "_" + idCard;
					if (redisUtils.get(key)==null) {
						redisUtils.set("c_" + name + "_" + idCard, "locked");
						redisUtils.expire(key, 4);
						open(faceRecogIp, name, idCard,type);
						return new ResponseData("200", "成功");
					} else {
						return new ResponseData("500", "已锁定");
					}
				}
			}
		}
	}
	// 开门并记录通行
	private void open(String faceRecogIp, String name, String idCard,String type) throws Exception {
		
		DeviceRelated devRelated = devRelatedService.findByFaceIP(faceRecogIp);
		if (devRelated == null) {
			logger.otherError("找不到人脸IP为"+faceRecogIp+"的相关设备");
			String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(), "找不到人脸IP为"+faceRecogIp+"的相关设备", "","数据错误", Constants.errorLogUrl,keysign);
			return;
		} else {
			//24路继电器
			Control24DeviceUtil.controlDevice(devRelated.getRelayIP(), 8080,devRelated.getRelayOUT(), towerInforService.findOrgId());
			//4路继电器
			//ControlDeviceUtil.controlDevice(device.getDeviceIp(), 8080, device.getE_out(), towerInforService.findOrgId());
			saverecord(name, idCard,type,faceRecogIp,devRelated.getRelayOUT());
		}
	}

	// 记录通行记录
	private void saverecord( String name, String idCard,String personType,String faceIP,String OUT) {
		// TODO Auto-generated method stub
		Devices device = deviceService.findByDeviceIp(faceIP);
		AccessRecord accessRecord = new AccessRecord();
		accessRecord.setOrgCode(towerInforService.findOrgId());
		accessRecord.setPospCode(towerInforService.findPospCode());
		accessRecord.setScanDate(getDate());
		accessRecord.setScanTime(getTime());
		accessRecord.setInOrOut(device.getFQ_turnover());
		accessRecord.setOutNumber(OUT);
		accessRecord.setDeviceType("FACE");
		accessRecord.setDeviceIp(faceIP);
		accessRecord.setUserType(personType);
		accessRecord.setUserName(name);
		accessRecord.setIdCard(idCard);
		accessRecord.setIsSendFlag("F");
		accessRecordService.save(accessRecord);
	}

	private String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}

	private String getTime() {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}

	private String getDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}
}
