package com.idea.util;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Calendar;
import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.util.StringUtils;

import com.idea.model.ScheduleBean;
import com.idea.model.constant.CycleType;


public class NewScheduleUtil {
	public final static String DF = "yyyy-MM-dd";
	
	public final static String TF = "HH:mm:ss";
	
	public final static String DTF = "yyyy-MM-dd HH:mm:ss";
	
	
	public static ScheduleBean setUpCycle(ScheduleBean scheduleBean) {
		scheduleBean.setNextFireTime(scheduleBean.getStartDate()+" "+scheduleBean.getStartTime());
		String cronString = "";
		Calendar ca = Calendar.getInstance();
		if (!scheduleBean.getCycle().equals(CycleType.IMMEDIATELY)
				&& !StringUtils.isEmpty(scheduleBean.getStartDate())) {
			ca.setTime(StringUtil.stringToDate(scheduleBean.getStartDate() + " " + scheduleBean.getStartTime(), DTF));
		}
		String cycleNum = scheduleBean.getCycleNum();
		switch (scheduleBean.getCycle()) {
		case ONCE:
			scheduleBean.setRepeatCount(0);
			long repeatInterval = StringUtil.stringToDate(
					scheduleBean.getStartDate(), DF).getTime()
					-  System.currentTimeMillis();
			if (repeatInterval > 0L)
				scheduleBean.setRepeatInterval(repeatInterval);
			else {
				scheduleBean.setRepeatInterval(-repeatInterval);
			}
			break;
		case SECOND:
			scheduleBean.setRepeatInterval(Long.parseLong(cycleNum) * 1000L);
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			break;
		case MINUTE:
			scheduleBean
					.setRepeatInterval(Long.parseLong(cycleNum) * 60L * 1000L);
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			break;
		case HOUR:
			scheduleBean
					.setRepeatInterval(Long.parseLong(cycleNum) * 60L * 60L * 1000L);
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			break;
		case DAY:
			int cycleNumber = Integer.valueOf(cycleNum) + 1;
			cronString = ca.get(13) + " " + ca.get(12) + " " + ca.get(11) + " " + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/"+cycleNumber
			+ " * ? *";
			scheduleBean.setCronString(cronString);
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			break;
		case WEEK:
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			cronString = ca.get(13) + " " + ca.get(12) + " " + ca.get(11)
					+ " ? * " + scheduleBean.getWeekNum();
			scheduleBean.setCronString(cronString);
			break;
		case MONTH:
			cronString = ca.get(13) + " " + ca.get(12) + " " + ca.get(11) + " " + cycleNum + " * ?";
			scheduleBean.setCronString(cronString);
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			break;
		case YEAR:
			String yeartype = scheduleBean.getYearType();
			if ("0".equals(yeartype)) {
				String[] monthAndDay = cycleNum.split("-");

				cronString = ca.get(13) + " " + ca.get(12) + " " + ca.get(11)
						+ " " + monthAndDay[1] + " " + monthAndDay[0] + " ?";
			} else if ("1".equals(yeartype)) {
				String monthnum = scheduleBean.getMonthNum();
				String weeknum = scheduleBean.getWeekNum();
				String daynum = scheduleBean.getDayNum();

				if (!"L".equals(weeknum)) {
					weeknum = "#" + weeknum;
				}

				cronString = ca.get(13) + " " + ca.get(12) + " " + ca.get(11)
						+ " ? " + monthnum + " " + daynum + weeknum;
			}
			scheduleBean.setCronString(cronString);
			scheduleBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			break;
		default:
			break;
		}
		return scheduleBean;
	}
	
	public static Trigger setUpJobDataMapAndTrigger(ScheduleBean scheduleBean,JobDetail jobDetail) {
		Trigger trigger = null;
		JobDataMap data = jobDetail.getJobDataMap();

		data.put("scheduleBean", scheduleBean);
		Date endDate = StringUtils.isEmpty(scheduleBean.getEndDate()) ? null : StringUtil
				.stringToDate(scheduleBean.getEndDate() + " 23:59:59",
						"yyyy-MM-dd HH:mm:ss");
		try {
			if (scheduleBean.getCycle()==CycleType.IMMEDIATELY) {
				trigger = newTrigger()
						.withIdentity(scheduleBean.getDispatchName() + "_trigger",//scheduleBean.getJobName()
								    scheduleBean.getJobGroup())
						.startNow()
						.build();
			} else if (StringUtils.isEmpty(scheduleBean.getCronString())) {
				trigger = newTrigger()
						.withIdentity(scheduleBean.getDispatchName() + "_trigger",//
								scheduleBean.getJobGroup())
						.withSchedule(
								simpleSchedule().withIntervalInMilliseconds(
										scheduleBean.getRepeatInterval())
										.withRepeatCount(scheduleBean.getRepeatCount())
										.withMisfireHandlingInstructionNextWithExistingCount())//
						.startAt(
								StringUtil.stringToDate(
										scheduleBean.getStartDate()+" "+scheduleBean.getStartTime(),
										"yyyy-MM-dd HH:mm:ss")).endAt(endDate)
						.build();

			} else {
				trigger = newTrigger()
						.withIdentity(scheduleBean.getDispatchName() + "_trigger",//
								scheduleBean.getJobGroup())
						.withSchedule(
								cronSchedule(scheduleBean.getCronString())  //0 */3 * * * ?     "0 36 13 ? * *"
								.withMisfireHandlingInstructionDoNothing()) //
						.startAt(
								StringUtil.stringToDate(
										scheduleBean.getStartDate()+" "+scheduleBean.getStartTime(),
										"yyyy-MM-dd HH:mm:ss")).endAt(endDate)
						.build();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return trigger;
	}
}
