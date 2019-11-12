package com.uppermac.data;

public class Constants {
	
	
	 	public static String pullStaffUrl = "foreign/findOrgCode";
	    public static String pullSingleStaffUrl = "foreign/findBySoleCode";
	    public static String confirmReceiveUrl = "foreign/ findOrgCodeConfirm";
	    public static final String baseURl = "http://47.99.209.40:8082/";
	    public static final String baseFileURl = "http://47.98.205.206:8081/";
	    public static String pullOrgCompanyUrl = "companyUser/findApplySucOrg";		//新增员工接口
	    public static String initUsers = "companyUser/findApplyAllSucOrg";			//员工数据初始化接口
	    public static String accessRecordByBatch = "goldccm-imgServer/inAndOut/save";
	    
	    public static String sendAccessRecordBySingle = "http://47.99.209.40:8082/foreign/uploadBySingle";
	    public static String sendAccessRecordByBatch = "http://47.99.209.40:8082/foreign/uploadByBatch";
	    public static String sendLogFile = "http://47.99.209.40:8081/goldccm-imgServer/goldccm/filelog/uploadMore";
	    
	    public static String errorLogUrl = "http://47.99.209.40:8082/errorLog/uploadErrorLog";
	    
	    public static String listenSocketURI = "ws://192.168.1.10:10000/echo?orgCode=hlxz&pospCode=00000001";
	    
	    public static final String page = "1";
	    public static final int PAGENUMBER = 1;
	       
	     
	    //Linux系统文件存储路径
	    public static String VisitorPath = "/usr/shangweiji/photoCache/visitor";
	    public static String StaffPath = "/usr/shangweiji/photoCache/staff";
	    public static String ApksPath = "/usr/shangweiji/Apks";
	    public static String AccessRecPath ="/usr/shangweiji/Recored";
	    
	    /*
	    //Windows系统文件存储路径
	    public static String VisitorPath ="E:\\sts-space\\photoCache\\visitor";	//访客缓存照片路径
	    public static String StaffPath ="E:\\sts-space\\photoCache\\staff";		//员工缓存照片路径
	    public static String ApksPath ="E:\\Apks";	//
	    public static String AccessRecPath ="E:\\Recored";
	*/
	    //测试接口
	    public static String ceshi1="http://192.168.10.129:8098/visitor/companyUser/findApplySucOrg";
	    
	    public static String ceshi2="http://192.168.1.10:8082/goldccm-imgServer/inAndOut/save";
}
 