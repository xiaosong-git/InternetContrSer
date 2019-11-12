package com.uppermac.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.uppermac.entity.Devices;
import com.uppermac.entity.TbCompanyUser;
import com.uppermac.entity.TbScanrecode;
import com.uppermac.entity.TbVisitor;
import com.uppermac.service.DevicesService;
import com.uppermac.service.TbCompanyUserService;
import com.uppermac.service.TbScanrecodeService;
import com.uppermac.service.TbVisitorService;
import com.uppermac.service.TowerInforService;
import com.uppermac.service.impl.TbCompanyUserServiceImpl;
import com.uppermac.service.impl.TbScanrecodeServiceImpl;
import com.uppermac.service.impl.TbVisitorServiceImpl;
import com.uppermac.service.impl.TowerInforServiceImpl;
import com.uppermac.service.impl.VisitInOrOutServiceImpl;

@Component
public class CheckUtils {

	@Autowired
	private TowerInforService towerInforService;

	@Autowired
	private TbVisitorService tbStaffService;

	@Autowired
	private TbScanrecodeService tbScanrecodeService;

	@Autowired
	private TbCompanyUserService companyUserService;

	@Autowired
	private DevicesService deviceService;

	MyLog logger = new MyLog(CheckUtils.class);

	OkHttpUtil okHttpUtil = new OkHttpUtil();
	private static CheckUtils checkUtils;

	@PostConstruct
	public void init() {
		checkUtils = this;
		checkUtils.tbStaffService = this.tbStaffService;
	}

	/*
	 * 门禁卡二维码验证
	 */
	public boolean scanUserCode(String name, String userCompany, String userCompanyId, String readerIp) {
		System.out.println(name + "***" + userCompany + "***" + userCompanyId + "***" + readerIp);
		boolean result = false;
		if (StringUtils.isEmpty(name)) {
			return result;
		}
		if (StringUtils.isEmpty(userCompany)) {
			return result;
		}
		if (StringUtils.isEmpty(userCompanyId)) {
			return result;
		}

		TbCompanyUser tbCompanyUser = companyUserService.findByNameAndCid(name, Integer.valueOf(userCompanyId));

		if (tbCompanyUser == null) {
			System.out.println("本地没有此员工数据");
			return result;
		}
		String dateTime = getDate();
		String date = getDate2();
		String time = getDate3();

		result = true;

		// 门禁卡使用记录通行记录
		System.out.println(readerIp);
		Devices device = deviceService.findByDeviceIp(readerIp);
		if (device == null) {
			System.out.println("找不到读头Ip");
			return false;
		}

		TbScanrecode tbScanrecode = new TbScanrecode();
		tbScanrecode.setScanId(UUID.randomUUID().toString().replaceAll("\\-", ""));
		tbScanrecode.setDeviceid(device.getDeviceId());
		tbScanrecode.setDevicename(device.getE_out());
		tbScanrecode.setVisitDate(date);
		tbScanrecode.setVisitTime(time);
		tbScanrecode.setStaffidnumber(tbCompanyUser.getIdNO());
		tbScanrecode.setTurnover(device.getFQ_turnover());
		tbScanrecode.setOrgcode(towerInforService.findOrgId());
		tbScanrecode.setChecktype("QRCode");
		tbScanrecode.setDatetype("临时");
		tbScanrecode.setStaffname(tbCompanyUser.getUserName());
		tbScanrecode.setPersonType("staff");
		tbScanrecode.setVisitorfacedel("");
		tbScanrecodeService.save(tbScanrecode);
		return result;
	}

	/*
	 * 访客访问二维码验证
	 */

	public boolean verificationCache(TbVisitor visitor) throws ParseException {

		String nowtime = getDate();

		boolean period = compareDate(nowtime, visitor.getPrestartdate());
		boolean after = compareDate(visitor.getEnddate(), nowtime);

		if (period && after) {
			return true;
		} else {
			logger.otherError("访问时间不在预约时间内");
			return false;
		}
	}

	private String getDate() {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}

	private String getDate2() {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}

	private String getDate3() {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}

	private boolean compareDate(String DATE1, String DATE2) throws ParseException {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		Date dt1 = df.parse(DATE1);
		Date dt2 = df.parse(DATE2);

		if (dt1.getTime() > dt2.getTime()) {
			return true;
		} else if (dt1.getTime() < dt2.getTime()) {
			return false;
		} else {
			return true;
		}

	}
}
