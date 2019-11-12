package com.uppermac.scheduler;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.uppermac.config.HCNetSDK;
import com.uppermac.utils.MyLog;

@Order(1)
@Component
public class InitHCNetSDK implements ApplicationRunner{

	
	boolean isInit = false;
	
	int lUserID;
	
	static {
		System.load("/usr/apache-java-jar/lib/libHCCore.so");
		System.load("/usr/apache-java-jar/lib/libhpr.so");
		System.load("/usr/apache-java-jar/lib/libhcnetsdk.so");
	}
	private MyLog logger = new MyLog(GetVisitorRecord.class);
	
	HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
	
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		// TODO Auto-generated method stub
		systemload();
		isInit = hCNetSDK.NET_DVR_Init();
		if(!isInit) {
			logger.otherError("海康SDK初始化失败");
		}else {
			logger.info("海康SDK初始化成功");
		}
	}
	
	public void systemload() {

		/*
		 * String strPathCom = "/usr/apache-java-jar/lib";
		 * HCNetSDK.NET_DVR_LOCAL_SDK_PATH struComPath = new
		 * HCNetSDK.NET_DVR_LOCAL_SDK_PATH(); System.arraycopy(strPathCom.getBytes(), 0,
		 * struComPath.sPath, 0, strPathCom.length()); struComPath.write();
		 * hCNetSDK.NET_DVR_SetSDKInitCfg(2, struComPath.getPointer());
		 */

		String strPathCom2 = "/usr/apache-java-jar/lib/";
		HCNetSDK.NET_DVR_LOCAL_SDK_PATH struComPath2 = new HCNetSDK.NET_DVR_LOCAL_SDK_PATH();
		System.arraycopy(strPathCom2.getBytes(), 0, struComPath2.sPath, 0, strPathCom2.length());
		struComPath2.write();
		hCNetSDK.NET_DVR_SetSDKInitCfg(2, struComPath2.getPointer());

		HCNetSDK.BYTE_ARRAY ptrByteArrayCrypto = new HCNetSDK.BYTE_ARRAY(256);
		String strPathCrypto = "/usr/apache-java-jar/lib/libssl.so";
		System.arraycopy(strPathCrypto.getBytes(), 0, ptrByteArrayCrypto.byValue, 0, strPathCrypto.length());
		ptrByteArrayCrypto.write();
		hCNetSDK.NET_DVR_SetSDKInitCfg(3, ptrByteArrayCrypto.getPointer());

		HCNetSDK.BYTE_ARRAY ptrByteArrayCrypto2 = new HCNetSDK.BYTE_ARRAY(256);
		String strPathCrypto2 = "/usr/apache-java-jar/lib/libcrypto.so.1.0.0";
		System.arraycopy(strPathCrypto2.getBytes(), 0, ptrByteArrayCrypto2.byValue, 0, strPathCrypto2.length());
		ptrByteArrayCrypto2.write();
		hCNetSDK.NET_DVR_SetSDKInitCfg(3, ptrByteArrayCrypto2.getPointer());

		HCNetSDK.BYTE_ARRAY ptrByteArrayCrypto3 = new HCNetSDK.BYTE_ARRAY(256);
		String strPathCrypto3 = "/usr/apache-java-jar/lib/libcrypto.so";
		System.arraycopy(strPathCrypto3.getBytes(), 0, ptrByteArrayCrypto3.byValue, 0, strPathCrypto3.length());
		ptrByteArrayCrypto3.write();
		hCNetSDK.NET_DVR_SetSDKInitCfg(3, ptrByteArrayCrypto3.getPointer());
	}
}
