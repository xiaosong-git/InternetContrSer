package com.uppermac.scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
import com.uppermac.config.HCNetSDK;
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
import com.uppermac.utils.Base64_2;
import com.uppermac.utils.FilesUtils;
import com.uppermac.utils.MD5Util;
import com.uppermac.utils.MyLog;
import com.uppermac.utils.OkHttpUtil;
import com.uppermac.utils.RedisUtils;

/*
 * 	定时拉取员工数据并下发，定时5分钟
 * 
 * 
 * 
 */
public class GetCompanyUserRecord implements Job {

	@Autowired
	private TowerInforService towerInforService;
	
	private MyLog logger = new MyLog(GetCompanyUserRecord.class);

	OkHttpUtil okHttpUtil = new OkHttpUtil();

	@Autowired
	private DevRelatedService devRelatedService;

	@Autowired
	private TbCompanyUserService companyUserService;

	@Autowired
	private FaceReceiveService faceReceiveService;

	@Resource
	private RedisUtils redisUtils;

	@Autowired
	private DevicesService devicesService;
	
	@Autowired
	private HCNetSDKService hcNetSDKService;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		try {
			if(!towerInforService.findDeviceType().equals("1")) {
				getOrgInformation();
			}else {
				return;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getOrgInformation() throws Exception {
		
		if (towerInforService.findOrgId().isEmpty()) {
			logger.stringNull("大楼编号orgId");
			return;
		}
		logger.info("开始更新大楼员工数据");

		Map<String, String> map = new HashMap<>();
		map.put("org_code", towerInforService.findOrgId());
		String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
		String sign = MD5Util.MD5(keysign);
		map.put("sign", sign);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Constants.baseURl);
		stringBuilder.append(Constants.pullOrgCompanyUrl);
		//stringBuilder.append(Constants.ceshi);
		String url = stringBuilder.toString();
		logger.info("获取员工数据地址：" + url);

		String responseContent = okHttpUtil.post(url, map);

		if (responseContent != null) {
			GetCompanyUserScheduleModel getStaffScheduleResponseParentModel = JSON.parseObject(responseContent,
					GetCompanyUserScheduleModel.class);
			// System.out.println(getStaffScheduleResponseParentModel.toString());
			if (null != getStaffScheduleResponseParentModel.getData()) {
				List<TbCompanyUser> companyUserList = getStaffScheduleResponseParentModel.getData();
				System.out.println(companyUserList.size());
				if (companyUserList == null || companyUserList.size() == 0) {
					logger.warn("无新员工数据!");
				} else {
					for (TbCompanyUser companyUser : companyUserList) {
					//	System.out.println(companyUser.toString());
						companyUser.setIsdel("1");
						companyUser.setIssued("1");
						TbCompanyUser userfind = companyUserService.findByNameAndIdNO(companyUser.getUserName(),
								companyUser.getIdNO(),"normal");
						if (userfind == null) {
							notExitUser(companyUser);
						} else {
							doExitUser(userfind,companyUser);
						}
					}
				}
			}
		} else {
			logger.urlRequsetNull(url);
			//logger.sendErrorLog(towerInforService.findOrgId(), "请求网址"+url+"获取的数据为空", "","网络错误", Constants.errorLogUrl,keysign);
		}

		// 查找今天之前下发错误的
		List<TbCompanyUser> companyUsers = companyUserService.findBeforeToDay(getDate());
		if (companyUsers.size() == 0 || companyUsers == null) {
			return;
		}

		for (TbCompanyUser user : companyUsers) {
			if (!user.getCurrentStatus().equals("normal")) {
				continue;
			}
			String issued = "0";
			List<FaceReceive> faceFails = faceReceiveService.findByName(user.getUserName(),"save");
			if (faceFails.size() == 0 || faceFails == null) {
				logger.otherError("失败记录表无" + user.getUserName() + "数据");
				continue;
			}
			for (FaceReceive faceReceive : faceFails) {
				String photo = isPhoto(user);
				if (photo == null) {
					logger.otherError("缺失照片");
					//logger.sendErrorLog(towerInforService.findOrgId(), user.getUserName()+"缺失照片", "","数据错误", Constants.errorLogUrl,keysign);
					continue;
				}
				Devices device = devicesService.findByDeviceIp(faceReceive.getFaceIp());
				if(null == device) {
					logger.otherError("设备表缺少IP为"+faceReceive.getFaceIp()+"的设备");
					return;
				}
				boolean isSuccess = true;
				if(device.getDeviceType().equals("TPS980")) {
					isSuccess = companyUserService.sendWhiteList(faceReceive.getFaceIp(), user, photo);
				}else if(device.getDeviceType().equals("DS-K5671")) {
					isSuccess = setUser(device,user);
				}else if(device.getDeviceType().equals("DS-2CD8627FWD")) {
					File picAppendData = IPCxmlFile(user);
					String filePath = Constants.StaffPath + "/" + user.getUserName() + user.getCompanyId()
					+ ".jpg";
					File picture = new File(filePath);
					isSuccess = hcNetSDKService.sendToIPC(faceReceive.getFaceIp(), picture, picAppendData,user,null);
				}
				if (isSuccess == false) {
					issued = "1";
					logger.otherError("失败名单下发" + user.getUserName() + "再次失败");
					//logger.sendErrorLog(towerInforService.findOrgId(), "失败名单下发" + user.getUserName() + "再次失败", "人脸设备IP"+faceReceive.getFaceIp(),"下发错误", Constants.errorLogUrl,keysign);
					continue;
				} else {
					faceReceive.setReceiveFlag("0");
					faceReceiveService.update(faceReceive);
				}
			}
			user.setIssued(issued);
			companyUserService.update(user);
		}

		//处理未删除的员工数据
		List<TbCompanyUser> faliList = companyUserService.findFailDel();
		if(faliList.size() == 0||faliList == null) {
			return;
		}else {
			for(TbCompanyUser deluser:faliList) {
				String photo = isPhoto(deluser);
				if(null == photo) {
					return;
				}
				String companyfloor = deluser.getCompanyFloor();
				List<String> allFaceDecive = devRelatedService.getAllFaceDeviceIP(companyfloor);
				if (allFaceDecive.size() > 0) {
					String isdel = "0";
					for (int i = 0; i < allFaceDecive.size(); i++) {

						Devices device = devicesService.findByDeviceIp(allFaceDecive.get(i));
						if(null == device) {
							logger.otherError("设备表缺少IP为"+allFaceDecive.get(i)+"的设备");
							continue;
						}
						boolean isSuccess = true;
						if(device.getDeviceType().equals("TPS980")) {
							isSuccess = companyUserService.sendWhiteList(allFaceDecive.get(i), deluser,photo);
						}else if(device.getDeviceType().equals("DS-K5671")) {
							isSuccess = delUser(device,deluser);
						}else if(device.getDeviceType().equals("DS-2CD8627FWD")) {
							if(null == deluser.getIdFrontImgUrl()) {
								isSuccess = true;
							}else {
								isSuccess = hcNetSDKService.delIPCpicture("staff", deluser.getIdFrontImgUrl());
							}
							
						}
						
						if(isSuccess) {
							logger.info("设备IP"+allFaceDecive.get(i)+"删除"+deluser.getUserName()+"成功");
						}else {
							isdel ="1";
							FaceReceive faceReceive = new FaceReceive();
							faceReceive.setFaceIp(allFaceDecive.get(i));
							faceReceive.setIdCard(deluser.getIdNO());
							faceReceive.setUserName(deluser.getUserName());
							faceReceive.setReceiveFlag("1");
							faceReceive.setUserType("staff");
							faceReceive.setOpera("delete");
							faceReceive.setReceiveTime(getDateTime());
							faceReceiveService.save(faceReceive);
						}
					}
					deluser.setIsdel(isdel);
					companyUserService.update(deluser);
				}	
			}
		}
	}

	private void notExitUser(TbCompanyUser companyUser) throws Exception {

		// 无状态员工不接收
		if (!"normal".equals(companyUser.getCurrentStatus())) {
			logger.info("员工" + companyUser.getUserName() + "的状态是"+companyUser.getCurrentStatus()+",上位机不接收");
			return;
		}
		String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
		TbCompanyUser userfind = companyUserService.findByNameAndIdNO(companyUser.getUserName(),
				companyUser.getIdNO(),"deleted");
		
		if(null != userfind) {
			companyUserService.deleteOne(userfind);
		}
		companyUserService.save(companyUser);

		if (companyUser.getPhoto() != null) {
			redisUtils.set("photo_" + companyUser.getCompanyId() + "_" + companyUser.getIdNO(), companyUser.getPhoto());
			byte[] photoKey = Base64_2.decode(companyUser.getPhoto());
			String fileName = companyUser.getUserName() + companyUser.getCompanyId() + ".jpg";
			FilesUtils.getFileFromBytes(photoKey, Constants.StaffPath, fileName);
			//FilesUtils.getFileFromBytes(photoKey, "E:\\sts-space\\photoCache\\staff", fileName);
		} else {
			logger.otherError((companyUser.getUserName() + "该用户无照片"));
			//logger.sendErrorLog(towerInforService.findOrgId(), companyUser.getUserName() + "该用户无照片", "","数据错误", Constants.errorLogUrl,keysign);
			return;
		}

		String companyfloor = null;
		if (null != companyUser.getCompanyFloor()) {
			companyfloor = companyUser.getCompanyFloor();
		}

		List<String> allFaceDecive = devRelatedService.getAllFaceDeviceIP(companyfloor);
		System.out.println(allFaceDecive.size());
		if (allFaceDecive.size() > 0) {
			String issued = "0";
			for (int i = 0; i < allFaceDecive.size(); i++) {
				logger.info("需下发的人像识别仪器IP为：" + allFaceDecive.get(i));

				Devices device = devicesService.findByDeviceIp(allFaceDecive.get(i));
				if(null == device) {
					logger.otherError("设备表缺少IP为"+allFaceDecive.get(i)+"的设备");
					continue;
				}
				boolean isSuccess = true;
				if(device.getDeviceType().equals("TPS980")) {
					isSuccess = companyUserService.sendWhiteList((String) allFaceDecive.get(i), companyUser,
							companyUser.getPhoto());
				}else if(device.getDeviceType().equals("DS-K5671")) {
					isSuccess = setUser(device,companyUser);
				
				}else if(device.getDeviceType().equals("DS-2CD8627FWD")) {
					File picAppendData = IPCxmlFile(companyUser);
					String filePath = Constants.StaffPath + "/" + companyUser.getUserName() + companyUser.getCompanyId()
					+ ".jpg";
					File picture = new File(filePath);
					isSuccess = hcNetSDKService.sendToIPC((String) allFaceDecive.get(i), picture, picAppendData,companyUser,null);
				}
				// 针对下发失败的需要登记，待下次冲洗下发，已经下发成功的不在下发
				FaceReceive faceReceive = new FaceReceive();

				if (isSuccess == false) {
					issued = "1";
					faceReceive.setFaceIp(allFaceDecive.get(i));
					faceReceive.setIdCard(companyUser.getIdNO());
					faceReceive.setUserName(companyUser.getUserName());
					faceReceive.setReceiveFlag("1");
					faceReceive.setUserType("staff");
					faceReceive.setOpera("save");
					faceReceive.setReceiveTime(getDateTime());
					faceReceiveService.save(faceReceive);
					logger.info("失败表记录" + companyUser.getUserName() + "数据");
				}
			}
			companyUser.setIssued(issued);
			companyUserService.update(companyUser);
		}
	}

	private void doExitUser(TbCompanyUser companyUser,TbCompanyUser newUser) throws Exception {

		if(newUser.getCurrentStatus().equals("normal")) {
			if (companyUser.getIssued().equals("0")) {
				return;
			} else {
				List<FaceReceive> faceReceiveList = faceReceiveService.findByName(companyUser.getUserName(),"save");
				if (faceReceiveList.size() <= 0) {

					return;
				} else {
					
					String issued = "0";
					for (FaceReceive faceReceive : faceReceiveList) {
						TbCompanyUser user = companyUserService.findByNameAndIdNO(faceReceive.getUserName(),
								faceReceive.getIdCard(),"normal");

						String photo = isPhoto(user);
						if (photo == null) {
							String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
							logger.otherError("缺失照片");
							//logger.sendErrorLog(towerInforService.findOrgId(),user.getUserName()+ "缺失照片", "","数据错误", Constants.errorLogUrl,keysign);
							continue;
						}
						Devices device = devicesService.findByDeviceIp(faceReceive.getFaceIp());
						if(null == device) {
							logger.otherError("设备表缺少IP为"+faceReceive.getFaceIp()+"的设备");
							continue;
						}
						boolean isSuccess = true;
						if(device.getDeviceType().equals("TPS980")) {
							isSuccess = companyUserService.sendWhiteList(faceReceive.getFaceIp(), user,
									photo);
						}else if(device.getDeviceType().equals("DS-K5671")) {
							isSuccess = setUser(device,user);
						
						}else if(device.getDeviceType().equals("DS-2CD8627FWD")) {
							File picAppendData = IPCxmlFile(user);
							String filePath = Constants.StaffPath + "/" + user.getUserName() + user.getCompanyId()
							+ ".jpg";
							File picture = new File(filePath);
							isSuccess = hcNetSDKService.sendToIPC(faceReceive.getFaceIp(), picture, picAppendData,user,null);
						}
						if (isSuccess == false) {
							issued = "1";
							logger.otherError("失败名单下发" + user.getUserName() + "再次失败");
							String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
							//logger.sendErrorLog(towerInforService.findOrgId(), "失败名单下发" + user.getUserName() + "再次失败", "人脸设备IP"+faceReceive.getFaceIp(),"设备接收错误", Constants.errorLogUrl,keysign);
							continue;
						} else {
							faceReceive.setReceiveFlag("0");
							faceReceiveService.update(faceReceive);
						}
					}
					companyUser.setIssued(issued);
					companyUserService.update(companyUser);
				}
				return;
			}
		}else if(newUser.getCurrentStatus().equals("deleted")) {
			if(companyUser.getIssued().equals("1")) {
				return;
			}
			String photo = newUser.getPhoto();
			if(null == photo) {
				photo = isPhoto(companyUser);
			}
			String companyfloor = companyUser.getCompanyFloor();
			List<String> allFaceDecive = devRelatedService.getAllFaceDeviceIP(companyfloor);
			if (allFaceDecive.size() > 0) {
				String isdel = "0";
				for (int i = 0; i < allFaceDecive.size(); i++) {
					Devices device = devicesService.findByDeviceIp(allFaceDecive.get(i));
					if(null == device) {
						logger.otherError("设备表缺少IP为"+allFaceDecive.get(i)+"的设备");
						continue;
					}
					boolean isSuccess = true;
					if(device.getDeviceType().equals("TPS980")) {
						companyUserService.sendDelWhiteList(allFaceDecive.get(i), companyUser.getUserName(), companyUser.getIdNO());
					}else if(device.getDeviceType().equals("DS-K5671")) {
						isSuccess = delUser(device,companyUser);
					
					}else if(device.getDeviceType().equals("DS-2CD8627FWD")) {
						if(null == companyUser.getIdFrontImgUrl()) {
							isSuccess = true;
						}else {
							isSuccess = hcNetSDKService.delIPCpicture("staff", companyUser.getIdFrontImgUrl());
						}
					}
					if(isSuccess) {
						logger.info("设备IP"+allFaceDecive.get(i)+"删除"+companyUser.getUserName()+"成功");
					}else {
						isdel ="1";
						FaceReceive faceReceive = new FaceReceive();
						faceReceive.setFaceIp(allFaceDecive.get(i));
						faceReceive.setIdCard(companyUser.getIdNO());
						faceReceive.setUserName(companyUser.getUserName());
						faceReceive.setReceiveFlag("1");
						faceReceive.setUserType("staff");
						faceReceive.setOpera("delete");
						faceReceive.setReceiveTime(getDateTime());
						faceReceiveService.save(faceReceive);
					}
				}
				companyUser.setIsdel(isdel);
				companyUser.setCurrentStatus("deleted");
				companyUserService.update(companyUser);
			}	
		}else {
			
		}
		
	}

	private String isPhoto(TbCompanyUser companyUser) throws Exception {
		String photo = redisUtils.get("photo_" + companyUser.getCompanyId() + "_" + companyUser.getIdNO());
		if (photo == null) {
			String filePath = Constants.StaffPath+"/" + companyUser.getUserName()+ companyUser.getCompanyId() + ".jpg";
			
			//String filePath = "E:\\sts-space\\photoCache\\staff\\" + companyUser.getUserName()+ companyUser.getCompanyId() + ".jpg";
			File file = new File(filePath);
			if (!file.exists()) {
				String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
				logger.otherError(companyUser.getUserName() + "无照片");
				//logger.sendErrorLog(towerInforService.findOrgId(), companyUser.getUserName() + "无照片", "","数据错误", Constants.errorLogUrl,keysign);
				return null;
			} else {
				photo = Base64_2.encode(FilesUtils.getBytesFromFile(file));
				redisUtils.set("photo_" + companyUser.getCompanyId() + "_" + companyUser.getIdNO(), photo);
			}
		}
		return photo;
	}

	private String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}

	private String getDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}
	private boolean setUser(Devices device,TbCompanyUser companyUser) throws UnsupportedEncodingException {
		String strCardNo = "S"+companyUser.getCompanyUserId();
		boolean setCard = hcNetSDKService.setCardInfo(device.getDeviceIp(), companyUser.getCompanyUserId(), companyUser.getUserName(), strCardNo, "normal");
		if(!setCard) {
			return false;
		}
		
		return hcNetSDKService.setFace(device.getDeviceIp(), strCardNo, companyUser);
	}
	private boolean delUser(Devices device,TbCompanyUser companyUser) throws UnsupportedEncodingException {
		String strCardNo = "S"+companyUser.getCompanyUserId();
		return hcNetSDKService.setCardInfo(device.getDeviceIp(), companyUser.getCompanyUserId(), companyUser.getUserName(), strCardNo, "delete");
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
