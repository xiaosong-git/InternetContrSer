package com.uppermac.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uppermac.data.Constants;
import com.uppermac.data.ResponseData;
import com.uppermac.entity.AccessRecord;
import com.uppermac.entity.DeviceRelated;
import com.uppermac.entity.Devices;
import com.uppermac.entity.TbVisitor;
import com.uppermac.entity.responseModel.qRCodeModel.QRCodeCommonMessage;
import com.uppermac.service.AccessRecordService;
import com.uppermac.service.DevRelatedService;
import com.uppermac.service.DevicesService;
import com.uppermac.service.impl.TbVisitorServiceImpl;
import com.uppermac.service.impl.TowerInforServiceImpl;
import com.uppermac.utils.Base64_2;
import com.uppermac.utils.CheckUtils;
import com.uppermac.utils.Control24DeviceUtil;
import com.uppermac.utils.IPUtil;
import com.uppermac.utils.MyLog;


/*
 * 
 * 
 */
@RestController
public class ScannerController {

	
	MyLog logger = new MyLog(ScannerController.class);
	

	@Autowired
	private TbVisitorServiceImpl visitorService;

	@Autowired
	private TowerInforServiceImpl towerInforService;
	

	@Autowired
	private AccessRecordService accessRecordService;
	
	@Autowired
	private DevicesService deviceService;
	
	@Autowired
	private DevRelatedService devRelatedService;
	
	@Autowired
	private CheckUtils checkUtils;
	
	@Value("${qrCode.TotalPages}")
	private String TotalPages;

	/*
	 * 
	 * 读头识别二维码解析数据并校验
	 * 
	 */
	@RequestMapping("/scanS")
	public ResponseData scanContext(HttpServletRequest request) throws Exception {
		String content = request.getParameter("content");
		System.out.println(content);
		String faceRecogIp = IPUtil.getIp(request);
		// 读取扫描二维码数据
	/*	BufferedReader reader = request.getReader();
		if (reader == null) {
			logger.error("二维码扫描数据错误");
			//System.out.println("数据错误");
			return;
		}
		char[] buf = new char[1024];
		int len = 0;
		StringBuffer contentBuffer = new StringBuffer();
		while ((len = reader.read(buf)) != -1) {
			contentBuffer.append(buf, 0, len);
		}

		// 数据解析
		String content = contentBuffer.toString();
		
		//System.out.println(content);
		//读头数据自带表头，占16字节
		if(content.length()>15) {
			content = content.substring(15);
		}*/
		String splitStrings[] = content.split("\\|");
		String commonMessage = splitStrings[0].trim();
		String[] commonMessageSplit = commonMessage.split("\\&");
		
		if (splitStrings.length != 2 || commonMessageSplit.length != 5) {
			logger.otherError("二维码扫描数据错误");
			return new ResponseData("500", "二维码扫描数据错误");
		}
		if (!commonMessageSplit[3].equals(TotalPages)) {
			logger.otherError("二维码扫描数据错误");
			return new ResponseData("500", "二维码扫描数据错误");
		}

		// 解析头部分内容
		QRCodeCommonMessage qrCodeCommonMessage = new QRCodeCommonMessage();
		qrCodeCommonMessage.setIdentifier(commonMessageSplit[0]);
		qrCodeCommonMessage.setBitMapType(commonMessageSplit[1]);
		qrCodeCommonMessage.setCurrentPage(commonMessageSplit[2]);
		qrCodeCommonMessage.setTotalPages(commonMessageSplit[3]);
		qrCodeCommonMessage.setViewTime(commonMessageSplit[4]);
		
		String s =new String(splitStrings[1].trim());
		System.out.println(s);
		// BASE64加密内容解析
		String strByte = new String(Base64_2.decode(splitStrings[1].trim()), "UTF-8");
		
		System.out.println(strByte);
		qrCodeCommonMessage.setContent(strByte);
		List<String> contentStringLists = parseContent(strByte);
		System.out.println(contentStringLists.size());
		if (qrCodeCommonMessage.getBitMapType().equals("1")) {
			return new ResponseData("500", "失败");
		}else if (qrCodeCommonMessage.getBitMapType().equals("2")) {
			String soleCode = contentStringLists.get(1);
			if(StringUtils.isEmpty(soleCode)) {
				logger.otherError("用户唯一标识码soleCode为空");
				return new ResponseData("500", "用户唯一标识码soleCode为空");
			}
			String startTime = "";
			String endTime = "";
			
			if(contentStringLists.size() == 4) {
				 startTime = contentStringLists.get(2);
				 endTime = contentStringLists.get(3);
			}else {
				 startTime = contentStringLists.get(9);
				 endTime = contentStringLists.get(10);
			}
			List<TbVisitor> visitorInfos = visitorService.findByVisitId(soleCode,startTime,endTime);
			System.out.println(visitorInfos.size());
			if(visitorInfos ==null || visitorInfos.size()<=0) {
				logger.otherError("没有该soleCode的访问数据");
				return new ResponseData("500", "没有该soleCode的访问数据");
			}else {
				
				TbVisitor visitorInfo =visitorInfos.get(0);
				
				boolean success = checkUtils.verificationCache(visitorInfo);
				if(success) {
					
					Devices device = deviceService.findByDeviceIp(faceRecogIp);
					if(null == device) {
						logger.otherError("找不到该IP的设备");
						return new ResponseData("500", "找不到该IP的设备");
					}else {
						open(faceRecogIp, visitorInfo.getUserrealname(), visitorInfo.getIdNO(),"visitor");
						return new ResponseData("0", "二维码扫描成功");
					}
				}else {
					logger.otherError("已过访问有效期");
					return new ResponseData("500", "已过访问有效期");
				}
			}
			
		}else if (qrCodeCommonMessage.getBitMapType().equals("3")) {
			return new ResponseData("500", "失败");
		}else {
			return new ResponseData("500", "失败");
		}
	
	}
	
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
	

	/*
	 * 解析“[]”内容
	
	 * SoleCode(contentStringLists.get(0)) 				//唯一身份识别码
	 * RealName(contentStringLists.get(1))				//访客姓名
	 * IdNO(contentStringLists.get(2))					//访客证件号
	 * Province(contentStringLists.get(3))				//访问的省
	 * City(contentStringLists.get(4))					//访问的市
	 * VisitorCompany(contentStringLists.get(5))		//被访问者公司名字
	 * VisitorName(contentStringLists.get(6))			//被访问者名字
	 * Phone(contentStringLists.get(7))					//访问者手机号
	 * HeadImgUrl(contentStringLists.get(8))			//访问者照片(目前不存照片)
	 * StarDate(contentStringLists.get(9))				//访问开始时间
	 * EndDate(contentStringLists.get(10)) 				//访问结束时间 
	 * UserCompanyId(contentStringLists.get(11))		//访问者的公司ID
	 * UserCompanyName(contentStringLists.get(12))		//访问者的公司名字
	 */
	public static List<String> parseContent(String content) {
		List<String> ls = new ArrayList<String>();
		Pattern pattern = Pattern.compile("(\\[[^\\]]*\\])");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String s = matcher.group();
			if (s.length() > 2) {
				s = s.substring(1, s.length() - 1);
			} else {
				s = "";
			}
			ls.add(s);
		}
		return ls;
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
		accessRecord.setDeviceType("QRCODE");
		accessRecord.setDeviceIp(faceIP);
		accessRecord.setUserType(personType);
		accessRecord.setUserName(name);
		accessRecord.setIdCard(idCard);
		accessRecord.setIsSendFlag("F");
		accessRecordService.save(accessRecord);
	}
}
