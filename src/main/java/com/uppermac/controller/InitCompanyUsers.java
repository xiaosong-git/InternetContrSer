package com.uppermac.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.uppermac.data.Constants;
import com.uppermac.entity.Devices;
import com.uppermac.entity.FaceReceive;
import com.uppermac.entity.TbCompanyUser;
import com.uppermac.entity.responseModel.GetCompanyUserScheduleModel;
import com.uppermac.service.DevRelatedService;
import com.uppermac.service.DevicesService;
import com.uppermac.service.FaceReceiveService;
import com.uppermac.service.HCNetSDKService;
import com.uppermac.service.TbCompanyUserService;
import com.uppermac.service.TowerInforService;
import com.uppermac.utils.Base64;
import com.uppermac.utils.Base64_2;
import com.uppermac.utils.FilesUtils;
import com.uppermac.utils.HttpUtil;
import com.uppermac.utils.MyErrorException;
import com.uppermac.utils.MyLog;
import com.uppermac.utils.OkHttpUtil;
import com.uppermac.utils.RedisUtils;
import com.uppermac.utils.ThirdResponseObj;

@Controller
public class InitCompanyUsers {

	String uKey = Base64.encode("admin".getBytes());
	String uvalue = Base64.encode("123".getBytes());

	@Autowired
	private FaceReceiveService faceReceiveService;

	@Autowired
	private DevRelatedService devRelatedService;

	@Autowired
	private DevicesService devicesService;

	@Autowired
	private TowerInforService towerInforService;

	@Autowired
	private TbCompanyUserService companyUserService;

	private MyLog logger = new MyLog(InitCompanyUsers.class);

	@Autowired
	OkHttpUtil okHttpUtil = new OkHttpUtil();

	@Resource
	private RedisUtils redisUtils;

	@Autowired
	private HCNetSDKService hcNetSDKService;

	@RequestMapping("/initCompanyUsers")
	public String login() {
		return "initUsers";
	}

	@RequestMapping("/toInit")
	public String init(String key, String value) throws Exception, MyErrorException {

		key = Base64.encode(key.getBytes());
		value = Base64.encode(value.getBytes());
		if (!key.equals(uKey)) {
			logger.otherError("初始化失败，账号不正确");
			String keysign = towerInforService.findOrgId() + towerInforService.findPospCode()
					+ towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(), "初始化失败，账号不正确", "", "数据错误", Constants.errorLogUrl,
			//		keysign);
			return "initFail";
		}
		if (!value.equals(uvalue)) {
			logger.otherError("初始化失败，密码不正确");
			String keysign = towerInforService.findOrgId() + towerInforService.findPospCode()
					+ towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(), "初始化失败，密码不正确", "", "数据错误", Constants.errorLogUrl,
			//		keysign);
			return "initFail";
		}

		Map<String, String> map = new HashMap<>();
		map.put("org_code", towerInforService.findOrgId());
		StringBuilder stringBuilder = new StringBuilder();

		// stringBuilder.append("http://192.168.10.129:8098/visitor/companyUser/findApplyAllSucOrg");
		stringBuilder.append(Constants.baseURl);
		stringBuilder.append(Constants.initUsers);

		String url = stringBuilder.toString();
		logger.info("获取员工数据地址：" + url);
		// System.out.println("获取员工数据地址：" + url);

		String responseContent = okHttpUtil.post(url, map);

		if (responseContent != null) {
			GetCompanyUserScheduleModel getStaffScheduleResponseParentModel = JSON.parseObject(responseContent,
					GetCompanyUserScheduleModel.class);
			System.out.println(getStaffScheduleResponseParentModel.toString());
			if (getStaffScheduleResponseParentModel.getData() != null) {
				companyUserService.deleteAll();
				List<TbCompanyUser> companyUserList = getStaffScheduleResponseParentModel.getData();
				for (TbCompanyUser companyUser : companyUserList) {
					sendUsers(companyUser);
				}
			}
			return "initSuccess";
		} else {

			return "initFail";
		}

	}

	private void sendUsers(TbCompanyUser companyUser) throws Exception {

		// 非正常状态员工不接收
		if (!"normal".equals(companyUser.getCurrentStatus())) {
			return;
		}
		companyUser.setIssued("1");
		companyUser.setIsdel("1");
		companyUserService.save(companyUser);

		if (companyUser.getPhoto() != null) {
			redisUtils.set("photo_" + companyUser.getCompanyId() + "_" + companyUser.getIdNO(), companyUser.getPhoto());
			byte[] photoKey = Base64_2.decode(companyUser.getPhoto());
			String fileName = companyUser.getUserName() + companyUser.getCompanyId() + ".jpg";
			File fileload = FilesUtils.getFileFromBytes(photoKey, Constants.StaffPath, fileName);
			logger.info("初始化员工存放照片地址"+fileload.getAbsolutePath());
		} else {
			logger.warn(companyUser.getUserName() + "该用户无照片");
			String keysign = towerInforService.findOrgId() + towerInforService.findPospCode()
					+ towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(), companyUser.getUserName() + "该用户无照片", "", "数据错误",
			//		Constants.errorLogUrl, keysign);
			return;
		}

		String companyfloor = null;
		if (null != companyUser.getCompanyFloor()) {
			companyfloor = companyUser.getCompanyFloor();
		}

		String photo = isPhoto(companyUser);
		if (photo == null) {
			return;
		}

		List<String> allFaceDecive = devRelatedService.getAllFaceDeviceIP(companyfloor);
		if (allFaceDecive.size() <= 0 || allFaceDecive == null) {
			return;
		}
		System.out.println("共需要下发"+allFaceDecive.size()+"台");
		if (allFaceDecive.size() > 0) {
			String issued = "0";
			for (int i = 0; i < allFaceDecive.size(); i++) {
				logger.info("需下发的人像识别仪器IP为：" + allFaceDecive.get(i));
				Devices device = devicesService.findByDeviceIp(allFaceDecive.get(i));
				if (null == device) {
					logger.otherError("设备表缺少IP为" + allFaceDecive.get(i) + "的设备");
					continue;
				}
				boolean isSuccess = true;
				if (device.getDeviceType().equals("TPS980")) {
					isSuccess = companyUserService.sendWhiteList((String) allFaceDecive.get(i), companyUser,
							companyUser.getPhoto());
				} else if (device.getDeviceType().equals("DS-K5671")) {
					isSuccess = setUser(device, companyUser);

				}else if(device.getDeviceType().equals("DS-2CD8627FWD")) {
					File picAppendData = IPCxmlFile(companyUser);
					String filePath = Constants.StaffPath + "/" + companyUser.getUserName() + companyUser.getCompanyId()
					+ ".jpg";
					File picture = new File(filePath);
					if(!picAppendData.exists()||!picture.exists()) {
						logger.otherError("下发网络摄像头失败，找不到附加信息或图片的数据");
						isSuccess =false;
					}else {
						isSuccess = hcNetSDKService.sendToIPC((String) allFaceDecive.get(i), picture, picAppendData,companyUser,null);
					}
				}
				// 针对下发失败的需要登记，待下次冲洗下发，已经下发成功的不在下发
				FaceReceive faceReceive = new FaceReceive();

				if (isSuccess == false) {
					issued = "1";
					faceReceive.setOpera("save");
					faceReceive.setFaceIp(allFaceDecive.get(i));
					faceReceive.setIdCard(companyUser.getIdNO());
					faceReceive.setUserName(companyUser.getUserName());
					faceReceive.setReceiveFlag("1");
					faceReceive.setUserType("staff");
					faceReceive.setReceiveTime(getDateTime());
					faceReceiveService.save(faceReceive);
					String keysign = towerInforService.findOrgId() + towerInforService.findPospCode()
							+ towerInforService.findKey();
					//logger.sendErrorLog(towerInforService.findOrgId(), "下发" + companyUser.getUserName() + "失败，已记录",
					//		"人脸设备IP" + allFaceDecive.get(i), "下发错误", Constants.errorLogUrl, keysign);
				}
			}
			companyUser.setIssued(issued);
			companyUserService.update(companyUser);
		}
		return;
	}

	private String isPhoto(TbCompanyUser companyUser) throws Exception {

		String photo = redisUtils.get("photo_" + companyUser.getCompanyId() + "_" + companyUser.getIdNO());
		if (photo == null) {
			// String filePath = "E:\\sts-space\\photoCache\\staff\\" +
			// companyUser.getUserName()+ companyUser.getCompanyId() + ".jpg";

			String filePath = Constants.StaffPath + "/" + companyUser.getUserName() + companyUser.getCompanyId()
					+ ".jpg";
			File file = new File(filePath);
			if (!file.exists()) {
				logger.otherError(companyUser.getUserName() + "无照片");
				String keysign = towerInforService.findOrgId() + towerInforService.findPospCode()
						+ towerInforService.findKey();
				//logger.sendErrorLog(towerInforService.findOrgId(), companyUser.getUserName() + "该用户无照片", "", "数据错误",
				//		Constants.errorLogUrl, keysign);
				return null;
			} else {
				photo = Base64_2.encode(FilesUtils.getBytesFromFile(file));
				redisUtils.set("photo_" + companyUser.getCompanyId() + "_" + companyUser.getIdNO(), photo);
			}
		}
		return photo;
	}

	private boolean setUser(Devices device, TbCompanyUser companyUser) throws UnsupportedEncodingException {
		String strCardNo = "S" + companyUser.getCompanyUserId();
		boolean setCard = hcNetSDKService.setCardInfo(device.getDeviceIp(), companyUser.getCompanyUserId(),
				companyUser.getUserName(), strCardNo, "normal");
		if (!setCard) {
			return false;
		}

		return hcNetSDKService.setFace(device.getDeviceIp(), strCardNo, companyUser);
	}

	private String getDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}

	/**
	 * 下发IPC人像时所需照片附加信息文件
	 * 
	 * @param user
	 * @return
	 */
	public File IPCxmlFile(TbCompanyUser user) {
		// TODO Auto-generated method stub
		String filePath = Constants.StaffPath + "/" + user.getUserName() + user.getCompanyId() + ".xml";
		File filepath = new File(Constants.StaffPath);
		if (!filepath.exists()) {
			filepath.mkdirs();
		}
		File file = new File(filePath);
		
		StringBuilder builder = new StringBuilder();
		builder.append("<FaceAppendData><name>S");
		builder.append(user.getUserName());
		builder.append("</name><certificateType>ID</certificateType><certificateNumber>");
		builder.append(user.getCompanyUserId());
		builder.append("</certificateNumber></FaceAppendData>");
		
		OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(file, false),"UTF-8");
			StringBuilder outputString = new StringBuilder();
			outputString.append(builder.toString());
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
		return file;
	}

}
