package com.idea.config;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
@EnableScheduling
public class QuartzConfig {
	@Autowired
	private DataConfig dataconfig;
	
	@Autowired
    private MyJobFactory myJobFactory;
	
	private SchedulerFactoryBean factory;
	
	public SchedulerFactoryBean getFactory() {
		return factory;
	}

	public void setFactory(SchedulerFactoryBean factory) {
		this.factory = factory;
	}
	
	 @Bean
	 public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
	        factory = new SchedulerFactoryBean();
	        factory.setOverwriteExistingJobs(true);
	        //factory.setStartupDelay(20);	        
	        factory.setQuartzProperties(quartzProperties());
	        factory.setDataSource(dataconfig.dataSource());
	        factory.setJobFactory(myJobFactory);
	        return factory;
	  }
	    
	  @Bean
	  public Properties quartzProperties() throws IOException {
	        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
	        propertiesFactoryBean.setLocation(new FileSystemResource(System.getProperties().getProperty( "user.dir" )+File.separator
	        		+"src\\main\\resources\\"+"quartz.properties"));
	        propertiesFactoryBean.afterPropertiesSet();
	        return propertiesFactoryBean.getObject();
	  }
}
