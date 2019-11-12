package com.uppermac.scheduler;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.uppermac.entity.Devices;
import com.uppermac.entity.FaceReceive;
import com.uppermac.entity.TbCompanyUser;
import com.uppermac.entity.TbVisitor;
import com.uppermac.service.DevRelatedService;
import com.uppermac.service.DevicesService;
import com.uppermac.service.FaceReceiveService;
import com.uppermac.service.HCNetSDKService;
import com.uppermac.service.TbCompanyUserService;
import com.uppermac.service.TbVisitorService;
import com.uppermac.service.TowerInforService;
import com.uppermac.utils.Misc;
import com.uppermac.utils.MyLog;
import com.uppermac.utils.RedisUtils;

public class DelGoneVisitorRec implements Job {

	private MyLog logger = new MyLog(DelGoneVisitorRec.class);

	@Autowired
	private TowerInforService towerInforService;

	@Autowired
	private FaceReceiveService faceReceiveService;

	@Autowired
	private TbVisitorService staffService;

	@Autowired
	private TbCompanyUserService companyUserService;

	@Autowired
	private DevRelatedService devRelatedService;

	@Resource
	private RedisUtils redisUtils;

	@Autowired
	private HCNetSDKService hcNetSDKService;
	
	@Autowired
	private DevicesService devicesService;
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		/*if (!towerInforService.findVisitorCheckType().equals("QRCODE")) {
			delStaff();
		} else {
			return;
		}*/
	}

	/**
	 * 	删除三步：
	 * 	1，收集所有下发过人脸的设备IP
	 * 	2，收集不能删除人脸的设备IP（员工下发人脸的设备IP + 访客未过期的设备IP）
	 * 	3，取得需要删除人脸的设备IP
	 * 
	 */
	public void delStaff() {

		List<TbVisitor> visitorList = staffService.findByGoneDay();

		if (visitorList.size() <= 0) {
			logger.info("无访客过期数据可删");
			return;
		}
		//做处理标记集合，最后redis统一删除标记
		List<String> flagList = new ArrayList<>();
		
		for (TbVisitor visitor : visitorList) {
			if ("1".equals(redisUtils.get(visitor.getIdNO()))) {
				continue;
			} else {
				flagList.add(visitor.getIdNO());
				//该访客做过处理做标志
				redisUtils.set(visitor.getIdNO(), "1");
				
				//所有下发人脸的设备IP集合
				List<String> allAddr = new ArrayList<String>();
				//不可删除人脸的设备IP集合
				List<String> notGoneAddr = new ArrayList<>();
				// 删除的访客是否有员工身份
				TbCompanyUser visitorUser = companyUserService.findByNameAndIdNO(visitor.getUserrealname(),
						visitor.getIdNO(), "normal");
				if (null != visitorUser) {
					String visitorCompanyfloor = visitorUser.getCompanyFloor();
					if (null == visitorCompanyfloor) {
						continue;
					} else {
						// 员工照片所在的设备IP
						List<String> visitorUserDeviceIP = devRelatedService.getAllFaceDeviceIP(visitorCompanyfloor);
						for (int i = 0; i < visitorUserDeviceIP.size(); i++) {
							if (!allAddr.contains(visitorUserDeviceIP.get(i))) {
								allAddr.add(visitorUserDeviceIP.get(i));
							}
							if (!notGoneAddr.contains(visitorUserDeviceIP.get(i))) {
								notGoneAddr.add(visitorUserDeviceIP.get(i));
							}
						}
					}
				}
				List<TbVisitor> visitorAllInfo = staffService.findByVisitor(visitor.getUserrealname(),
						visitor.getIdNO());
				for (int i = 0; i < visitorAllInfo.size(); i++) {
					// 查找该访客下发的所有设备IP（过期与未过期）
					TbCompanyUser cUser = companyUserService.findByNameAndIdNO(
							visitorAllInfo.get(i).getVistorrealname(), visitorAllInfo.get(i).getVisitorIdNO(),
							"normal");
					String cFloor = cUser.getCompanyFloor();
					List<String> cFaceIP = devRelatedService.getAllFaceDeviceIP(cFloor);
					for (int j = 0; j < cFaceIP.size(); j++) {
						if (!allAddr.contains(cFaceIP.get(j))) {
							allAddr.add(cFaceIP.get(j));
						}
					}
					// 查找该访客下发的所有设备IP（未过期）
					try {
						if (Misc.compareDate(getDateTime2(), visitorAllInfo.get(i).getStartdate())
								&& Misc.compareDate(visitorAllInfo.get(i).getEnddate(), getDateTime2())) {
							for (int j = 0; j < cFaceIP.size(); j++) {
								if (!notGoneAddr.contains(cFaceIP.get(j))) {
									notGoneAddr.add(cFaceIP.get(j));
								}
							}
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				//
				boolean removeSuc = allAddr.removeAll(notGoneAddr);
				logger.info("删除访客记录移除未过期数据集合结果:" + removeSuc);
				if(removeSuc ==false) {
					logger.info("访客集合移除错误");
					return;
				}
				if (allAddr.size() > 0) {
					String delflag = "0";
					for (int i = 0; i < allAddr.size(); i++) {
						if (!allAddr.contains(allAddr.get(i))) {

							Devices device = devicesService.findByDeviceIp(allAddr.get(i));
							if(null == device) {
								logger.otherError("设备表缺少IP为"+allAddr.get(i)+"的设备");
								continue;
							}
							boolean isSuccess = true;
							if(device.getDeviceType().equals("TPS980")) {
								isSuccess = companyUserService.sendDelWhiteList(allAddr.get(i), visitor.getUserrealname(), visitor.getIdNO());
							}else if(device.getDeviceType().equals("DS-K5671")){
								isSuccess = setUser(device,visitor);
							}else if(device.getDeviceType().equals("DS-2CD8627FWD")) {
								if(null == visitor.getPicID()) {
									isSuccess =  true;
								}else {
									isSuccess = hcNetSDKService.delIPCpicture("visitor", visitor.getPicID());
								}
								
							}
							if (isSuccess) {
								logger.info("设备IP" + allAddr.get(i) + "删除" + visitor.getUserrealname() + "成功");
							} else {
								delflag = "1";
								FaceReceive faceReceive = new FaceReceive();
								faceReceive.setFaceIp(allAddr.get(i));
								faceReceive.setIdCard(visitor.getIdNO());
								faceReceive.setUserName(visitor.getUserrealname());
								faceReceive.setReceiveFlag("1");
								faceReceive.setUserType("visitor");
								faceReceive.setOpera("delete");
								faceReceive.setReceiveTime(getDateTime());
								faceReceiveService.save(faceReceive);
							}
						} else {
							continue;
						}
					}
					visitor.setDelflag(delflag);
					staffService.update(visitor);
				}
			}

		}
		
		//redis统一删除
		for(int i=0;i<flagList.size();i++) {
			redisUtils.delete(flagList.get(i));
		}
	}
	private boolean setUser(Devices device,TbVisitor vistor) {
		String strCardNo ="V"+ vistor.getVisitId();
		boolean suc = false;
		try {
			suc = hcNetSDKService.setCardInfo(device.getDeviceIp(), Integer.valueOf(vistor.getVisitId()), vistor.getUserrealname(), strCardNo, "delete");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return suc;
	}
	
	private String getDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}

	private String getDateTime2() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 设置日期格式
		String date = df.format(new Date()); // new Date()为获取当前系统时间
		return date;
	}
}
