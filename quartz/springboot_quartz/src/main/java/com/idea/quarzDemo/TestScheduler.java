package com.idea.quarzDemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.idea.config.QuartzConfig;
import com.idea.model.ScheduleBean;
import com.idea.util.NewScheduleUtil;
import com.idea.util.StringUtil;

@Service
public class TestScheduler {
	
	@Autowired
	private QuartzConfig quartzConfig;
	
	
	public void createScheduler() throws IOException, SchedulerException{
		ScheduleBean scheduleBean = new ScheduleBean();
		scheduleBean.setJobGroup("group");
		scheduleBean.setCreateTime(new Date());
		
		//获取Scheduler工厂，并且由工厂创建Scheduler实例
		SchedulerFactoryBean sb = quartzConfig.schedulerFactoryBean();
		Scheduler sched = sb.getScheduler();
		
		//由JobBuilder创建 job详细信息
		JobDetail jobDetail = newJob(TestJob.class)
						.withDescription("testjob")
						.withIdentity("name","groupName")
						.storeDurably().build();
		
		//有TriggerBuilder创建触发器trigger
		Trigger trigger = NewScheduleUtil.setUpJobDataMapAndTrigger(scheduleBean, jobDetail);
		
		Set<Trigger> set = new HashSet<Trigger>();
		set.add(trigger);
		//将jobDetail和trigger注册到Scheduler，并且启动
		sched.scheduleJob(jobDetail,set,true);
		sched.start();
	}
	
	
	/**
	 * 创建调度、并启动 CronTrigger
	 */
	public void testCronScheduler() throws IOException, SchedulerException{
		Date endDate = StringUtil.stringToDate("2018-08-02"+ " 23:59:59",
				"yyyy-MM-dd HH:mm:ss");
		Date startDate = StringUtil.stringToDate("2018-08-02"+" "+"00:26:00",
				"yyyy-MM-dd HH:mm:ss");
		
		//获取Scheduler工厂，并且由工厂创建Scheduler实例
		SchedulerFactoryBean sb = quartzConfig.schedulerFactoryBean();
		Scheduler sched = sb.getScheduler();
		
		//由JobBuilder创建 job详细信息
		JobDetail jobDetail = newJob(TestJob.class)
				.withDescription("testjob")
				.withIdentity("name","groupName")
				.storeDurably().build();
		
		//有TriggerBuilder创建触发器trigger，下面用的触发器是CronTrigger
		Trigger trigger = newTrigger()
				.withIdentity("name"+"_trigger","groupName")
				.withSchedule(cronSchedule("*/5 * * * * ?")
						.withMisfireHandlingInstructionDoNothing())// 暂停恢复后，不补跑job
				.startAt(startDate)
				.endAt(endDate)
				.build();
		
		Set<Trigger> set = new HashSet<Trigger>();
		set.add(trigger);
		//将jobDetail和trigger注册到Scheduler，并且启动
		sched.scheduleJob(jobDetail,set,true);
		sched.start();
	}
	
	/**
	 * 暂停调度
	 * @throws IOException
	 * @throws SchedulerException
	 */
	public void pause(JobKey jobkey) throws IOException, SchedulerException{
		SchedulerFactoryBean sb = quartzConfig.schedulerFactoryBean();
		Scheduler sched = sb.getScheduler();
		
		System.out.println("=============暂停job===========");
		sched.pauseJob(jobkey);
		System.out.println("=============暂停job 结束===========");	
	}
	
	/**
	 * 恢复调度
	 * @throws SchedulerException
	 * @throws IOException
	 */
	public void resume(JobKey jobkey) throws SchedulerException, IOException{
		SchedulerFactoryBean sb = quartzConfig.schedulerFactoryBean();
		Scheduler sched = sb.getScheduler();
		
		System.out.println("=============重用job===========");
		sched.resumeJob(jobkey);
		System.out.println("=============重用job  结束===========");
		
	}
	
	/**
	 * 删除调度
	 * @param jobkey
	 * @throws IOException
	 * @throws SchedulerException
	 */
	public void delete(JobKey jobkey) throws IOException, SchedulerException{
		SchedulerFactoryBean sb = quartzConfig.schedulerFactoryBean();
		Scheduler sched = sb.getScheduler();
		
		System.out.println("=============重用job===========");
		sched.deleteJob(jobkey);
		System.out.println("=============重用job  结束===========");
	}
	
	/**
	 * 获取所有计划中的任务列表
	 * @return
	 */
	public List<ScheduleBean> getAllJob(){
		
		List<ScheduleBean> scheduleList = new ArrayList<ScheduleBean>();
		try{
			SchedulerFactoryBean sb = quartzConfig.schedulerFactoryBean();
			Scheduler sched = sb.getScheduler();
			HashSet<JobKey> set = (HashSet<JobKey>)sched.getJobKeys(GroupMatcher.jobGroupEquals("group"));
			for(JobKey jobkey:set){
				JobDetail jobDetail = sched.getJobDetail(jobkey);
				JobDataMap data = jobDetail.getJobDataMap();
				List<?extends Trigger> triggers = sched.getTriggersOfJob(jobkey);
				ScheduleBean scheduleBean = (ScheduleBean) data.get("scheduleBean");
				
				if(triggers.size() > 0){
					scheduleBean.setJobGroup(triggers.get(0).getJobKey().getGroup());
					//scheduleBean.setJobName(triggers.get(0).getJobKey().getName());
					scheduleBean.setDispatchName(triggers.get(0).getJobKey().getName());////////////////
					scheduleBean.setTriggerGroup(triggers.get(0).getKey().getGroup());
					scheduleBean.setTriggerName(triggers.get(0).getKey().getName());
					scheduleBean.setDescription(jobDetail.getDescription());
					scheduleBean.setTriggerState(sched.getTriggerState(triggers.get(0).getKey()));
					scheduleBean.setNextFireTime(
							StringUtil.dateToString(triggers.get(0).getNextFireTime(), "yyyy-MM-dd HH:mm:ss"));
					scheduleBean.setStartDate(
							StringUtil.dateToString(triggers.get(0).getStartTime(), "yyyy-MM-dd"));
					scheduleBean.setStartTime(StringUtil.dateToString(triggers.get(0).getStartTime(), "HH:mm:ss"));
					scheduleBean
							.setEndDate(StringUtil.dateToString(triggers.get(0).getEndTime(), "yyyy-MM-dd"));

					if ((triggers.get(0) instanceof CronTrigger)) {
						scheduleBean.setCronString(((CronTrigger) triggers.get(0)).getCronExpression());
					} else if ((triggers.get(0) instanceof SimpleTrigger)) {
						scheduleBean.setRepeatInterval(((SimpleTrigger) triggers.get(0)).getRepeatInterval());
						scheduleBean.setRepeatCount(((SimpleTrigger) triggers.get(0)).getRepeatCount());
					}
				}else {
					scheduleBean.setTriggerState(TriggerState.COMPLETE);
					scheduleBean.setJobGroup(jobDetail.getKey().getGroup());
					scheduleBean.setDispatchName(jobDetail.getKey().getName());
					scheduleBean.setDescription(jobDetail.getDescription());
				}
				scheduleList.add(scheduleBean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return scheduleList;
	}
	
	/**
	 * 获取所有正在运行的job
	 * @return
	 * @throws IOException
	 * @throws SchedulerException
	 */
	public List<ScheduleBean> getAllRunningJob() throws IOException, SchedulerException{
		SchedulerFactoryBean sb = quartzConfig.schedulerFactoryBean();
		Scheduler sched = sb.getScheduler();
		List<JobExecutionContext> executingJobs = sched.getCurrentlyExecutingJobs();
		List<ScheduleBean> jobList = new ArrayList<ScheduleBean>(executingJobs.size());
		
		for(JobExecutionContext executingJob : executingJobs){
			ScheduleBean job = new ScheduleBean();
			JobDetail jobDetail = executingJob.getJobDetail();
			JobKey jobKey = jobDetail.getKey();
	        Trigger trigger = executingJob.getTrigger();
	        job.setJobName(jobKey.getName());
	        job.setJobGroup(jobKey.getGroup());
	        job.setDescription("触发器:" + trigger.getKey());
	        Trigger.TriggerState triggerState = sched.getTriggerState(trigger.getKey());
	        //job.setJobStatus(triggerState.name());
	        if (trigger instanceof CronTrigger) {
	            CronTrigger cronTrigger = (CronTrigger) trigger;
	            String cronExpression = cronTrigger.getCronExpression();
	            job.setCronString(cronExpression);
	        }
	        jobList.add(job);
		}
		return jobList;
	}
	
	/**
	 * 立即执行job
	 * @param jobKey
	 * @throws SchedulerException
	 * @throws IOException
	 */
	public void runAJobNow(JobKey jobKey) throws SchedulerException, IOException {
		SchedulerFactoryBean sb = quartzConfig.schedulerFactoryBean();
		Scheduler sched = sb.getScheduler();
	    sched.triggerJob(jobKey);
	}
	
	/**
	 * 更新job时间表达式
	 * @throws SchedulerException
	 * @throws IOException
	 */
	public void updateJobCron() throws SchedulerException, IOException {
		SchedulerFactoryBean sb = quartzConfig.schedulerFactoryBean();
		Scheduler sched = sb.getScheduler();

	    TriggerKey triggerKey = TriggerKey.triggerKey("triggerName","group");

	    CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);

	    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("");

	    trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

	    sched.rescheduleJob(triggerKey, trigger);
	}
}
