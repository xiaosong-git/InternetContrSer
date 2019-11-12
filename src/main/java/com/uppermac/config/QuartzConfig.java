package com.uppermac.config;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.uppermac.scheduler.AccessRecordToYum;
import com.uppermac.scheduler.DelGoneVisitorRec;
import com.uppermac.scheduler.DeviceReboot;
import com.uppermac.scheduler.GetCompanyUserRecord;
import com.uppermac.scheduler.GetHCRecord;
import com.uppermac.scheduler.GetVisitorRecord;
import com.uppermac.scheduler.HCRecord;
import com.uppermac.scheduler.PINGDevice;
@Configuration
public class QuartzConfig {

	@Autowired
	private JobFactory jobFactory;

	
	
	/**
	 * 1.创建job对象 做什么
	 * 
	 * 2.创建Trigger对象 什么时候做
	 * 
	 */
	
	/**
	 * 
	 * 第一个任务：定时拉取访客数据
	 * @return
	 */
	@Bean(name = "firstJobDetail")
	public JobDetailFactoryBean firstJobDetail() {
		JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
		jobDetail.setJobClass(GetVisitorRecord.class);
		return jobDetail;
	}

	@Bean(name = "firstTrigger")
	public CronTriggerFactoryBean firstTriggerFactoryBean(JobDetail firstJobDetail) {
		CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
		trigger.setCronExpression("0/15 * * * * ?"); // 每15秒执行一次
		// trigger.setCronExpression("0 0 5 * * ?"); //每天凌晨5点钟触发一次
		trigger.setJobDetail(firstJobDetail);
		return trigger;
	}
	
	/**
	 * 
	 * 第二个任务：定时拉取员工数据
	 * @return
	 */
	@Bean(name = "secondJobDetail")
	public JobDetailFactoryBean secondJobDetail() {
		JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
		jobDetail.setJobClass(GetCompanyUserRecord.class);
		return jobDetail;
	}

	@Bean(name = "secondTrigger")
	public CronTriggerFactoryBean secondTriggerFactoryBean(JobDetail secondJobDetail) {
		CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
		trigger.setCronExpression("0 0/5 * * * ?"); // 每5分钟执行一次
		//trigger.setCronExpression("0/15 * * * * ?"); //每天凌晨5点钟触发一次
		trigger.setJobDetail(secondJobDetail);
		return trigger;
	}

	/**
	 *	第三个任务：定时重启设备
	 * 
	 * @return
	 */
	@Bean(name = "thirdJobDetail")
	public  JobDetailFactoryBean thirdJobDetail() {
		JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
		jobDetail.setJobClass(DeviceReboot.class);
		return jobDetail;
	}
	
	@Bean(name = "thirdTrigger")
	public CronTriggerFactoryBean thirdTriggerFactoryBean(JobDetail thirdJobDetail) {
		CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
		trigger.setCronExpression("0 0 0 */2 * ?"); 
		//trigger.setCronExpression("0 0/1 * * * ?");
		trigger.setJobDetail(thirdJobDetail);
		return trigger;
	}
	
	/**
	 *	第四个任务：定时发送通行记录
	 * 
	 * @return
	 */
	@Bean(name = "fourJobDetail")
	public  JobDetailFactoryBean fourJobDetail() {
		JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
		jobDetail.setJobClass(AccessRecordToYum.class);
		return jobDetail;
	}
	
	@Bean(name = "fourTrigger")
	public CronTriggerFactoryBean fourTriggerFactoryBean(JobDetail fourJobDetail) {
		CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
		trigger.setCronExpression("0 0 13,22 * * ?");
		//trigger.setCronExpression("0/45 * * * * ?");
		trigger.setJobDetail(fourJobDetail);
		return trigger;
	}
	
	/**
	 *	第五个任务：定时删除过期访客照片
	 * 
	 * @return
	 */
	@Bean(name = "fiveJobDetail")
	public  JobDetailFactoryBean fiveJobDetail() {
		JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
		jobDetail.setJobClass(DelGoneVisitorRec.class);
		return jobDetail;
	}
	
	@Bean(name = "fiveTrigger")
	public CronTriggerFactoryBean fiveTriggerFactoryBean(JobDetail fiveJobDetail) {
		CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
		trigger.setCronExpression("0 0 5 * * ?");
		//trigger.setCronExpression("0/45 * * * * ?");
		trigger.setJobDetail(fiveJobDetail);
		return trigger;
	}
	/**
	 * 
	 * 第六个任务：定时运行PING命令，查看上位机与人脸设备的连通性
	 * 
	 */
	@Bean(name = "sixJobDetail")
	public  JobDetailFactoryBean sixJobDetail() {
		JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
		jobDetail.setJobClass(PINGDevice.class);
		return jobDetail;
	}
	
	@Bean(name = "sixTrigger")
	public CronTriggerFactoryBean sixTriggerFactoryBean(JobDetail sixJobDetail) {
		CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
		trigger.setCronExpression("0 */1 * * * ?");
		trigger.setJobDetail(sixJobDetail);
		return trigger;
	}
	/**
	 * 
	 * 	第七个任务，定时获取海康门禁设备的通行记录
	 * @return
	 */
	@Bean(name ="sevenJobDetail")
	public JobDetailFactoryBean sevenJobDetail() {
		JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
		jobDetail.setJobClass(GetHCRecord.class);
		return jobDetail;
	}
	
	@Bean(name ="sevenTrigger")
	public CronTriggerFactoryBean sevenTriggerFactoryBean(JobDetail sevenJobDetail) {
		CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
		trigger.setCronExpression("0 30 13,22 * * ?");
		trigger.setJobDetail(sevenJobDetail);
		return trigger;
	}
	/**
	 * 	第八个任务：定时获取网络摄像头通行记录
	 * 
	 * @return
	 */
	@Bean(name ="eightJobDetail")
	public JobDetailFactoryBean eightJobDetail() {
		JobDetailFactoryBean jobDetail = new JobDetailFactoryBean();
		jobDetail.setJobClass(HCRecord.class);
		return jobDetail;
	}
	
	@Bean(name ="eightTrigger")
	public CronTriggerFactoryBean eightTriggerFactoryBean(JobDetail eightJobDetail) {
		CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
		trigger.setCronExpression("0 0 21 * * ?");
		trigger.setJobDetail(eightJobDetail);
		return trigger;
	}
	/**
	 * 3.创建Scheduler对象
	 */
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(Trigger ... triggers) {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setOverwriteExistingJobs(true);
		// 延时启动 
		//factory.setStartupDelay(20);
		// 自定义Job Factory，用于Spring注入
		factory.setJobFactory(jobFactory);
		factory.setTriggers(triggers); // 设置多个定时任务配置
		return factory;

	}

}
