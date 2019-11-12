package com.uppermac;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.http.entity.StringEntity;
import org.java_websocket.client.WebSocketClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uppermac.config.HCNetSDK;
import com.uppermac.data.Constants;
import com.uppermac.entity.AccessRecord;
import com.uppermac.entity.DeviceRelated;
import com.uppermac.entity.Devices;
import com.uppermac.entity.FaceReceive;
import com.uppermac.entity.TbCompanyUser;
import com.uppermac.entity.TbVisitor;
import com.uppermac.repository.AccessRecordRepository;
import com.uppermac.repository.DevicesRepository;
import com.uppermac.repository.TbCompanyUserRepository;
import com.uppermac.service.AccessRecordService;
import com.uppermac.service.DevRelatedService;
import com.uppermac.service.DevicesService;
import com.uppermac.service.FaceReceiveService;
import com.uppermac.service.HCNetSDKService;
import com.uppermac.service.TbCompanyUserService;
import com.uppermac.service.TbVisitorService;
import com.uppermac.service.TowerInforService;
import com.uppermac.utils.Base64_2;
import com.uppermac.utils.CheckUtils;
import com.uppermac.utils.Control24DeviceUtil;
import com.uppermac.utils.ControlDeviceUtil;
import com.uppermac.utils.FilesUtils;
import com.uppermac.utils.MyErrorException;
import com.uppermac.utils.MyLog;
import com.uppermac.utils.OkHttpUtil;
import com.uppermac.utils.RedisUtils;
import com.uppermac.utils.WebSocketUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShangweijiApplicationTests {


	@Autowired
	private DevicesRepository device;
	
	@Autowired
	private DevicesService devicesService;
	
	@Autowired
	private TowerInforService towerInforService;

	@Autowired
	private TbVisitorService staffService;

	@Autowired
	private TbCompanyUserService companyService;

	@Autowired
	private TowerInforService towerService;

	@Autowired
	private FaceReceiveService faceReceiveService;

	@Autowired
	private AccessRecordService accessRecordService;
	
	@Autowired
	private TbVisitorService visitorService;
	
	@Autowired
	OkHttpUtil okHttpUtil = new OkHttpUtil();

	private RedisUtils redisUtils;
	
	@Autowired
	CheckUtils check = new CheckUtils();
	
	@Autowired
	OkHttpUtil okhttp = new OkHttpUtil();
	// static Logger logger = LoggerFactory.getLogger(GetCompanyUser.class);

	MyLog logger = new MyLog(ShangweijiApplicationTests.class);

	@Value("${orgCode}")
	private String orgCode;

	
	@Autowired
	private DevRelatedService devRelatedService;
	
	@Autowired
	private AccessRecordRepository accessRecordRepository;
	
	@Autowired
	private HCNetSDKService hcNetSDKService;
	
	@Test
	public void contextLoads() throws InterruptedException, ParseException, UnsupportedEncodingException {
		
		
		File picture = new File("D:\\海康威视\\java示例\\NetBeansProjects(Alarm)\\测试账号1.jpg");
		File picAppendData = new File("D:\\海康威视\\java示例\\NetBeansProjects(Alarm)\\测试账号1.xml");
		//System.out.println(hcNetSDKService.sendToIPC("192.168.1.80", picture, picAppendData,"staff"));
		//System.out.println("hello");
		//hcNetSDKService.setCardInfo("192.168.10.130",204, "林", "333", "normal");
		//TbCompanyUser comp11anyUser = companyService.findOne(1024);
		//hcNetSDKService.setFace("192.168.10.130", "333", companyUser);
		
		/*boolean initSuc = hCNetSDK.NET_DVR_Init();
		if (initSuc != true) {
			System.out.println("初始化失败");
		}
		String m_sDeviceIP = "192.168.10.130";
		HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();// 设备信息
		int iPort = 8000;
		lUserID = hCNetSDK.NET_DVR_Login_V30(m_sDeviceIP, (short) iPort, "admin", "wgmhao123", m_strDeviceInfo);
		System.out.println(lUserID);*/
	/*	Map<String, Object> param = new HashMap<>();
		File file = new File("D://XiaoSong//第三方接口配置文档//APK升级包//2.8.1.apk");
		param.put("apk", file);
		param.put("version", "2.8.1");
		String url = "http://192.168.2.112:8080/office/upgradeApk";
		// String url="http://192.168.10.137:8088/shangweiji/receiveAPK";
		String response = okhttp.postFile(url, param, "multipart/form-data");
		System.out.println(response);
*/		
	/*	String s = Base64_2.encode("[唐龙辉][677][2019-10-08 10:21:00][2019-10-08 11:21:00]".getBytes("UTF-8"));
		System.out.println(s);*/
		
		//W0M0Mzk1NDk3NDk1MjE4MTc2XVvlkLTmoYLmsJFdW0QxMTUxNUIzMjY4QkJCOTI0REJENTE2QTMwNUVCQTc1Mzc3RDYxQjAxQTdDRTQ0Q11b5p6X56aPXVsxODA2NTk4ODY0NV1baWZjXVsyMDE5LTA5LTI4IDExOjU3XVsyMDE5LTA5LTI4IDExOjU4XQ==
	/*	for(int i=1;i<25;i++) {
			String out = "OUT"+i;
			Control24DeviceUtil.controlDevice("192.168.10.209", 8080, out, orgCode);
			
		}*/
		
	/*TelnetClient t =new TelnetClient();
		try {
			t.connect("192.168.10.100",8080);
			System.out.println(t.isConnected());
		} catch (Exception e) {
			System.out.println(t.isConnected());
			System.out.println("9999999999999999");
			// TODO Auto-generated catch block
		}
		*/
	/*	List<AccessRecord> accessRecordList =accessRecordService.findByIsSendFlag("F");
		System.out.println(accessRecordList.size());
		accessRecordService.update(accessRecordList);*/
		/*
		 * List<AccessRecord> accessRecordList =
		 * accessRecordService.findByIsSendFlag("F");
		 * System.out.println(accessRecordList.size()); Map<String,String> map = new
		 * HashMap<>(); map.put("pospCode", towerInforService.findPospCode());
		 * map.put("orgCode", towerInforService.findOrgId()); map.put("raws",
		 * JSON.toJSONString(accessRecordList)); String sendResponse
		 * =okHttpUtil.post("http://192.168.10.55:8098/visitor/inAndOut/save", map);
		 * 
		 * System.out.println(sendResponse);
		 */
		// OUT1 A区进，OUT2 A区出，OUT3 B区进
		// ControlDeviceUtil.controlDevice("192.168.2.6", 8080, "OUT1",
		// towerInforService.findOrgId()); //OUT2 C区出，OUT4 D区出，OUT3 D区进

		// logger.sendErrorLog(towerInforService.findOrgId(), "没获取到访客访问的数据", "","数据错误",
		// Constants.errorLogUrl);

//		logger.sendErrorLog(towerInforService.findOrgId(), "测试", "face12", Constants.errorLogUrl);

		/*
		 * File file = new File("E:\\sts-space\\photoCache\\staff\\林福1.jpg"); String
		 * photo= Base64_2.encode(FilesUtils.getBytesFromFile(file));
		 * System.out.println(photo);
		 */

		// TbCompanyUser tb = new TbCompanyUser();

		// companyService.sendWhiteList("192.168.10.173", user, photo)
		/*
		 * File file = new File("E:\\sts-space\\photoCache\\staff\\ceshi1.jpeg"); String
		 * photo= Base64_2.encode(FilesUtils.getBytesFromFile(file));
		 * System.out.println(photo);
		 */
		// System.out.println(faceReceiveService.findByFaceFlag("1", "staff").size());
		/*
		 * BufferedReader br = null; try { Process p =
		 * Runtime.getRuntime().exec("adb connect 192.168.10.174");
		 * Runtime.getRuntime().exec("adb reboot"); br = new BufferedReader(new
		 * InputStreamReader(p.getInputStream())); String line = null; StringBuilder sb
		 * = new StringBuilder(); while ((line = br.readLine()) != null) {
		 * sb.append(line + "\n"); }
		 * 
		 * System.out.println(sb.toString()); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } finally { if (br != null) {
		 * try { br.close(); } catch (Exception e) { e.printStackTrace(); } } }
		 */
		// List<FaceReceive> faceReceiveList = faceReceiveService.findByFaceFlag("1");
		// TbCompanyUser isExitUser = companyService.findOne(164);
		// System.out.println("*************");
		// System.out.println(isExitUser.toString());

		// System.out.println(companyService.findOne(164).getCompanyFloor());

		// System.out.println(faceReceiveService.findAll().size());
		// System.out.println(faceReceiveService.findByFaceIp("5").getIdCard());
		// System.out.println(faceReceiveService.findByFaceFlag("5").size());
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		// String date = df.format(new Date()); // new Date()为获取当前系统时间

		// staffService.findByBetweenTime("", idCard, date);
		/*
		 * Map<String,Object> param = new HashMap<>(); File file = new
		 * File("D://XiaoSong//第三方接口配置文档//APK升级包//2.8.0.apk"); param.put("apk", file);
		 * param.put("version","2.8.0"); // String url =
		 * "http://192.168.2.107:8080/office/upgradeApk"; String url
		 * ="http://192.168.10.137:8088/shangweiji/receiveAPK"; String response
		 * =okhttp.postFile(url, param, "multipart/form-data");
		 * System.out.println(response);
		 */

		/*
		 * MyLog mine = new MyLog(GetStaffStatUp.class);
		 * mine.stringNull("name","age","company");
		 * System.out.println(mine.getClassName().getName());
		 */

		/*
		 * for(int i=0;i<10;i++) { ControlDeviceUtil.controlDevice("192.168.1.5", 8080,
		 * "OUT2"); new Thread().sleep(3000); }
		 */

		// TbCompanyUser companyUser = companyUserService.findByNameAndIdNO(visitorname,
		// idNO);
		// TbStaff s= staffService.findByNameAndNewTime("林福");
		// System.out.println(s.toString());
		/*
		 * Map<String, Object> params = new HashMap<String, Object>();
		 * params.put("companyName", towerService.findOrgId()); params.put("logfiles",
		 * new File("E://logs//error.log")); params.put("type", "1");
		 * 
		 * 
		 * if(new File("E://logs//error.log").exists()) {
		 * 
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub String s
		 * =okHttpUtil.postFile(Constants.sendLogFile, params, "multipart/form-data");
		 * System.out.println(s); } }).start(); }
		 */
		/*
		 * Map<String, Object> params = new HashMap<String, Object>();
		 * params.put("version", "2.6.5"); File file = new
		 * File("E:\\APKs\\Attend-2.6.4.apk"); params.put("logfiles", file);
		 * //params.put("type", "1"); System.out.println(file); new Thread(new
		 * Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 * System.out.println("sssssssss"); String s =
		 * okHttpUtil.postFile("http://192.168.1.103:8080/office/upgradeApk", params,
		 * "multipart/form-data");
		 * 
		 * System.out.println(s);
		 * 
		 * } }).start();
		 * 
		 */

		// System.out.println(companyService.findByStatus("normal").size());

		// List<TbStaff> staffList = staffService.findByGoneDay();
		// System.out.println(staffList.size());
		/*
		 * if(staffList.size() <= 0) { logger.info("无访客过期数据"); return; }
		 * System.out.println(staffList.size()); for(TbStaff tbStaff:staffList) {
		 * System.out.println(tbStaff.getVistorrealname()+"***"+tbStaff.getVisitorIdNO()
		 * ); TbCompanyUser companyUser =
		 * companyService.findByNameAndIdNO(tbStaff.getVistorrealname(),tbStaff.
		 * getVisitorIdNO());
		 * 
		 * System.out.println(companyUser.getUserName());
		 * System.out.println(companyUser.getCompanyFloor()); }
		 */
		// System.out.println(li.size());

		/*
		 * TbCompanyUser t1 = companyService.findByNameAndIdNO("林福",
		 * "080804E5852B71E9E3394E506740612B60B34A084EE7006D");
		 * System.out.println(t1.getRoleType()); System.out.println(redisUtils.get(
		 * "companyUser_林福_080804E5852B71E9E3394E506740612B60B34A084EE7006D"));
		 * 
		 * TbCompanyUser t2 = companyService.findByNameAndCid("林福", 7);
		 * System.out.println(t2.getRoleType());
		 * System.out.println(redisUtils.get("companyUser_林福_7"));
		 */

		/*
		 * List<TbStaff> staffList = staffService.findByIssued("1");
		 * System.out.println(staffList.size());
		 */

		/*
		 * logger.trace("entry"); //等同于logger.entry();但此方法在新版本好像已经废弃
		 * 
		 * logger.error("Did it again!");
		 * 
		 * logger.info("这是info级信息");
		 * 
		 * logger.debug("这是debug级信息");
		 * 
		 * logger.warn("这是warn级信息");
		 * 
		 * 
		 * logger.trace("exit");
		 */

		// redisUtils.set("redis_key", "redis_vale");
		// redisUtils.expire("redis_key", 30);
		// System.out.println("3333");
		// TbDevice tb = tbDeviceService.findOUTByReaderIp("192.168.1.45");
		// List<TbCompanyUser> ls = companyUserRepository.findByNameAndCid("林福",1);
		// List<TbCompanyUser> ls = companyUserService.findByNameAndIdNO("林福",
		// "080804E5852B71E9E3394E506740612B60B34A084EE7006D");
		// System.out.println(tb.toString());
		/*
		 * JSONObject paramsJson = new JSONObject(); paramsJson.put("startTime",
		 * "2018-6-11 15:45:45"); paramsJson.put("endTime", "2019-6-11 15:20:45");
		 * paramsJson.put("person", "林福"); StringEntity entity=new
		 * StringEntity(paramsJson.toJSONString(),"UTF-8");
		 * entity.setContentType("aaplication/json"); ThirdResponseObj thirdResponseObj
		 * = null; thirdResponseObj =
		 * HttpUtil.http2Se("http://192.168.1.151:8080/office/visitRecord", entity,
		 * "UTF-8"); System.out.println(thirdResponseObj.toString());
		 */
		/*
		 * JSONObject paramsJson = new JSONObject(); paramsJson.put("Name", "林福");
		 * paramsJson.put("idCard", ""); paramsJson.put("imageFile", ""); StringEntity
		 * entity=new StringEntity(paramsJson.toJSONString(),"UTF-8");
		 * entity.setContentType("aaplication/json"); ThirdResponseObj thirdResponseObj
		 * = null; thirdResponseObj =
		 * HttpUtil.http2Se("http://192.168.1.151:8080/office/compareResult", entity,
		 * "UTF-8"); System.out.println(thirdResponseObj.toString());
		 * 
		 */

		// String s =towerService.findOrgId();
		// String s2 =towerService.findPospCode();
		// System.out.println(tbDeviceService.findOUT("192.168.1.44").size());
		// System.out.println(s2);
		/*
		 * CheckUtils check =new CheckUtils(); check.scanUserCode("s", "ss", "s");
		 * 
		 */
		/*
		 * List<TbCompanyUser> companyUserList = companyUserService.findAll();
		 * System.out.println(companyUserList.size());
		 */
		/*
		 * OkHttpUtil okHttpUtil = new OkHttpUtil();
		 * 
		 * Map<String, String> map = new HashMap<>(); map.put("pospCode",
		 * Constants.shangWeiJiNumber); map.put("orgCode", Constants.towerNumbers);
		 * 
		 * List<VisitInOrOut> visitInOrOutList = visitService.findByFlag("F"); if
		 * (visitInOrOutList.size() <= 0) { System.out.println("无堆积的扫描结果数据，已全部发送");
		 * return; }
		 * 
		 * map.put("raws", JSON.toJSONString(ssvisitInOrOutList));
		 * 
		 * String sendResponse =
		 * okHttpUtil.post("http://47.99.209.40:8082/foreign/uploadByBatch", map); if
		 * (sendResponse.isEmpty() || sendResponse == null) { return; } JSONObject
		 * jsonObject = JSONObject.parseObject(sendResponse); Map<String, Object>
		 * verifyReceive = (Map<String, Object>) jsonObject; JSONObject verify =
		 * (JSONObject) verifyReceive.get("verify"); if
		 * (verify.get("sign").equals("success")) { for (VisitInOrOut visitInOrOut :
		 * visitInOrOutList) { visitInOrOut.setIsSendFlag("T");
		 * visitService.save(visitInOrOut); } System.out.println("堆积数据已全部发送！"); }
		 * System.out.println(sendResponse);
		 */

		/*
		 * OkHttpUtil okHttpUtil = new OkHttpUtil(); String response =
		 * okHttpUtil.post("http://47.99.209.40:8082/foreign/uploadBySingle",
		 * "swjixm04","ifc4", "4", "out", "2019-02-28", "14:30:15");
		 * 
		 * System.out.println(response);
		 */

		/*
		 * TbDiscrepancyrecode tbDiscrepancyrecode = new TbDiscrepancyrecode();
		 * tbDiscrepancyrecode.setDiscrepancyId(UUID.randomUUID().toString().replaceAll(
		 * "\\-", ""));
		 * tbDiscrepancyrecode.setDeviceid("c62c6646ed6e11e88876f8a963481d55");
		 * tbDiscrepancyrecode.setDevicename("设备1");
		 * tbDiscrepancyrecode.setTurnover("0");
		 * tbDiscrepancyrecode.setOrgcode("xm0216");
		 * tbDiscrepancyrecode.setPhone("18065988645");
		 * tbDiscrepancyrecode.setScantime("2019-02-19 15:49:39");
		 * tbDiscrepancyrecode.setStaffid("C4246781136668672");
		 * tbDiscrepancyrecode.setStaffidnumber(
		 * "080804E5852B71E9E3394E506740612B60B34A084EE7006D");
		 * tbDiscrepancyrecode.setDatetype("长期");
		 * tbDiscrepancyrecode.setEnddate("2019-02-26");
		 * tbDiscrepancyrecode.setStaffname("林福");
		 * tbDiscrepancyrecode.setStartdate("2019-02-19");
		 * tbDiscrepancyrecode.setVisitId("78");
		 * 
		 * JSONObject jsonObject = new JSONObject(); jsonObject.put("11111111",
		 * tbDiscrepancyrecode); String jsonString = jsonObject.toJSONString();
		 * Map<String, String> testMap = JSONObject.parseObject(jsonString, Map.class);
		 * System.out.println(testMap);
		 */

		/*
		 * OkHttpUtil okHttpUtil = new OkHttpUtil(); String response =
		 * okHttpUtil.post("http://127.0.0.1:8080/foreign/uploadBySingle", "swjixm04",
		 * "ifc4", "4", "out", "2019-02-25", "14:30:15"); if
		 * (StringUtils.isEmpty(response)) { System.out.println("返回数据为空"); }
		 * System.out.println(response);
		 */

		/*
		 * String str = "{\"verify\":{\"sign\":\"success\",\"desc\":\"数据发送成功!\"}}";
		 * 
		 * JSONObject jsonObject = JSONObject.parseObject(str); Map<String,Object> map =
		 * (Map<String,Object>)jsonObject; JSONObject s = (JSONObject)
		 * map.get("verify"); System.out.println(s.get("sign"));
		 */

	}

	
	
	private boolean checkPINGResult(String line) {
		Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(line);
		while(matcher.find()) {
			return true;
		}
		return false;
	}
}
