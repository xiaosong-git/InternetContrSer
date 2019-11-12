package com.uppermac.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

@Component
public class JobFactory extends AdaptableJobFactory {
	
	
	@Autowired
	private AutowireCapableBeanFactory autowiredCaptableBeanFactory;

	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		// 调用父类的方法
		Object o = super.createJobInstance(bundle);
		// 将o对象添加到springIOC容器中,并完成注入
		autowiredCaptableBeanFactory.autowireBean(o);
		return o;

	}

}
