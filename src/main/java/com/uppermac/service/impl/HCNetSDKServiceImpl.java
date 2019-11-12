package com.uppermac.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.annotation.Resource;
import javax.swing.JOptionPane;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.uppermac.config.HCNetSDK;
import com.uppermac.config.HCNetSDK.NET_DVR_ALARMER;
import com.uppermac.data.Constants;
import com.uppermac.entity.AccessRecord;
import com.uppermac.entity.DeviceRelated;
import com.uppermac.entity.Devices;
import com.uppermac.entity.TbCompanyUser;
import com.uppermac.entity.TbVisitor;
import com.uppermac.service.AccessRecordService;
import com.uppermac.service.DevRelatedService;
import com.uppermac.service.DevicesService;
import com.uppermac.service.HCNetSDKService;
import com.uppermac.service.TbCompanyUserService;
import com.uppermac.service.TbVisitorService;
import com.uppermac.service.TowerInforService;
import com.uppermac.utils.MyLog;
import com.uppermac.utils.RedisUtils;

@Service
public class HCNetSDKServiceImpl implements HCNetSDKService {

	/**
	 * lUserID 用户句柄 dwEmployeeNo 用户工号 name 用户名字
	 * 
	 * 
	 */

	@Autowired
	private TbCompanyUserService companyUserService;

	@Autowired
	private TbVisitorService visitorService;

	@Autowired
	private DevicesService deviceService;

	@Autowired
	private AccessRecordService accessRecordService;

	@Autowired
	private TowerInforService towerInforService;

	@Autowired
	private DevRelatedService devRelatedService;

	@Resource
	private RedisUtils redisUtils;

	private MyLog logger = new MyLog(HCNetSDKServiceImpl.class);

	int lUserID;// 用户句柄
	

	HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

	NativeLong m_lUploadHandle;

	NativeLong m_UploadStatus;

	@Override
	public boolean setCardInfo(String deviceIP, int dwEmployeeNo, String name, String strCardNo, String isdel)
			throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		lUserID = initAndLogin(deviceIP);
		if(lUserID < 0) {
			return false;
		}
		int iErr = 0;

		// 设置卡参数
		HCNetSDK.NET_DVR_CARD_CFG_COND m_struCardInputParamSet = new HCNetSDK.NET_DVR_CARD_CFG_COND();
		m_struCardInputParamSet.read();
		m_struCardInputParamSet.dwSize = m_struCardInputParamSet.size();
		m_struCardInputParamSet.dwCardNum = 1;
		m_struCardInputParamSet.byCheckCardNo = 1;

		Pointer lpInBuffer = m_struCardInputParamSet.getPointer();
		m_struCardInputParamSet.write();

		Pointer pUserData = null;
		FRemoteCfgCallBackCardSet fRemoteCfgCallBackCardSet = new FRemoteCfgCallBackCardSet();

		int lHandle = this.hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_SET_CARD_CFG_V50, lpInBuffer,
				m_struCardInputParamSet.size(), fRemoteCfgCallBackCardSet, pUserData);
		if (lHandle < 0) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("建立长连接失败，错误号：" + iErr);
			return false;
		}

		HCNetSDK.NET_DVR_CARD_CFG_V50 struCardInfo = new HCNetSDK.NET_DVR_CARD_CFG_V50(); // 卡参数
		struCardInfo.read();
		struCardInfo.dwSize = struCardInfo.size();
		struCardInfo.dwModifyParamType = 0x6DAF;// 0x00000001 + 0x00000002 + 0x00000004 + 0x00000008 +
		// 0x00000010 + 0x00000020 + 0x00000080 + 0x00000100 + 0x00000200 + 0x00000400 +
		// 0x00000800;
		/***
		 * #define CARD_PARAM_CARD_VALID 0x00000001 //卡是否有效参数 #define CARD_PARAM_VALID
		 * 0x00000002 //有效期参数 #define CARD_PARAM_CARD_TYPE 0x00000004 //卡类型参数 #define
		 * CARD_PARAM_DOOR_RIGHT 0x00000008 //门权限参数 #define CARD_PARAM_LEADER_CARD
		 * 0x00000010 //首卡参数 #define CARD_PARAM_SWIPE_NUM 0x00000020 //最大刷卡次数参数 #define
		 * CARD_PARAM_GROUP 0x00000040 //所属群组参数 #define CARD_PARAM_PASSWORD 0x00000080
		 * //卡密码参数 #define CARD_PARAM_RIGHT_PLAN 0x00000100 //卡权限计划参数 #define
		 * CARD_PARAM_SWIPED_NUM 0x00000200 //已刷卡次数 #define CARD_PARAM_EMPLOYEE_NO
		 * 0x00000400 //工号 #define CARD_PARAM_NAME 0x00000800 //姓名
		 */
		for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++) {
			struCardInfo.byCardNo[i] = 0;
		}
		for (int i = 0; i < strCardNo.length(); i++) {
			struCardInfo.byCardNo[i] = strCardNo.getBytes()[i];
		}
		if ("delete".equals(isdel)) {
			struCardInfo.byCardValid = 0;// 0-无效,1-有效
		} else {
			struCardInfo.byCardValid = 1;
		}
		struCardInfo.byCardType = 1;
		struCardInfo.byLeaderCard = 0;
		struCardInfo.byDoorRight[0] = 1; // 门1有权限
		struCardInfo.wCardRightPlan[0].wRightPlan[0] = 1; // 门1关联卡参数计划模板1

		// 卡有效期
		struCardInfo.struValid.byEnable = 1;
		struCardInfo.struValid.struBeginTime.wYear = 2010;
		struCardInfo.struValid.struBeginTime.byMonth = 12;
		struCardInfo.struValid.struBeginTime.byDay = 1;
		struCardInfo.struValid.struBeginTime.byHour = 0;
		struCardInfo.struValid.struBeginTime.byMinute = 0;
		struCardInfo.struValid.struBeginTime.bySecond = 0;
		struCardInfo.struValid.struEndTime.wYear = 2024;
		struCardInfo.struValid.struEndTime.byMonth = 12;
		struCardInfo.struValid.struEndTime.byDay = 1;
		struCardInfo.struValid.struEndTime.byHour = 0;
		struCardInfo.struValid.struEndTime.byMinute = 0;
		struCardInfo.struValid.struEndTime.bySecond = 0;

		struCardInfo.dwMaxSwipeTime = 0; // 无次数限制
		struCardInfo.dwSwipeTime = 0;
		struCardInfo.byCardPassword = "123456".getBytes();
		struCardInfo.dwEmployeeNo = dwEmployeeNo;
		struCardInfo.wSchedulePlanNo = 1;
		struCardInfo.bySchedulePlanType = 2;
		struCardInfo.wDepartmentNo = 1;

		byte[] strCardName = name.getBytes("GBK");
		for (int i = 0; i < HCNetSDK.NAME_LEN; i++) {
			struCardInfo.byName[i] = 0;
		}
		for (int i = 0; i < strCardName.length; i++) {
			struCardInfo.byName[i] = strCardName[i];
		}

		struCardInfo.write();
		Pointer pSendBufSet = struCardInfo.getPointer();

		if (!hCNetSDK.NET_DVR_SendRemoteConfig(lHandle, 0x3, pSendBufSet, struCardInfo.size())) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("ENUM_ACS_SEND_DATA失败，错误号：" + iErr);
			return false;
		}

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!hCNetSDK.NET_DVR_StopRemoteConfig(lHandle)) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("断开长连接失败，错误号：" + iErr);
			return false;
		}
		logger.otherError("断开长连接成功!");
		return true;
	}

	/**
	 * 
	 * strCardNo 人脸关联的卡号
	 * 
	 */
	@Override
	public boolean setFace(String deviceIP, String strCardNo, TbCompanyUser companyUser)
			throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		lUserID = initAndLogin(deviceIP);
		if(lUserID<0) {
			return false;
		}
		int iErr = 0; // 错误号

		// 设置人脸参数
		HCNetSDK.NET_DVR_FACE_PARAM_COND m_struFaceSetParam = new HCNetSDK.NET_DVR_FACE_PARAM_COND();
		m_struFaceSetParam.dwSize = m_struFaceSetParam.size();

		// String strCardNo = "201909";// 人脸关联的卡号
		for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++) {
			m_struFaceSetParam.byCardNo[i] = 0;
		}
		System.arraycopy(strCardNo.getBytes(), 0, m_struFaceSetParam.byCardNo, 0, strCardNo.length());

		m_struFaceSetParam.byEnableCardReader[0] = 1;
		m_struFaceSetParam.dwFaceNum = 1;
		m_struFaceSetParam.byFaceID = 1;
		m_struFaceSetParam.write();

		Pointer lpInBuffer = m_struFaceSetParam.getPointer();

		Pointer pUserData = null;
		FRemoteCfgCallBackFaceSet fRemoteCfgCallBackFaceSet = new FRemoteCfgCallBackFaceSet();

		int lHandle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_SET_FACE_PARAM_CFG, lpInBuffer,
				m_struFaceSetParam.size(), fRemoteCfgCallBackFaceSet, pUserData);
		if (lHandle < 0) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("建立长连接失败，错误号：" + iErr);
			return false;
		}
		
		HCNetSDK.NET_DVR_FACE_PARAM_CFG struFaceInfo = new HCNetSDK.NET_DVR_FACE_PARAM_CFG(); // 卡参数
		struFaceInfo.read();
		struFaceInfo.dwSize = struFaceInfo.size();

		// strCardNo = "201909";// 人脸关联的卡号
		for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++) {
			struFaceInfo.byCardNo[i] = 0;
		}
		
		struFaceInfo.byEnableCardReader[0] = 1; // 需要下发人脸的读卡器，按数组表示，每位数组表示一个读卡器，数组取值：0-不下发该读卡器，1-下发到该读卡器
		struFaceInfo.byFaceID = 1; // 人脸ID编号，有效取值范围：1~2
		struFaceInfo.byFaceDataType = 1; // 人脸数据类型：0- 模板（默认），1- 图片

		/*****************************************
		 * 从本地文件里面读取JPEG图片二进制数据
		 *****************************************/
		FileInputStream picfile = null;
		int picdataLength = 0;
		try {
			String filePath = Constants.StaffPath + "/" + companyUser.getUserName() + companyUser.getCompanyId()
					+ ".jpg";
			
			File picture = new File(filePath);
			if (!picture.exists()) {
				return false;
			}
			
			picfile = new FileInputStream(picture);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		}

		try {
			picdataLength = picfile.available();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (picdataLength < 0) {
			System.out.println("input file dataSize < 0");
			logger.otherError("照片数据错误");
			return false;
		}

		HCNetSDK.BYTE_ARRAY ptrpicByte = new HCNetSDK.BYTE_ARRAY(picdataLength);
		try {
			picfile.read(ptrpicByte.byValue);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		ptrpicByte.write();
		/**************************/

		struFaceInfo.dwFaceLen = picdataLength;
		struFaceInfo.pFaceBuffer = ptrpicByte.getPointer();

		struFaceInfo.write();
		Pointer pSendBufSet = struFaceInfo.getPointer();
		System.out.println(lHandle + "*" + pSendBufSet + "*" + 0x9 + "*" + struFaceInfo.size());
		// ENUM_ACS_INTELLIGENT_IDENTITY_DATA = 9, //智能身份识别终端数据类型，下发人脸图片数据
		if (!hCNetSDK.NET_DVR_SendRemoteConfig(lHandle, 0x9, pSendBufSet, struFaceInfo.size())) {

			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("NET_DVR_SendRemoteConfig失败，错误号：" + iErr);
			return false;
		}

		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 
		if(fRemoteCfgCallBackFaceSet.sendFlag !=1 || fRemoteCfgCallBackFaceSet.faceFlag !=1) {
			logger.otherError("下发人脸参数失败");
			return false;
		}
		if (!hCNetSDK.NET_DVR_StopRemoteConfig(lHandle)) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("断开长连接失败，错误号：" + iErr);
			return false;
		}
		return true;
	}

	@Override
	public boolean delFace(String deviceIP,String idCardNo) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		int iErr = 0;
		// 删除人脸数据
		HCNetSDK.NET_DVR_FACE_PARAM_CTRL m_struFaceDel = new HCNetSDK.NET_DVR_FACE_PARAM_CTRL();
		m_struFaceDel.dwSize = m_struFaceDel.size();
		m_struFaceDel.byMode = 0; // 删除方式：0- 按卡号方式删除，1- 按读卡器删除

		m_struFaceDel.struProcessMode.setType(HCNetSDK.NET_DVR_FACE_PARAM_BYCARD.class);
		m_struFaceDel.struProcessMode.struByCard.byCardNo = idCardNo.getBytes();// 需要删除人脸关联的卡号
		m_struFaceDel.struProcessMode.struByCard.byEnableCardReader[0] = 1; // 读卡器
		m_struFaceDel.struProcessMode.struByCard.byFaceID[0] = 1; // 人脸ID
		m_struFaceDel.write();

		Pointer lpInBuffer = m_struFaceDel.getPointer();

		boolean lRemoteCtrl = hCNetSDK.NET_DVR_RemoteControl(lUserID, HCNetSDK.NET_DVR_DEL_FACE_PARAM_CFG, lpInBuffer,
				m_struFaceDel.size());
		if (!lRemoteCtrl) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("删除人脸图片失败，错误号：" + iErr);
			return false;
		} else {
			logger.info("删除人脸图片成功!");
			return true;
		}
	}

	class FRemoteCfgCallBackCardSet implements HCNetSDK.FRemoteConfigCallback {

		public int sendFlag = -1;		//卡状态下发返回标记（1成功，-1失败,0正在下发）
		
		public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
			switch (dwType) {
			case 0:// NET_SDK_CALLBACK_TYPE_STATUS
				HCNetSDK.BYTE_ARRAY struCallbackStatus = new HCNetSDK.BYTE_ARRAY(40);
				struCallbackStatus.write();
				Pointer pStatus = struCallbackStatus.getPointer();
				pStatus.write(0, lpBuffer.getByteArray(0, struCallbackStatus.size()), 0, dwBufLen);
				struCallbackStatus.read();

				int iStatus = 0;
				byte[] byCardNo;
				for (int i = 0; i < 4; i++) {
					int ioffset = i * 8;
					int iByte = struCallbackStatus.byValue[i] & 0xff;
					iStatus = iStatus + (iByte << ioffset);
				}

				switch (iStatus) {
				case 1000:// NET_SDK_CALLBACK_STATUS_SUCCESS
					logger.info("下发卡参数成功");
					sendFlag = 1;
					break;
				case 1001:
					byCardNo = new byte[32];
					System.arraycopy(struCallbackStatus.byValue, 4, byCardNo, 0, 32);
					logger.info("正在下发卡参数中,卡号:" + new String(byCardNo).trim());
					sendFlag = 0;
					break;
				case 1002:
					int iErrorCode = 0;
					for (int i = 0; i < 4; i++) {
						int ioffset = i * 8;
						int iByte = struCallbackStatus.byValue[i + 4] & 0xff;
						iErrorCode = iErrorCode + (iByte << ioffset);
					}
					byCardNo = new byte[32];
					System.arraycopy(struCallbackStatus.byValue, 8, byCardNo, 0, 32);
					logger.otherError("下发卡参数失败,卡号:"+ new String(byCardNo).trim() + ",错误号:" + iErrorCode);
					sendFlag = -1;
					break;
				}
				break;
			default:
				break;
			}
		}
	}
	/***
	 * 
	 * 门禁设备人脸下发回调函数
	 * @author Admin
	 *
	 */
	class FRemoteCfgCallBackFaceSet implements HCNetSDK.FRemoteConfigCallback {
		
		public int faceFlag = -1; 	//人脸数据状态
		public int sendFlag = -1;	//人脸下发状态
		
		public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
			switch (dwType) {
			case 0:// NET_SDK_CALLBACK_TYPE_STATUS
				HCNetSDK.BYTE_ARRAY struCallbackStatus = new HCNetSDK.BYTE_ARRAY(40);
				struCallbackStatus.write();
				Pointer pStatus = struCallbackStatus.getPointer();
				pStatus.write(0, lpBuffer.getByteArray(0, struCallbackStatus.size()), 0, dwBufLen);
				struCallbackStatus.read();

				int iStatus = 0;
				byte[] byCardNo;

				for (int i = 0; i < 4; i++) {
					int ioffset = i * 8;
					int iByte = struCallbackStatus.byValue[i] & 0xff;
					iStatus = iStatus + (iByte << ioffset);
				}

				switch (iStatus) {
				case 1000:// NET_SDK_CALLBACK_STATUS_SUCCESS
					logger.info("下发人脸参数成功");
					sendFlag = 1;
					break;
				case 1001:
					byCardNo = new byte[32];
					System.arraycopy(struCallbackStatus.byValue, 4, byCardNo, 0, 32);
					logger.info("正在下发人脸参数,卡号:" + new String(byCardNo).trim());
					sendFlag = 0;
					break;
				case 1002:
					int iErrorCode = 0;
					for (int i = 0; i < 4; i++) {
						int ioffset = i * 8;
						int iByte = struCallbackStatus.byValue[i + 4] & 0xff;
						iErrorCode = iErrorCode + (iByte << ioffset);
					}
					byCardNo = new byte[32];
					System.arraycopy(struCallbackStatus.byValue, 8, byCardNo, 0, 32);
					logger.otherError("下发人脸参数失败,错误号:" + iErrorCode + ",卡号:"+ new String(byCardNo).trim());
					sendFlag = -1;
					break;
				}
				break;
			case 2:// 获取状态数据
				HCNetSDK.NET_DVR_FACE_PARAM_STATUS m_struFaceStatus = new HCNetSDK.NET_DVR_FACE_PARAM_STATUS();
				m_struFaceStatus.write();
				Pointer pStatusInfo = m_struFaceStatus.getPointer();
				pStatusInfo.write(0, lpBuffer.getByteArray(0, m_struFaceStatus.size()), 0, m_struFaceStatus.size());
				m_struFaceStatus.read();
				if(m_struFaceStatus.byCardReaderRecvStatus[0] == 1) {
					logger.info("人脸读卡器状态正常，照片可下发");
					faceFlag = 1;
				}else {
					logger.otherError("人脸读卡器状态错误，状态值：" + m_struFaceStatus.byCardReaderRecvStatus[0]);
					faceFlag = -1;
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean getCardInfo(String deviceIP) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return false;
	}

	/***
	 * hCNetSDK：海康SDK lUserID：SDK注册状态（-1失败，0成功） isdel：删除还是下发的标志（delete删除，normal下发）
	 * visitor：访客数据
	 * 
	 */
	@Override
	public boolean setVisitorCard(String deviceIP, String isdel, TbVisitor visitor) {
		// TODO Auto-generated method stub
		lUserID = initAndLogin(deviceIP);
		if(lUserID<0) {
			logger.otherError("注册失败");
			return false;
		}
		int iErr = 0;

		// 设置卡参数
		HCNetSDK.NET_DVR_CARD_CFG_COND m_struCardInputParamSet = new HCNetSDK.NET_DVR_CARD_CFG_COND();
		m_struCardInputParamSet.read();
		m_struCardInputParamSet.dwSize = m_struCardInputParamSet.size();
		m_struCardInputParamSet.dwCardNum = 1;
		m_struCardInputParamSet.byCheckCardNo = 1;

		Pointer lpInBuffer = m_struCardInputParamSet.getPointer();
		m_struCardInputParamSet.write();

		Pointer pUserData = null;
		FRemoteCfgCallBackCardSet fRemoteCfgCallBackCardSet = new FRemoteCfgCallBackCardSet();

		int lHandle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_SET_CARD_CFG_V50, lpInBuffer,
				m_struCardInputParamSet.size(), fRemoteCfgCallBackCardSet, pUserData);
		if (lHandle < 0) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("建立长连接失败，错误号：" + iErr);
			return false;
		}

		HCNetSDK.NET_DVR_CARD_CFG_V50 struCardInfo = new HCNetSDK.NET_DVR_CARD_CFG_V50(); // 卡参数
		struCardInfo.read();
		struCardInfo.dwSize = struCardInfo.size();
		struCardInfo.dwModifyParamType = 0x6DAF;// 0x00000001 + 0x00000002 + 0x00000004 + 0x00000008 +
		// 0x00000010 + 0x00000020 + 0x00000080 + 0x00000100 + 0x00000200 + 0x00000400 +
		// 0x00000800;
		/***
		 * #define CARD_PARAM_CARD_VALID 0x00000001 //卡是否有效参数 #define CARD_PARAM_VALID
		 * 0x00000002 //有效期参数 #define CARD_PARAM_CARD_TYPE 0x00000004 //卡类型参数 #define
		 * CARD_PARAM_DOOR_RIGHT 0x00000008 //门权限参数 #define CARD_PARAM_LEADER_CARD
		 * 0x00000010 //首卡参数 #define CARD_PARAM_SWIPE_NUM 0x00000020 //最大刷卡次数参数 #define
		 * CARD_PARAM_GROUP 0x00000040 //所属群组参数 #define CARD_PARAM_PASSWORD 0x00000080
		 * //卡密码参数 #define CARD_PARAM_RIGHT_PLAN 0x00000100 //卡权限计划参数 #define
		 * CARD_PARAM_SWIPED_NUM 0x00000200 //已刷卡次数 #define CARD_PARAM_EMPLOYEE_NO
		 * 0x00000400 //工号 #define CARD_PARAM_NAME 0x00000800 //姓名
		 */
		for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++) {
			struCardInfo.byCardNo[i] = 0;
		}
		String strCardNo = visitor.getVisitId();
		for (int i = 0; i < strCardNo.length(); i++) {
			struCardInfo.byCardNo[i] = strCardNo.getBytes()[i];
		}
		if ("delete".equals(isdel)) {
			struCardInfo.byCardValid = 0;
		} else {
			struCardInfo.byCardValid = 1;
		}

		struCardInfo.byCardType = 1;
		struCardInfo.byLeaderCard = 0;
		struCardInfo.byDoorRight[0] = 1; // 门1有权限
		struCardInfo.wCardRightPlan[0].wRightPlan[0] = 1; // 门1关联卡参数计划模板1

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date startDate = sdf.parse(visitor.getStartdate());
			Date endDate = sdf.parse(visitor.getEnddate());
			Calendar startCalendar = Calendar.getInstance();
			Calendar endCalendar = Calendar.getInstance();
			startCalendar.setTime(startDate);
			endCalendar.setTime(endDate);

			// 卡有效期
			struCardInfo.struValid.byEnable = 1;
			struCardInfo.struValid.struBeginTime.wYear = (short) startCalendar.get(Calendar.YEAR);
			struCardInfo.struValid.struBeginTime.byMonth = (byte) (startCalendar.get(Calendar.MONTH) + 1);
			struCardInfo.struValid.struBeginTime.byDay = (byte) startCalendar.get(Calendar.DAY_OF_MONTH);
			struCardInfo.struValid.struBeginTime.byHour = (byte) startCalendar.get(Calendar.HOUR_OF_DAY);
			struCardInfo.struValid.struBeginTime.byMinute = (byte) startCalendar.get(Calendar.MINUTE);
			struCardInfo.struValid.struBeginTime.bySecond = 0;
			struCardInfo.struValid.struEndTime.wYear = (short) endCalendar.get(Calendar.YEAR);
			struCardInfo.struValid.struEndTime.byMonth = (byte) (endCalendar.get(Calendar.MONTH) + 1);
			struCardInfo.struValid.struEndTime.byDay = (byte) endCalendar.get(Calendar.DAY_OF_MONTH);
			struCardInfo.struValid.struEndTime.byHour = (byte) endCalendar.get(Calendar.HOUR_OF_DAY);
			struCardInfo.struValid.struEndTime.byMinute = (byte) endCalendar.get(Calendar.MINUTE);
			struCardInfo.struValid.struEndTime.bySecond = 0;

		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		struCardInfo.dwMaxSwipeTime = 0; // 无次数限制
		struCardInfo.dwSwipeTime = 0;
		struCardInfo.byCardPassword = "123456".getBytes();
		struCardInfo.dwEmployeeNo = Integer.valueOf(visitor.getVisitId());
		struCardInfo.wSchedulePlanNo = 1;
		struCardInfo.bySchedulePlanType = 2;
		struCardInfo.wDepartmentNo = 1;

		byte[] strCardName = null;
		try {
			strCardName = visitor.getUserrealname().getBytes("GBK");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int i = 0; i < HCNetSDK.NAME_LEN; i++) {
			struCardInfo.byName[i] = 0;
		}
		for (int i = 0; i < strCardName.length; i++) {
			struCardInfo.byName[i] = strCardName[i];
		}

		struCardInfo.write();
		Pointer pSendBufSet = struCardInfo.getPointer();

		if (!hCNetSDK.NET_DVR_SendRemoteConfig(lHandle, 0x3, pSendBufSet, struCardInfo.size())) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("ENUM_ACS_SEND_DATA失败，错误号：" + iErr);
			return false;
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(fRemoteCfgCallBackCardSet.sendFlag != 1) {
			return false;
		}
		if (!hCNetSDK.NET_DVR_StopRemoteConfig(lHandle)) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("断开长连接失败，错误号：" + iErr);
			return false;
		}
		return true;
	}

	@Override
	public boolean setVisitorFace(String deviceIP, TbVisitor visitor) {
		// TODO Auto-generated method stub
		lUserID = initAndLogin(deviceIP);
		int iErr = 0; // 错误号

		// 设置人脸参数
		HCNetSDK.NET_DVR_FACE_PARAM_COND m_struFaceSetParam = new HCNetSDK.NET_DVR_FACE_PARAM_COND();
		m_struFaceSetParam.dwSize = m_struFaceSetParam.size();

		String strCardNo = "V" + visitor.getVisitId();
		// String strCardNo = "201909";// 人脸关联的卡号
		for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++) {
			m_struFaceSetParam.byCardNo[i] = 0;
		}

		System.arraycopy(strCardNo.getBytes(), 0, m_struFaceSetParam.byCardNo, 0, strCardNo.length());

		m_struFaceSetParam.byEnableCardReader[0] = 1;
		m_struFaceSetParam.dwFaceNum = 1;
		m_struFaceSetParam.byFaceID = 1;
		m_struFaceSetParam.write();

		Pointer lpInBuffer = m_struFaceSetParam.getPointer();

		Pointer pUserData = null;
		FRemoteCfgCallBackFaceSet fRemoteCfgCallBackFaceSet = new FRemoteCfgCallBackFaceSet();

		int lHandle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_SET_FACE_PARAM_CFG, lpInBuffer,
				m_struFaceSetParam.size(), fRemoteCfgCallBackFaceSet, pUserData);
		if (lHandle < 0) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			System.out.println("建立长连接失败，错误号：" + iErr);
		}
		System.out.println("建立设置卡参数长连接成功!");
		HCNetSDK.NET_DVR_FACE_PARAM_CFG struFaceInfo = new HCNetSDK.NET_DVR_FACE_PARAM_CFG(); // 卡参数
		struFaceInfo.read();
		struFaceInfo.dwSize = struFaceInfo.size();

		// strCardNo = "201909";// 人脸关联的卡号
		for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++) {
			struFaceInfo.byCardNo[i] = 0;
		}
		System.arraycopy(strCardNo.getBytes(), 0, struFaceInfo.byCardNo, 0, strCardNo.length());

		struFaceInfo.byEnableCardReader[0] = 1; // 需要下发人脸的读卡器，按数组表示，每位数组表示一个读卡器，数组取值：0-不下发该读卡器，1-下发到该读卡器
		struFaceInfo.byFaceID = 1; // 人脸ID编号，有效取值范围：1~2
		struFaceInfo.byFaceDataType = 1; // 人脸数据类型：0- 模板（默认），1- 图片

		/*****************************************
		 * 从本地文件里面读取JPEG图片二进制数据
		 *****************************************/
		FileInputStream picfile = null;
		int picdataLength = 0;
		try {
			String filePath = Constants.VisitorPath + "/" + visitor.getUserrealname() + visitor.getSolecode() + ".jpg";
			File picture = new File(filePath);
			if (!picture.exists()) {
				return false;
			}
			// picfile = new FileInputStream(new File(System.getProperty("user.dir") +
			// "\\face.jpg"));
			picfile = new FileInputStream(picture);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			picdataLength = picfile.available();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (picdataLength < 0) {
			logger.otherError("人脸照片数据长度错误");
			return false;
		}

		HCNetSDK.BYTE_ARRAY ptrpicByte = new HCNetSDK.BYTE_ARRAY(picdataLength);
		try {
			picfile.read(ptrpicByte.byValue);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		ptrpicByte.write();
		/**************************/

		struFaceInfo.dwFaceLen = picdataLength;
		struFaceInfo.pFaceBuffer = ptrpicByte.getPointer();

		struFaceInfo.write();
		Pointer pSendBufSet = struFaceInfo.getPointer();

		// ENUM_ACS_INTELLIGENT_IDENTITY_DATA = 9, //智能身份识别终端数据类型，下发人脸图片数据
		if (!hCNetSDK.NET_DVR_SendRemoteConfig(lHandle, 0x9, pSendBufSet, struFaceInfo.size())) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("NET_DVR_SendRemoteConfig失败，错误号：" + iErr);
			return false;
		}

		/*
		 * try { Thread.sleep(5000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		if (!hCNetSDK.NET_DVR_StopRemoteConfig(lHandle)) {
			iErr = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("断开长连接失败，错误号：" + iErr);
			return false;
		}
		return true;
	}

	@Override
	public int initAndLogin(String sDeviceIP) {
		// TODO Auto-generated method stub
		
		HCNetSDK.NET_DVR_USER_LOGIN_INFO struLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();
		HCNetSDK.NET_DVR_DEVICEINFO_V40 struDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();
		Pointer PointerstruDeviceInfoV40 = struDeviceInfo.getPointer();
		Pointer PointerstruLoginInfo = struLoginInfo.getPointer();
		String passwordFieldPwd = "wgmhao123";
		String sUserName = "admin";
		int iPort = 8000;
		for (int i = 0; i < sDeviceIP.length(); i++) {
			struLoginInfo.sDeviceAddress[i] = (byte) sDeviceIP.charAt(i);
		}
		for (int i = 0; i < passwordFieldPwd.length(); i++) {
			struLoginInfo.sPassword[i] = (byte) passwordFieldPwd.charAt(i);
		}
		for (int i = 0; i < sUserName.length(); i++) {
			struLoginInfo.sUserName[i] = (byte) sUserName.charAt(i);
		}
		struLoginInfo.wPort = (short) iPort;
		struLoginInfo.write();
		lUserID = hCNetSDK.NET_DVR_Login_V40(PointerstruLoginInfo, PointerstruDeviceInfoV40);
		if(lUserID < 0) {
			logger.otherError("注册失败，失败号："+hCNetSDK.NET_DVR_GetLastError());
		}
		return lUserID;
	}

	/***
	 * 
	 * 门禁设备通行记录
	 * 
	 * 
	 */
	@Override
	public void sendAccessRecord(String deviceIP) {
		// TODO Auto-generated method stub
		lUserID = initAndLogin(deviceIP);
		int lAlarmHandle;
		FMSGCallBack_V31 fMSFCallBack_V31 = new FMSGCallBack_V31();
		fMSFCallBack_V31 = new FMSGCallBack_V31();
		HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
		m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
		m_strAlarmInfo.byLevel = 1;
		m_strAlarmInfo.byAlarmInfoType = 1;
		m_strAlarmInfo.write();

		lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);
		System.out.println(lAlarmHandle);
		Pointer pUser = null;
		if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack_V31, pUser)) {
			logger.otherError("设置回调函数失败!");
		}
		try {
			new Thread().sleep(180000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!hCNetSDK.NET_DVR_CloseAlarmChan_V30(lAlarmHandle)) {
			logger.otherError("撤销报警上传通道失败");
			hCNetSDK.NET_DVR_Logout(lUserID);
			hCNetSDK.NET_DVR_Cleanup();
			return;
		}
		// 注销用户
		hCNetSDK.NET_DVR_Logout(lUserID);
		// 释放 SDK 资源

		hCNetSDK.NET_DVR_Cleanup();
		return;

	}

	public class FMSGCallBack_V31 implements HCNetSDK.FMSGCallBack_V31 {
		public boolean invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen,
				Pointer pUser) {
			AlarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
			return true;
		}

	}

	public void AlarmDataHandle(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen,
			Pointer pUser) {
		// TODO Auto-generated method stub
		String sAlarmType = new String();
		String[] newRow = new String[3];
		// 报警时间
		Date today = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		sAlarmType = new String("lCommand=") + lCommand;
		String[] sIP = new String[2];
		if (lCommand == HCNetSDK.COMM_ALARM_ACS) {
			HCNetSDK.NET_DVR_ACS_ALARM_INFO strACSInfo = new HCNetSDK.NET_DVR_ACS_ALARM_INFO();
			strACSInfo.write();
			Pointer pACSInfo = strACSInfo.getPointer();
			pACSInfo.write(0, pAlarmInfo.getByteArray(0, strACSInfo.size()), 0, strACSInfo.size());
			strACSInfo.read();

			String idCardNo = new String(strACSInfo.struAcsEventInfo.byCardNo).trim();
			if (idCardNo != null && !"".equals(idCardNo)) {
				int id = Integer.valueOf(idCardNo.substring(1));
				sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
				HCNetSDK.NET_DVR_TIME dateTime = strACSInfo.struTime;
				String year = String.valueOf(dateTime.dwYear);
				String month = String.valueOf(dateTime.dwMonth);
				String day = String.valueOf(dateTime.dwDay);
				if (dateTime.dwDay < 10) {
					day = "0" + dateTime.dwDay;
				}
				if (dateTime.dwMonth < 10) {
					month = "0" + dateTime.dwMonth;
				}
				String date = year + "-" + month + "-" + day;
				String hour = String.valueOf(dateTime.dwHour);
				String minute = String.valueOf(dateTime.dwMinute);
				String second = String.valueOf(dateTime.dwSecond);
				if (dateTime.dwHour < 10) {
					hour = "0" + dateTime.dwHour;
				}
				if (dateTime.dwMinute < 10) {
					minute = "0" + dateTime.dwMinute;
				}
				if (dateTime.dwSecond < 10) {
					second = "0" + dateTime.dwSecond;
				}
				String time = hour + ":" + minute + ":" + second;

				if ("S".equals(idCardNo.substring(0, 1))) {

					TbCompanyUser user = companyUserService.findOne(id);
					DeviceRelated devRelated = devRelatedService.findByFaceIP(sIP[0]);
					String cardNO = "S" + user.getCompanyUserId();

					if (accessRecordService.findByDateTimeAndUser(date, time, user.getIdNO(), sIP[0]).size() <= 0) {
						System.out.println(user.getUserName());
						System.out.println(user.getIdNO());
						System.out.println(sIP[0]);
						System.out.println(devRelated.getRelayOUT());
						saverecord(user.getUserName(), user.getIdNO(), "staff", sIP[0], devRelated.getRelayOUT(), date,
								time, cardNO);
					}

				} else if ("V".equals(idCardNo.substring(0, 1))) {

					TbVisitor visitor = visitorService.findVisitorId(idCardNo.substring(1));
					DeviceRelated devRelated = devRelatedService.findByFaceIP(sIP[0]);
					String cardNO = "S" + visitor.getVisitId();
					if (accessRecordService.findByDateTimeAndUser(date, time, visitor.getIdNO(), sIP[0]).size() <= 0) {
						saverecord(visitor.getUserrealname(), visitor.getIdNO(), "visitor", sIP[0],
								devRelated.getRelayOUT(), date, time, cardNO);
					}

				}
				String scanTime = year + month + day + hour + minute + second;
				getPic(strACSInfo.pPicData, strACSInfo.dwPicDataLen, idCardNo, scanTime);
			}
			sAlarmType = sAlarmType + "：门禁主机报警信息，卡号：" + new String(strACSInfo.struAcsEventInfo.byCardNo).trim()
					+ "，卡类型：" + strACSInfo.struAcsEventInfo.byCardType + "，报警主类型：" + strACSInfo.dwMajor + "，报警次类型："
					+ strACSInfo.dwMinor;
			newRow[0] = dateFormat.format(today);
			// 报警类型
			newRow[1] = sAlarmType;
			// 报警设备IP地址

			System.out.println(sAlarmType);
		}

	}

	private void saverecord(String name, String idCard, String personType, String faceIP, String OUT, String date,
			String time, String cardNO) {
		// TODO Auto-generated method stub
		Devices device = deviceService.findByDeviceIp(faceIP);
		AccessRecord accessRecord = new AccessRecord();
		accessRecord.setOrgCode(towerInforService.findOrgId());
		accessRecord.setPospCode(towerInforService.findPospCode());
		accessRecord.setScanDate(date);
		accessRecord.setScanTime(time);
		accessRecord.setInOrOut(device.getFQ_turnover());
		accessRecord.setOutNumber(OUT);
		accessRecord.setDeviceType("FACE");
		accessRecord.setDeviceIp(faceIP);
		accessRecord.setUserType(personType);
		accessRecord.setUserName(name);
		accessRecord.setIdCard(idCard);
		accessRecord.setIsSendFlag("F");
		accessRecord.setExpt1(cardNO);
		accessRecordService.save(accessRecord);
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * @param picData
	 * @param dwPicDataLen
	 * @param cardNO
	 * @param dateTime
	 */

	private void getPic(Pointer picData, int dwPicDataLen, String cardNO, String dateTime) {
		if (dwPicDataLen > 0) {
			File file = new File(Constants.AccessRecPath);
			if (!file.exists()) {
				file.mkdirs();
			}
			FileOutputStream fout;

			try {
				fout = new FileOutputStream(Constants.AccessRecPath + "/" + dateTime + "_" + cardNO + ".jpg");
				long offset = 0;
				ByteBuffer buffers = picData.getByteBuffer(offset, dwPicDataLen);
				byte[] bytes = new byte[dwPicDataLen];
				buffers.rewind();
				buffers.get(bytes);
				fout.write(bytes);
				fout.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * 对IPC网络摄像头下发数据
	 * 
	 */
	@Override
	public boolean sendToIPC(String hcDeviceIP, File picture, File picAppendData, TbCompanyUser companyUser,
			TbVisitor visitor) {
		// TODO Auto-generated method stub
		if (initAndLogin(hcDeviceIP) < 0) {
			return false;
		}
		m_lUploadHandle = UploadFile("1");

		if (m_lUploadHandle.longValue() != 0) {
			return false;
		}
		boolean result = UploadFaceLinData(picture, picAppendData, companyUser, visitor);

		try {
			new Thread().sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * 网络摄像头 与人脸库建立长连接
	 * 
	 * @param faceLib 人脸库ID
	 * @return
	 */
	private NativeLong UploadFile(String faceLib) {
		HCNetSDK.NET_DVR_FACELIB_COND struInput = new HCNetSDK.NET_DVR_FACELIB_COND();
		struInput.dwSize = struInput.size();
		struInput.szFDID = faceLib.getBytes();
		struInput.byConcurrent = 0;
		struInput.byCover = 1;
		struInput.byCustomFaceLibID = 0;
		struInput.write();
		Pointer lpInput = struInput.getPointer();
		NativeLong ret = hCNetSDK.NET_DVR_UploadFile_V40(lUserID, HCNetSDK.IMPORT_DATA_TO_FACELIB, lpInput,
				struInput.size(), null, null, 0);
		System.out.println("m_lUploadHandle:" + ret.intValue());
		if (ret.longValue() == -1) {
			int code = hCNetSDK.NET_DVR_GetLastError();
			logger.otherError("上传图片文件失败: " + code);
			return ret;
		} else {
			return ret;
		}

	}

	/**
	 * 网络摄像头 获取文件上传进度
	 * 
	 * @return
	 */
	public NativeLong getUploadState() {
		IntByReference pInt = new IntByReference(0);
		m_UploadStatus = hCNetSDK.NET_DVR_GetUploadState(m_lUploadHandle, pInt);
		if (m_UploadStatus.longValue() == -1) {
			System.out.println("NET_DVR_GetUploadState fail,error=" + hCNetSDK.NET_DVR_GetLastError());
			logger.otherError("下发人脸及附加信息失败，错误号=" + hCNetSDK.NET_DVR_GetLastError());
		} else if (m_UploadStatus.longValue() == 2) {
			// System.out.println("is uploading!!!! progress = " + pInt.getValue());
		} else if (m_UploadStatus.longValue() == 1) {
			logger.info("下发成功");
		} else {
			logger.otherError("下发失败，失败号=" + hCNetSDK.NET_DVR_GetLastError());
		}

		return m_UploadStatus;
	}

	/**
	 * 
	 * @param picture       图片
	 * @param picAppendData 图片附加信息
	 */
	public boolean UploadFaceLinData(File picture, File picAppendData, TbCompanyUser companyUser, TbVisitor visitor) {

		UploadSend(picture, picAppendData);
		while (true) {
			if (-1 == m_lUploadHandle.longValue()) {
				return false;
			}
			m_UploadStatus = getUploadState();
			if (m_UploadStatus.longValue() == 1) {
				HCNetSDK.NET_DVR_UPLOAD_FILE_RET struPicRet = new HCNetSDK.NET_DVR_UPLOAD_FILE_RET();
				struPicRet.write();
				Pointer lpPic = struPicRet.getPointer();

				boolean bRet = hCNetSDK.NET_DVR_GetUploadResult(m_lUploadHandle, lpPic, struPicRet.size());
				if (!bRet) {
					System.out.println("NET_DVR_GetUploadResult failed with:" + hCNetSDK.NET_DVR_GetLastError());
					if (hCNetSDK.NET_DVR_UploadClose(m_lUploadHandle)) {
						m_lUploadHandle.setValue(-1);
					}
					return false;
				} else {
					System.out.println("NET_DVR_GetUploadResult succ");
					struPicRet.read();
					String m_picID = new String(struPicRet.sUrl);
					System.out.println("图片上传成功 PID:" + m_picID);
					if (null != companyUser) {
						companyUser.setIdFrontImgUrl(m_picID);
						companyUserService.save(companyUser);
					}
					if (null != visitor) {
						visitor.setPicID(m_picID);
						visitorService.save(visitor);
					}

					if (hCNetSDK.NET_DVR_UploadClose(m_lUploadHandle)) {
						m_lUploadHandle.setValue(-1);
					}
					return true;
				}

			} else if (m_UploadStatus.longValue() >= 3 || m_UploadStatus.longValue() == -1) {
				System.out.println("m_UploadStatus = " + m_UploadStatus);
				hCNetSDK.NET_DVR_UploadClose(m_lUploadHandle);
				m_lUploadHandle.setValue(-1);
				return false;
			}
		}

	}

	/**
	 * 网络摄像头 上传图片及图片的附加信息
	 * 
	 * @param picture         jpg格式图片
	 * @param picAppendData   xml格式附加文件
	 * @param m_lUploadHandle
	 */
	public void UploadSend(File picture, File picAppendData) {
		FileInputStream picfile = null;
		FileInputStream xmlfile = null;
		int picdataLength = 0;
		int xmldataLength = 0;

		try {
			picfile = new FileInputStream(picture);
			xmlfile = new FileInputStream(picAppendData);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			picdataLength = picfile.available();
			xmldataLength = xmlfile.available();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (picdataLength < 0 || xmldataLength < 0) {
			System.out.println("input file/xml dataSize < 0");
			return;
		}

		HCNetSDK.BYTE_ARRAY ptrpicByte = new HCNetSDK.BYTE_ARRAY(picdataLength);
		HCNetSDK.BYTE_ARRAY ptrxmlByte = new HCNetSDK.BYTE_ARRAY(xmldataLength);

		try {
			picfile.read(ptrpicByte.byValue);
			xmlfile.read(ptrxmlByte.byValue);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		ptrpicByte.write();
		ptrxmlByte.write();

		HCNetSDK.NET_DVR_SEND_PARAM_IN struSendParam = new HCNetSDK.NET_DVR_SEND_PARAM_IN();

		struSendParam.pSendData = ptrpicByte.getPointer();
		struSendParam.dwSendDataLen = picdataLength;
		struSendParam.pSendAppendData = ptrxmlByte.getPointer();
		struSendParam.dwSendAppendDataLen = xmldataLength;
		if (struSendParam.pSendData == null || struSendParam.pSendAppendData == null || struSendParam.dwSendDataLen == 0
				|| struSendParam.dwSendAppendDataLen == 0) {
			System.out.println("input file/xml data err");
			return;
		}

		struSendParam.byPicType = 1;
		struSendParam.dwPicMangeNo = 0;
		struSendParam.write();

		NativeLong iRet = hCNetSDK.NET_DVR_UploadSend(m_lUploadHandle, struSendParam.getPointer(), null);

		System.out.println("iRet=" + iRet);
		if (iRet.longValue() < 0) {
			System.out.println("NET_DVR_UploadSend fail,error=" + hCNetSDK.NET_DVR_GetLastError());
		} else {
			System.out.println("NET_DVR_UploadSend success");
			System.out.println("dwSendDataLen =" + struSendParam.dwSendDataLen);
			System.out.println("dwSendAppendDataLen =" + struSendParam.dwSendAppendDataLen);
		}

		try {
			picfile.close();
			xmlfile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/***
	 * 
	 * 	删除IPC摄像头里的人像照片
	 * 
	 * 
	 */
	@Override
	public boolean delIPCpicture(String type, String picID) {
		// TODO Auto-generated method stub
		
		String str = "DELETE /ISAPI/Intelligent/FDLib/1/picture/" + picID;

		HCNetSDK.NET_DVR_XML_CONFIG_INPUT struInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
		struInput.dwSize = struInput.size();

		HCNetSDK.BYTE_ARRAY ptrDeleteFaceLibUrl = new HCNetSDK.BYTE_ARRAY(HCNetSDK.BYTE_ARRAY_LEN);
		System.arraycopy(str.getBytes(), 0, ptrDeleteFaceLibUrl.byValue, 0, str.length());
		ptrDeleteFaceLibUrl.write();
		struInput.lpRequestUrl = ptrDeleteFaceLibUrl.getPointer();
		struInput.dwRequestUrlLen = str.length();
		struInput.lpInBuffer = null;
		struInput.dwInBufferSize = 0;
		struInput.write();

		HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struOutput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
		struOutput.dwSize = struOutput.size();
		struOutput.lpOutBuffer = null;
		struOutput.dwOutBufferSize = 0;

		HCNetSDK.BYTE_ARRAY ptrStatusByte = new HCNetSDK.BYTE_ARRAY(HCNetSDK.ISAPI_STATUS_LEN);
		struOutput.lpStatusBuffer = ptrStatusByte.getPointer();
		struOutput.dwStatusSize = HCNetSDK.ISAPI_STATUS_LEN;
		struOutput.write();

		if (!hCNetSDK.NET_DVR_STDXMLConfig(lUserID, struInput, struOutput)) {
			logger.info("NET_DVR_STDXMLConfig DELETE failed with:" + " " + hCNetSDK.NET_DVR_GetLastError());
			return false;
		} else {
			logger.info("NET_DVR_STDXMLConfig DELETE Succ!!!!!!!!!!!!!!!");
			logger.info("图片删除成功 PID:" + picID);
			return true;
		}

	}

	
	/**
	 * 
	 * 创建人脸库
	 * 
	 */
	@Override
	public void createIPCAlarm(String hcDeviceIP) {
		// TODO Auto-generated method stub
		if (initAndLogin(hcDeviceIP) < 0) {
			return;
		}
		IPCFMSGCallBack fMSFCallBack = new IPCFMSGCallBack();
		Pointer pUser = null;
		if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V30(fMSFCallBack, pUser)) {
			System.out.println("设置回调函数失败!");
		}
		HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
		m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
		m_strAlarmInfo.byLevel = 1;
		m_strAlarmInfo.byAlarmInfoType = 1;
		m_strAlarmInfo.write();
		int sAlarm = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);
		if(sAlarm != -1) {
			logger.info("IPC网络摄像头通行布防开启成功");
		}else {
			logger.otherError("IPC网络摄像头通行布防开启失败");
		}
		
	}

	private class IPCFMSGCallBack implements HCNetSDK.FMSGCallBack {

		@Override
		public void invoke(int lCommand, NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
			// TODO Auto-generated method stub
			String[] sIP = new String[2];
			switch (lCommand) {
			case HCNetSDK.COMM_SNAP_MATCH_ALARM:
				HCNetSDK.NET_VCA_FACESNAP_MATCH_ALARM strFaceSnapMatch = new HCNetSDK.NET_VCA_FACESNAP_MATCH_ALARM();
				strFaceSnapMatch.write();
				Pointer pFaceSnapMatch = strFaceSnapMatch.getPointer();
				pFaceSnapMatch.write(0, pAlarmInfo.getByteArray(0, strFaceSnapMatch.size()), 0,
						strFaceSnapMatch.size());
				strFaceSnapMatch.read();
				System.out.println(strFaceSnapMatch.fSimilarity);
				if(strFaceSnapMatch.fSimilarity > 0) {
					try {
						String sname = new String(strFaceSnapMatch.struBlackListInfo.struBlackListInfo.struAttribute.byName,"GBK").trim();
						String personType =null;
						if(sname.contains("S")) {
							 sname = sname.substring(1);
							 personType = "staff";
						}else if(sname.contains("V")) {
							sname = sname.substring(1);
							personType = "visitor";
						}
						String idCard = new String(strFaceSnapMatch.struBlackListInfo.struBlackListInfo.struAttribute.byCertificateNumber);
						sIP = new String(pAlarmer.sDeviceIP).split("\0", 2);
						String faceIP = sIP[0];
						DeviceRelated devRelated = devRelatedService.findByFaceIP(sIP[0]);
						System.out.println(sname+"***"+idCard+"***"+faceIP+"***"+devRelated.getRelayOUT()+"***"+getTime());
						saverecord(sname, idCard, personType, faceIP, devRelated.getRelayOUT(), getDate(), getTime(), "");
					} catch (UnsupportedEncodingException e) {
						
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			}
		}

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

	@Override
	public boolean getIPCRecord(String deviceIP,String dayInfo) {
		// TODO Auto-generated method stub
		if(null == deviceIP) {
			logger.otherError("通行记录保存失败，缺少设备信息");
			return false;
		}
		try {
			int pos = 1;
			boolean isQuit = false;
			String id = "1";
			while (!isQuit) {
				HCNetSDK.NET_DVR_XML_CONFIG_INPUT struInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
				struInput.dwSize = struInput.size();

				String str = "POST /ISAPI/Intelligent/FDLib/FCSearch\r\n";
				HCNetSDK.BYTE_ARRAY ptrUrl = new HCNetSDK.BYTE_ARRAY(HCNetSDK.BYTE_ARRAY_LEN);
				System.arraycopy(str.getBytes(), 0, ptrUrl.byValue, 0, str.length());
				ptrUrl.write();
				struInput.lpRequestUrl = ptrUrl.getPointer();
				struInput.dwRequestUrlLen = str.length();

				String strInBuffer = new String("<FCSearchDescription><searchID>9988</searchID><searchResultPosition>"
						+ pos
						+ "</searchResultPosition><maxResults>50</maxResults><snapStartTime>"+dayInfo+"T00:00:00Z</snapStartTime>"
						+ "<snapEndTime>"+dayInfo+"T23:59:59Z</snapEndTime><FDIDList><FDID>" + id
						+ "</FDID></FDIDList></FCSearchDescription>");
				HCNetSDK.BYTE_ARRAY ptrByte = new HCNetSDK.BYTE_ARRAY(10 * HCNetSDK.BYTE_ARRAY_LEN);
				ptrByte.byValue = strInBuffer.getBytes();
				ptrByte.write();
				struInput.lpInBuffer = ptrByte.getPointer();
				struInput.dwInBufferSize = strInBuffer.length();
				struInput.write();

				HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struOutput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
				struOutput.dwSize = struOutput.size();

				HCNetSDK.BYTE_ARRAY ptrOutByte = new HCNetSDK.BYTE_ARRAY(HCNetSDK.ISAPI_DATA_LEN);
				struOutput.lpOutBuffer = ptrOutByte.getPointer();
				struOutput.dwOutBufferSize = HCNetSDK.ISAPI_DATA_LEN;

				HCNetSDK.BYTE_ARRAY ptrStatusByte = new HCNetSDK.BYTE_ARRAY(HCNetSDK.ISAPI_STATUS_LEN);
				struOutput.lpStatusBuffer = ptrStatusByte.getPointer();
				struOutput.dwStatusSize = HCNetSDK.ISAPI_STATUS_LEN;
				struOutput.write();

				if (hCNetSDK.NET_DVR_STDXMLConfig(lUserID, struInput, struOutput)) {
					String xmlStr = struOutput.lpOutBuffer.getString(0);

					// dom4j解析xml
					Document document = DocumentHelper.parseText(xmlStr);
					// 获取根节点元素对象
					Element FCSearchResult = document.getRootElement();

					// 同时迭代当前节点下面的所有子节点
					Iterator<Element> iterator = FCSearchResult.elementIterator();
					while (iterator.hasNext()) {
						Element e = iterator.next();

						if (e.getName().equals("responseStatusStrg")) {
							if (e.getText().equals("MORE")) {
								isQuit = false;
							} else {
								isQuit = true;
							}
						}
						if (e.getName().equals("numOfMatches")) {
							pos += Integer.parseInt(e.getText());
						}
						
						if (e.getName().equals("MatchList")) {
							Iterator<Element> iterator2 = e.elementIterator(); // MatchElementList节点
							while (iterator2.hasNext()) {
								
								String date = null;
								String time = null;
								String idCard = null;
								String name =null;
								
								Element e2 = iterator2.next(); 					// MatchElement节点
								Iterator<Element> iterator3 = e2.elementIterator();
								while (iterator3.hasNext()) {
									Element e3 = iterator3.next();

									
									
									DeviceRelated devRelated = devRelatedService.findByFaceIP(deviceIP);
									
									if (e3.getName().equals("snapTime")) {
										int dateIndex = e3.getText().indexOf("T");
										
										date = e3.getText().substring(0,dateIndex);
										time = e3.getText().substring(dateIndex+1);					
									}
									if (e3.getName().equals("FaceMatchInfoList")) {

										Iterator<Element> iterator4 = e3.elementIterator(); // FaceMatchInfoList节点
										while (iterator4.hasNext()) {

											Element e4 = iterator4.next();
											if (e4.getName().equals("FaceMatchInfo")) {
												Iterator<Element> iterator5 = e4.elementIterator();
												while (iterator5.hasNext()) {
													
													Element e5 = iterator5.next(); // FaceMatchInfo节点
													
													if(e5.getName().equals("certificateNumber")) {
														idCard = e5.getText();
													}
													if (e5.getName().equals("name")) {
														name = e5.getText();
													}
												}
											}
										}
									}
								}
								if(name !=null) {
									if (name.contains("S")) {
										TbCompanyUser companyUser = companyUserService.findOne(Integer.valueOf(idCard));
										name = name.substring(1);
										DeviceRelated devRelated = devRelatedService.findByFaceIP(deviceIP);
										System.out.println(name+"***"+Integer.valueOf(idCard)+"***"+date+"***"+time+"****"+deviceIP);
										if(null == companyUser) {
											logger.otherError("员工ID与员工表关联失败，找不到"+name);
											continue;
										}
										saverecord(name, companyUser.getIdNO(), "staff", deviceIP, devRelated.getRelayOUT(), date, time, "");
										//System.out.println("name：" + e5.getText());
									} else if(name.contains("V")){
										
										TbVisitor visitor = visitorService.findVisitorId(idCard);
										if(null == visitor) {
											logger.otherError("访客ID与访客表关联失败，找不到"+name);
											continue;
										}
										DeviceRelated devRelated = devRelatedService.findByFaceIP(deviceIP);
										name = name.substring(1);
										saverecord(name, visitor.getIdNO(), "visitor", deviceIP, devRelated.getRelayOUT(), date, time, "");
									}
								}
								
			
							}
						}
					}
				} else {
					int code = hCNetSDK.NET_DVR_GetLastError();
					JOptionPane.showMessageDialog(null, "获取失败: " + code);
					logger.info("IPC通行记录获取失败，失败号: " + code);
					return false;
				}
			}
		} catch (DocumentException ex) {
			
			return false;
		}
		return true;
	}
}
