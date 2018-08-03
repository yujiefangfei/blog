package com.idea.model;

import java.io.Serializable;
import java.util.Date;
import org.quartz.Trigger.TriggerState;
import com.idea.model.constant.CycleType;


public class ScheduleBean implements Serializable, Comparable<ScheduleBean> {

	private static final long serialVersionUID = 1L;
	/**
	 * 触发器名
	 */
	private String triggerName;
	/**
	 * 触发器组
	 */
	private String triggerGroup;
	/**
	 * 触发器状态
	 */
	private TriggerState triggerState;
	/**
	 * 下一次运行时间
	 */
	private String nextFireTime;
	/**
	 * 作业名
	 */
	private String jobName;
	
	/**
	 * 调度名
	 */
	private String dispatchName;
	
	/**
	 * 调度组
	 */
	private String dispatchGroup;
	/**
	 * 描述
	 */
	private String description;
	private String cronString;
	/**
	 * 重复次数
	 */
	private int repeatCount;
	private long repeatInterval;
	/**
	 * 创建开始时间
	 */
	private String startTime;
	/**
	 * 开始时间
	 */
	private String startDate;
	/**
	 * 结束时间
	 */
	private String endDate;
	/**
	 * 名称
	 */
	private String middlePath;
	private String version;

	/**
	 * 输入源id
	 */
	private int inputSourceId;
	/**
	 * 类型
	 */
	private String fileType;
	private String repName;
	/**
	 * 路径
	 */
	private String actionPath;
	/**
	 * 周期
	 */
	private CycleType cycle;
	/**
	 * 周期时间
	 */
	private String cycleNum;
	private String dayType;
	private String monthType;
	private String yearType;
	private String dayNum;
	
	/**
	 * 一周中的所选几天
	 */
	private String weekNum;
	private String monthNum;
	private int userId;
	private String userName;
	private boolean edit;
	private String execType;
	private Date createTime;
	private int jobId;
	
	/**
	 * 是否创建调度
	 */
	private boolean dispatchEnable;
	
	/**
	 * 是否完成提交
	 */
	private boolean commitEnable;

	/**
	 * 是否是重用调度
	 */
	private boolean reuseEnable;
	
	private String[] weekchecks;
	
	public boolean isReuseEnable() {
		return reuseEnable;
	}

	public void setReuseEnable(boolean reuseEnable) {
		this.reuseEnable = reuseEnable;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMiddlePath() {
		return this.middlePath;
	}

	public void setMiddlePath(String middlePath) {
		this.middlePath = middlePath;
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public boolean getEdit() {
		return this.edit;
	}

	public String getDispatchName() {
		return dispatchName;
	}

	public void setDispatchName(String dispatchName) {
		this.dispatchName = dispatchName;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public String getTriggerName() {
		return this.triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	public String getTriggerGroup() {
		return this.triggerGroup;
	}

	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	public TriggerState getTriggerState() {
		return this.triggerState;
	}

	public void setTriggerState(TriggerState triggerState) {
		this.triggerState = triggerState;
	}

	public String getNextFireTime() {
		return this.nextFireTime;
	}

	public void setNextFireTime(String nextFireTime) {
		this.nextFireTime = nextFireTime;
	}

	public String getJobName() {
		return this.jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobGroup() {
		return this.dispatchGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.dispatchGroup = jobGroup;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCronString() {
		return this.cronString;
	}

	public void setCronString(String cronString) {
		this.cronString = cronString;
	}

	public int getRepeatCount() {
		return this.repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public long getRepeatInterval() {
		return this.repeatInterval;
	}

	public void setRepeatInterval(long repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	public String getStartTime() {
		return this.startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getRepName() {
		return this.repName;
	}

	public void setRepName(String repName) {
		this.repName = repName;
	}

	public String getActionPath() {
		return this.actionPath;
	}

	public void setActionPath(String actionPath) {
		this.actionPath = actionPath;
	}

	public CycleType getCycle() {
		return this.cycle;
	}

	public void setCycle(CycleType cycle) {
		this.cycle = cycle;
	}

	public String getCycleNum() {
		return this.cycleNum;
	}

	public void setCycleNum(String cycleNum) {
		this.cycleNum = cycleNum;
	}

	public String getDayType() {
		return this.dayType;
	}

	public void setDayType(String dayType) {
		this.dayType = dayType;
	}

	public String getMonthType() {
		return this.monthType;
	}

	public void setMonthType(String monthType) {
		this.monthType = monthType;
	}

	public String getYearType() {
		return this.yearType;
	}

	public void setYearType(String yearType) {
		this.yearType = yearType;
	}

	public String getDayNum() {
		return this.dayNum;
	}

	public void setDayNum(String dayNum) {
		this.dayNum = dayNum;
	}

	public String getWeekNum() {
		return this.weekNum;
	}

	public void setWeekNum(String weekNum) {
		this.weekNum = weekNum;
	}

	public String getMonthNum() {
		return this.monthNum;
	}

	public void setMonthNum(String monthNum) {
		this.monthNum = monthNum;
	}

	public String getExecType() {
		return this.execType;
	}

	public void setExecType(String execType) {
		this.execType = execType;
	}

	public int getJobId() {
		return jobId;
	}

	public void setJobId(int jobId) {
		this.jobId = jobId;
	}

	@Override
	public int compareTo(ScheduleBean s) {
		if (this == s) {
			return 0;
		} else if (s != null && createTime != null) {
			if (createTime.before(s.createTime)) {
				return 1;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}

	public boolean getDispatchEnable() {
		return dispatchEnable;
	}

	public void setDispatchEnable(boolean dispatchEnable) {
		this.dispatchEnable = dispatchEnable;
	}

	public int getInputSourceId() {
		return inputSourceId;
	}

	public void setInputSourceId(int inputSourceId) {
		this.inputSourceId = inputSourceId;
	}

	public boolean isCommitEnable() {
		return commitEnable;
	}

	public void setCommitEnable(boolean commitEnable) {
		this.commitEnable = commitEnable;
	}

	public String[] getWeekchecks() {
		return weekchecks;
	}

	public void setWeekchecks(String[] weekchecks) {
		this.weekchecks = weekchecks;
	}
	

}