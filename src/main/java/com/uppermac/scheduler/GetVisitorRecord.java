package com.uppermac.scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.http.entity.StringEntity;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uppermac.data.Constants;
import com.uppermac.data.FaceDevResponse;
import com.uppermac.entity.Devices;
import com.uppermac.entity.FaceReceive;
import com.uppermac.entity.TbCompanyUser;
import com.uppermac.entity.TbVisitor;
import com.uppermac.entity.responseModel.GetStaffScheduleDataResponseModel;
import com.uppermac.entity.responseModel.GetStaffScheduleResponseParentModel;
import com.uppermac.entity.responseModel.GetStaffScheduleVisitorResponseModel;
import com.uppermac.service.DevRelatedService;
import com.uppermac.service.DevicesService;
import com.uppermac.service.FaceReceiveService;
import com.uppermac.service.HCNetSDKService;
import com.uppermac.service.TbCompanyUserService;
import com.uppermac.service.TbVisitorService;
import com.uppermac.service.TowerInforService;
import com.uppermac.utils.Base64_2;
import com.uppermac.utils.FilesUtils;
import com.uppermac.utils.HttpUtil;
import com.uppermac.utils.MyErrorException;
import com.uppermac.utils.MyLog;
import com.uppermac.utils.OkHttpUtil;
import com.uppermac.utils.RedisUtils;
import com.uppermac.utils.ThirdResponseObj;

/**
 * 定时拉取访客访问数据，定时10秒
 * 
 * @author Linfu
 *
 */
public class GetVisitorRecord implements Job {

	@Resource
	private RedisUtils redisUtils;

	private MyLog logger = new MyLog(GetVisitorRecord.class);

	OkHttpUtil okHttpUtil = new OkHttpUtil();

	@Autowired
	private TowerInforService towerInforService;
	
	@Autowired
	private TbVisitorService staffService;
	
	@Autowired
	private DevicesService devicesService;
	
	@Autowired
	private TbCompanyUserService companyUserService;

	@Autowired
	private DevRelatedService devRelatedService;

	@Autowired
	private FaceReceiveService faceReceiveService;
	
	@Autowired
	private HCNetSDKService hcNetSDKService;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		try {
			if(!"1".equals(towerInforService.findDeviceType())) {
				getStaff();
			}else {
				return;
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getStaff() throws Exception {
		if (!towerInforService.findOrgId().isEmpty()) {
			// System.out.println("开始拉取数据");
			String towerNumber = towerInforService.findOrgId();
			StringBuilder stringBuilder = new StringBuilder();
			// 拉取的数据地址
			stringBuilder.append(Constants.baseURl);
			stringBuilder.append(Constants.pullStaffUrl);
			stringBuilder.append("/");
			stringBuilder.append(towerInforService.findPospCode());
			stringBuilder.append("/");
			stringBuilder.append(towerNumber.trim());
			stringBuilder.append("/");
			stringBuilder.append(Constants.page);
			stringBuilder.append("/");
			stringBuilder.append(Constants.PAGENUMBER);

			String url = stringBuilder.toString();
			logger.info("拉取地址：" + url);

			String responseContent = okHttpUtil.get(url);

			if (responseContent.isEmpty()) {
				logger.otherError("没获取到访问数据");
				String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
				//logger.sendErrorLog(towerInforService.findOrgId(), "没获取到访客访问的数据", "","数据错误", Constants.errorLogUrl,keysign);
				return;
			}

			// 返回数据转成json模式
			GetStaffScheduleResponseParentModel getStaffScheduleResponseParentModel = JSON.parseObject(responseContent,
					GetStaffScheduleResponseParentModel.class);
			if (getStaffScheduleResponseParentModel == null) {
				logger.otherError("返回数据格式不正确");
				String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
				//logger.sendErrorLog(towerInforService.findOrgId(), "访客访问的返回数据格式不正确", "","数据错误", Constants.errorLogUrl,keysign);
				return;
			}

			// 获取其数据
			GetStaffScheduleDataResponseModel getStaffScheduleDataResponseModel = getStaffScheduleResponseParentModel
					.getData();

			// 数据分析
			List<GetStaffScheduleVisitorResponseModel> getStaffScheduleVisitorResponseModels = getStaffScheduleDataResponseModel
					.getRows();
			List<TbVisitor> staffList = staffService.findByIssued("1");

			if ((getStaffScheduleVisitorResponseModels == null)
					|| (getStaffScheduleVisitorResponseModels.size() <= 0)) {
				logger.warn("无访问数据内容");
			} else {
				for (GetStaffScheduleVisitorResponseModel getStaffScheduleVisitorResponseModel : getStaffScheduleVisitorResponseModels) {
					TbVisitor tbStaff = new TbVisitor();
					tbStaff.setCity(getStaffScheduleVisitorResponseModel.getCity());
					tbStaff.setEnddate(getStaffScheduleVisitorResponseModel.getEndDate());
					tbStaff.setOrgcode(getStaffScheduleVisitorResponseModel.getOrgCode());
					tbStaff.setProvince(getStaffScheduleVisitorResponseModel.getProvince());
					tbStaff.setSolecode(getStaffScheduleVisitorResponseModel.getSoleCode());
					tbStaff.setStartdate(getStaffScheduleVisitorResponseModel.getStartDate());
					//访问时间提前半小时有效
					String preTime = preStartTime(getStaffScheduleVisitorResponseModel.getStartDate(), -30);
					tbStaff.setPrestartdate(preTime);
					tbStaff.setUserrealname(getStaffScheduleVisitorResponseModel.getUserRealName());
					tbStaff.setVisitdate(getStaffScheduleVisitorResponseModel.getVisitDate());
					tbStaff.setVisittime(getStaffScheduleVisitorResponseModel.getVisitTime());
					tbStaff.setVistorrealname(getStaffScheduleVisitorResponseModel.getVistorRealName());
					tbStaff.setDatetype(getStaffScheduleVisitorResponseModel.getDateType());
					tbStaff.setDelflag("1");
					if("QRCODE".equals(towerInforService.findVisitorCheckType())) {
						tbStaff.setIssued("0");
					}else {
						tbStaff.setIssued("1");
					}
					tbStaff.setIsposted("0");
					tbStaff.setVisitId(getStaffScheduleVisitorResponseModel.getVisitId());
					tbStaff.setIdNO(getStaffScheduleVisitorResponseModel.getUserIdNO());
					tbStaff.setPhoto(getStaffScheduleVisitorResponseModel.getPhoto());
					tbStaff.setVisitorIdNO(getStaffScheduleVisitorResponseModel.getVisitorIdNO());
					String uIdStaffId = UUID.randomUUID().toString().replaceAll("\\-", "");
					tbStaff.setStaffId(uIdStaffId);
					
					// 存数据
					staffService.save(tbStaff);
					// 向云端确认存储
					confirmReceiveData(towerNumber);
					if("QRCODE".equals(towerInforService.findVisitorCheckType())) {
						
						return;
					}else {
						if (getStaffScheduleVisitorResponseModel.getPhoto() != null) {
							redisUtils.set("photo_" + uIdStaffId, getStaffScheduleVisitorResponseModel.getPhoto());
							
							byte[] photoKey = Base64_2.decode(getStaffScheduleVisitorResponseModel.getPhoto());
							String fileName = getStaffScheduleVisitorResponseModel.getUserRealName()
									+ getStaffScheduleVisitorResponseModel.getVisitId() + ".jpg";
							FilesUtils.getFileFromBytes(photoKey, Constants.VisitorPath, fileName);
							
							//FilesUtils.getFileFromBytes(photoKey, "E:\\sts-space\\photoCache\\visitor", fileName);
						} else {
							logger.otherError(getStaffScheduleVisitorResponseModel.getUserRealName() + "该用户无照片");
							continue;
						}
						staffList.add(tbStaff);
					}
				}
			}
			if (staffList.size() == 0 || staffList == null) {
				logger.info("无访客下发数据");
				return;
			}
			sendFaceData(staffList);
		}

	}

	// 向云端发送确认收取
	private void confirmReceiveData(String towerNumber) throws Exception {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Constants.baseURl);
		stringBuilder.append(Constants.confirmReceiveUrl);
		stringBuilder.append("/");
		stringBuilder.append(towerInforService.findPospCode());
		stringBuilder.append("/");
		stringBuilder.append(towerNumber);
		stringBuilder.append("/");
		stringBuilder.append("T");
		String url = stringBuilder.toString();
		System.out.println("responseContent-->" + url);
		String responseContent = okHttpUtil.get(url);
		System.out.println("responseContent-->" + responseContent);
	}

	private void sendFaceData(List<TbVisitor> visitors) throws Exception {
		// TODO Auto-generated method stub
		
		// 访客下发名单
		for (TbVisitor visitor : visitors) {
			// 该访客是否还有员工身份
			TbCompanyUser companyUser = companyUserService.findByNameAndIdNO(visitor.getUserrealname(),visitor.getIdNO(),"normal");
			if (null == companyUser) {
				logger.info("正常下发访客");
				// 正常下发
				if (visitor.getIsposted().equals("1")) {
					//失败数据再下发
					doPosted(visitor);
				} else {
					//新数据下发
					doFirstPost(visitor) ;
				}
			} else {
				// 访客通过被访者所在楼层将访客信息下发到指定设备
				TbCompanyUser interviewee = companyUserService.findByNameAndIdNO(visitor.getVistorrealname(),visitor.getVisitorIdNO(),"normal");
				if(null == interviewee) {
					continue;
				}
				String floor = interviewee.getCompanyFloor();
				// 被访者相关联设备,即访客需要下发的设备
				List<String> allinterDecive = devRelatedService.getAllFaceDeviceIP(floor);
				// 访客的员工身份相关联设备
				List<String> allStaffDecive = devRelatedService.getAllFaceDeviceIP(companyUser.getCompanyFloor());
				for (int i = 0; i < allinterDecive.size(); i++) {
					//访客员工身份的相关联设备是否与访问楼层相关联的设备重叠
					if (allStaffDecive.contains(allinterDecive.get(i)) ) {
						if(companyUser.getIssued().equals("1")) {
							//失败记录表查找该员工下发失败的设备
							List<FaceReceive> failRecord = faceReceiveService.findByNameAndid(companyUser.getUserName(), companyUser.getIdNO(), "staff");
							if(null == failRecord) {
								//
								logger.info("失败记录表无访客的员工身份信息");
								continue;
							}else {
								for(FaceReceive faileData:failRecord) {
									if(faileData.getFaceIp().equals(allinterDecive.get(i))) {
										//指定员工下发指定设备
										logger.info(allinterDecive.get(i)+"接收访客信息");
										sendPointDevice(visitor,allinterDecive.get(i));
									}else {
										continue;
									}
								}
							}
						}else {
							visitor.setIssued("0");
							visitor.setIsposted("1");
							staffService.update(visitor);
							continue;
						}
					} else {
						//指定员工下发指定设备
						sendPointDevice(visitor,allinterDecive.get(i));
					}
				}

			}

		}

	}
	//指定一台设备下发
	private void sendPointDevice(TbVisitor vistor, String deviceIp) throws Exception {
		String idNO = vistor.getVisitorIdNO();
		String visitorname = vistor.getVistorrealname();

		if (idNO == null || visitorname == null) {
			logger.warn("被访人证件号或者姓名为空，找不到该员工数据");
			return;
		} else {
			TbCompanyUser companyUser = companyUserService.findByNameAndIdNO(visitorname, idNO,"normal");

			if (null == companyUser) {
				return;
			}

			String photo = isPhoto(vistor);
			if (photo == null) {
				return;
			}
			if (null != deviceIp) {
				logger.info("需下发的人像识别仪器IP为：" + deviceIp);
				
				Devices device = devicesService.findByDeviceIp(deviceIp);
				if(null == device) {
					logger.otherError("设备表缺少IP为"+deviceIp+"的设备");
					return;
				}
				boolean isSuccess = true;
				if(device.getDeviceType().equals("TPS980")) {
					isSuccess = this.sendWhiteList(deviceIp, vistor, photo);
				}else if(device.getDeviceType().equals("DS-K5671")) {
					isSuccess = setUser(device,vistor);
				}else if(device.getDeviceType().equals("DS-2CD8627FWD")) {
					File picAppendData = IPCxmlFile(vistor);
					String filePath = Constants.VisitorPath+"/" + vistor.getUserrealname() + vistor.getVisitId()+ ".jpg";
					File picture = new File(filePath);
					isSuccess = hcNetSDKService.sendToIPC(deviceIp, picture, picAppendData, null, vistor);
				}
				logger.info("下发数据结果" + isSuccess);
				if(vistor.getIsposted().equals("0")) {
					String issued = "0";
					FaceReceive faceReceive = new FaceReceive();
					if (isSuccess == false) {
						issued = "1";
						faceReceive.setFaceIp(deviceIp);
						faceReceive.setIdCard(companyUser.getIdNO());
						faceReceive.setUserName(companyUser.getUserName());
						faceReceive.setReceiveFlag("1");
						faceReceive.setUserType("visitor");
						faceReceive.setOpera("save");
						faceReceive.setReceiveTime(getDateTime());
						faceReceive.setVisitorUUID(vistor.getStaffId());
						faceReceiveService.save(faceReceive);
					}

					vistor.setIssued(issued);
					vistor.setIsposted("1");
					staffService.update(vistor);
				}else {
					String issued = "0";
					FaceReceive facefail = faceReceiveService.findByVisitorUUId(vistor.getStaffId());
					if (isSuccess == false) {
						issued="1";
					}else {
						issued = "0";
						facefail.setReceiveFlag("0");
						faceReceiveService.update(facefail);
					}
					vistor.setIssued(issued);
					vistor.setIsposted("1");
					staffService.update(vistor);
				}
				
			}
		}
	}

	private void doFirstPost(TbVisitor visitor) throws Exception {
		
		String idNO = visitor.getVisitorIdNO();
		String visitorname = visitor.getVistorrealname();

		if (idNO == null || visitorname == null) {
			logger.warn("被访人证件号或者姓名为空，找不到该员工数据");
			return;
		} else {
			TbCompanyUser companyUser = companyUserService.findByNameAndIdNO(visitorname, idNO,"normal");

			if (null == companyUser) {
				return;
			}

			String companyfloor = null;
			if (null != companyUser.getCompanyFloor()) {
				companyfloor = companyUser.getCompanyFloor();
			}
			List<String> allFaceDecive = devRelatedService.getAllFaceDeviceIP(companyfloor);
			String photo = isPhoto(visitor);
			if (photo == null) {
				return;
			}
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
						isSuccess = this.sendWhiteList((String) allFaceDecive.get(i), visitor, photo);
					}else if(device.getDeviceType().equals("DS-K5671")) {
						isSuccess = setUser(device,visitor);
					}else if(device.getDeviceType().equals("DS-2CD8627FWD")) {
						File picAppendData = IPCxmlFile(visitor);
						String filePath = Constants.VisitorPath+"/" + visitor.getUserrealname() + visitor.getVisitId()+ ".jpg";
						File picture = new File(filePath);
						isSuccess = hcNetSDKService.sendToIPC((String) allFaceDecive.get(i), picture, picAppendData, null, visitor);
					}
					visitor.setIsposted("1");
					logger.info("下发数据结果" + isSuccess);
					FaceReceive faceReceive = new FaceReceive();
					if (isSuccess == false) {
						issued = "1";
						faceReceive.setFaceIp(allFaceDecive.get(i));
						faceReceive.setIdCard(visitor.getIdNO());
						faceReceive.setUserName(visitor.getUserrealname());
						faceReceive.setReceiveFlag("1");
						faceReceive.setUserType("visitor");
						faceReceive.setOpera("save");
						faceReceive.setReceiveTime(getDateTime());
						faceReceive.setVisitorUUID(visitor.getStaffId());
						faceReceiveService.save(faceReceive);
					}
					continue;
				}
				visitor.setIssued(issued);
				staffService.update(visitor);
			}
			return;
		}

	}

	private void doPosted(TbVisitor vistor) throws Exception {
		
		if (vistor.getIssued().equals("0")) {
			return;
		} else {
			List<FaceReceive> faceReceiveList = faceReceiveService.findByFaceFlag("1","visitor");
			if (faceReceiveList.size() <= 0) {
				return;
			} else {
				String issued = "0";
				for (FaceReceive faceReceive : faceReceiveList) {
					
					TbVisitor tbStaff = staffService.findByUUID(faceReceive.getVisitorUUID());
					if(null == tbStaff) {
						continue;
					}
					String photo = isPhoto(tbStaff);
					if (photo == null) {
						logger.otherError("缺失照片");
						String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
						///logger.sendErrorLog(towerInforService.findOrgId(), tbStaff.getUserrealname()+"缺失照片", "","数据错误", Constants.errorLogUrl,keysign);
						continue;
					}
					Devices device = devicesService.findByDeviceIp(faceReceive.getFaceIp());
					if(null == device) {
						logger.otherError("设备表缺少IP为"+faceReceive.getFaceIp()+"的设备");
						return;
					}
					boolean isSuccess = true;
					if(device.getDeviceType().equals("TPS980")) {
						isSuccess = this.sendWhiteList(faceReceive.getFaceIp(), tbStaff, photo);
					}else if(device.getDeviceType().equals("DS-K5671")) {
						isSuccess = setUser(device,vistor);
					}else if(device.getDeviceType().equals("DS-2CD8627FWD")) {
						File picAppendData = IPCxmlFile(vistor);
						String filePath = Constants.VisitorPath+"/" + vistor.getUserrealname() + vistor.getVisitId()+ ".jpg";
						File picture = new File(filePath);
						isSuccess = hcNetSDKService.sendToIPC(faceReceive.getFaceIp(), picture, picAppendData, null, vistor);
					}
					//boolean isSuccess =false;
					if (isSuccess == false) {
						issued = "1";
						logger.otherError("失败名单下发" + tbStaff.getUserrealname() + "再次失败");
						String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
						//logger.sendErrorLog(towerInforService.findOrgId(), "失败名单下发" + tbStaff.getUserrealname() + "再次失败", "人脸设备IP"+faceReceive.getFaceIp(),"下发错误", Constants.errorLogUrl,keysign);
						continue;
					} else {
						faceReceive.setReceiveFlag("0");
						faceReceiveService.update(faceReceive);
					}
				}
				vistor.setIssued(issued);
				staffService.update(vistor);
			}
			return;
		}

	}

	private String isPhoto(TbVisitor vistor) throws Exception {
		String photo = redisUtils.get("photo_" + vistor.getStaffId());
		if (null == photo) {
			String filePath = Constants.VisitorPath+"/" + vistor.getUserrealname() + vistor.getVisitId()+ ".jpg";
			System.out.println(filePath);
			//String filePath = "E:\\sts-space\\photoCache\\visitor\\"+vistor.getUserrealname() + vistor.getSolecode()+ ".jpg";
			File file = new File(filePath);
			if (!file.exists()) {
				logger.otherError(vistor.getUserrealname() + "无照片");
				String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
				//logger.sendErrorLog(towerInforService.findOrgId(), vistor.getUserrealname() + "无照片", "","数据错误", Constants.errorLogUrl,keysign);
				return null;
			} else {
				photo = Base64_2.encode(FilesUtils.getBytesFromFile(file));
				redisUtils.set("photo_" + vistor.getStaffId(), photo);
			}
		}
		return photo;
	}

	

	private boolean sendWhiteList(String deviceIp, TbVisitor visitor, String photo) throws Exception {
	
		JSONObject paramsJson = new JSONObject();
		String URL = "http://" + deviceIp + ":8080/office/addOrDelUser";
		// String option = user.getCurrentStatus().equals("normal") ? "save" : "delete";
		// System.out.println(visitor.getUserrealname()+"++"+visitor.getIdNO());
		paramsJson.put("name", visitor.getUserrealname());
		paramsJson.put("idCard", visitor.getIdNO());
		paramsJson.put("op", "save");
		paramsJson.put("type", "visitor");
		paramsJson.put("imageFile", photo);

		StringEntity entity = new StringEntity(paramsJson.toJSONString(), "UTF-8");
		ThirdResponseObj thirdResponseObj = null;
		entity.setContentType("aaplication/json");
		try {
			thirdResponseObj = HttpUtil.http2Se(URL, entity, "UTF-8");
		} catch (MyErrorException e) {
			logger.otherError("访客数据下发设备" + deviceIp + "错误-->" + e.getMessage());
			String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(),"访客数据下发设备" + deviceIp + "错误" , "人脸设备IP"+deviceIp,"下发错误", Constants.errorLogUrl,keysign);
			//e.sendLog(towerInforService.findOrgId());
		}
		if (thirdResponseObj == null) {
			return false;
		}
		
		FaceDevResponse faceResponse = JSON.parseObject(thirdResponseObj.getResponseEntity(),FaceDevResponse.class);
	
		if ("success".equals(thirdResponseObj.getCode())) {
			logger.info(visitor.getUserrealname()+"下发"+deviceIp+"成功");
		} else {
			logger.otherError(visitor.getUserrealname()+"下发"+deviceIp+"失败");
			String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(),visitor.getUserrealname()+"下发"+deviceIp+"失败" , "人脸设备IP"+deviceIp,"下发错误", Constants.errorLogUrl,keysign);
			return false;
		}
		if("001".equals(faceResponse.getResult())) {
			logger.info("人脸设备接收"+visitor.getUserrealname()+"成功");
			return true;
		}else {
			logger.otherError("人脸设备接收"+visitor.getUserrealname()+"失败，失败原因："+faceResponse.getMessage());
			String keysign = towerInforService.findOrgId()+towerInforService.findPospCode()+towerInforService.findKey();
			//logger.sendErrorLog(towerInforService.findOrgId(),"人脸设备接收"+visitor.getUserrealname()+"失败，失败原因："+faceResponse.getMessage() , "人脸设备IP"+deviceIp,"设备接收错误", Constants.errorLogUrl,keysign);
			return false;
		}

	}

	private boolean setUser(Devices device,TbVisitor vistor) {
		String strCardNo ="V"+ vistor.getVisitId();
		try {
			boolean setCard = hcNetSDKService.setCardInfo(device.getDeviceIp(), Integer.valueOf(vistor.getVisitId()), vistor.getUserrealname(), strCardNo, "normal");
			if(!setCard) {
				return false;
			}
		} catch (NumberFormatException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hcNetSDKService.setVisitorFace(device.getDeviceIp(), vistor);
	}
	
	private String getDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}
	private String preStartTime(String dateTime,int pretime) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date dt = sdf.parse(dateTime); 
		Calendar rightNow = Calendar.getInstance();  
        rightNow.setTime(dt);
        rightNow.add(Calendar.MINUTE, pretime);
       return sdf.format(rightNow.getTime());
	}
	public File IPCxmlFile(TbVisitor visitor) {
		// TODO Auto-generated method stub
		String filePath = Constants.VisitorPath+"/" + visitor.getUserrealname() + visitor.getVisitId()+ ".xml";
		File filepath = new File(Constants.StaffPath);
		if (!filepath.exists()) {
			filepath.mkdirs();
		}
		File file = new File(filePath);
		
		StringBuilder builder = new StringBuilder();
		builder.append("<FaceAppendData><name>V");
		builder.append(visitor.getUserrealname());
		builder.append("</name><certificateType>ID</certificateType><certificateNumber>");
		builder.append(visitor.getVisitId());
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
