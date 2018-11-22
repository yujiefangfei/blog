### 1.quartz简介
Quartz是OpenSymphony开源组织在Job scheduling领域又一个开源项目，是完全由java开发的一个开源的任务日程管理系统，“任务进度管理器”就是一个在预先确定（被纳入日程）的时间到达时，负责执行（或者通知）其他软件组件的系统。

Quartz用一个小Java库发布文件（.jar文件），这个库文件包含了所有Quartz核心功能。这些功能的主要接口(API)是Scheduler接口。它提供了简单的操作，例如：将任务纳入日程或者从日程中取消，开始/停止/暂停日程进度。 [官网](http://www.quartz-scheduler.org/) 

### 2.定时器种类
Quartz中五种类型的Trigger：SimpleTrigger，CronTrigger，DataIntervalTrigger，NthIncludedDayTrigger和Calendar类

最常用的：

 - SimpleTrigger:用来触发只需执行一次或者在给定时间触发并且重复N次且每次执行延迟一定时间的任务
 - CronTrigger:按照日历触发，例如“每个周五”，“每个月10号”，“每天11点”

### 3.存储方式：RAMJobStore和JDBCJobStore
1. RAMJobStore：将调度信息存放在内存中，应用重启后，会丢失之前的调度信息
	 * 优点：不要外部数据库，配置容易，运行速度快
	 * 缺点：应用程序停止运行时，所有调度信息将被丢失；因为存储到JVM内存里面，所以可以存储的Job和Trigger个数将会受到限制
2. JDBCJobStore：将调度信息存放在数据库中，应用重启，之前的调度信息还存在
	 * 优点：支持集群，因为所有的任务信息都会保存到数据库中，可以控制事物，还有就是如果应用服务器关闭或者重启，任务信息都不会丢失，并且可以恢复因服务器关闭或者重启而导致执行失败的任务
	 * 缺点：运行速度的快慢取决与连接数据库的快慢
### 4.表关系和解释
* qrtz_blob_triggers：Trigger作为Blob类型存储(用于Quartz用户用JDBC创建他们自己定制的Trigger类型，JobStore 并不知道如何存储实例的时候)
* qrtz_calendars：以Blob类型存储Quartz的Calendar日历信息， quartz可配置一个日历来指定一个时间范围
* qrtz_cron_triggers：存储Cron Trigger，包括Cron表达式和时区信息。
* qrtz_fired_triggers：存储与已触发的Trigger相关的状态信息，以及相联Job的执行信息
* qrtz_job_details：存储每一个已配置的Job的详细信息
* qrtz_locks：存储程序的非观锁的信息(假如使用了悲观锁)
* qrtz_paused_trigger_graps：存储已暂停的Trigger组的信息
* qrtz_scheduler_state：存储少量的有关 Scheduler的状态信息，和别的 Scheduler 实例(假如是用于一个集群中)
* qrtz_simple_triggers：存储简单的 Trigger，包括重复次数，间隔，以及已触的次数
* qrtz_triggers：存储已配置的 Trigger的信息
* qrzt_simprop_triggers

### 5.核心类和关系
1. 核心类
	* QuartzSchedulerThread ：负责执行向QuartzScheduler注册的触发Trigger的工作线程
	* ThreadPool：Scheduler使用一个线程池作为任务运行的基础设施，任务通过共享线程池中的线程提供运行效率。
	* QuartzSchedulerResources：包含创建QuartzScheduler实例所需的所有资源（JobStore，ThreadPool等）。
	* SchedulerFactory ：提供用于获取调度程序实例的客户端可用句柄的机制。 
	* JobStore： 通过类实现的接口，这些类要为org.quartz.core.QuartzScheduler的使用提供一个org.quartz.Job和org.quartz.Trigger存储机制。作业和触发器的存储应该以其名称和组的组合为唯一性。
	* QuartzScheduler ：这是Quartz的核心，它是org.quartz.Scheduler接口的间接实现，包含调度org.quartz.Jobs，注册org.quartz.JobListener实例等的方法。 
	* Scheduler ：这是Quartz Scheduler的主要接口，代表一个独立运行容器。调度程序维护JobDetails和触发器的注册表。 一旦注册，调度程序负责执行作业，当他们的相关联的触发器触发（当他们的预定时间到达时）。
	* Trigger ：具有所有触发器通用属性的基本接口，描述了job执行的时间出发规则。 - 使用TriggerBuilder实例化实际触发器。
	* JobDetail ：传递给定作业实例的详细信息属性。 JobDetails将使用JobBuilder创建/定义。 
	* Job：要由表示要执行的“作业”的类实现的接口。只有一个方法 void execute(jobExecutionContext context) 
(jobExecutionContext 提供调度上下文各种信息，运行时数据保存在jobDataMap中) 
Job有个子接口StatefulJob ,代表有状态任务。 有状态任务不可并发，前次任务没有执行完，后面任务处于阻塞等到。

	
2. 核心元素的关系

Quartz 任务调度的核心元素是 scheduler, trigger 和 job，其中 trigger 和 job 是任务调度的元数据， scheduler 是实际执行调度的控制器。 

trigger 是用于定义调度时间的元素，即按照什么时间规则去执行任务。Quartz 中主要提供了四种类型的 trigger：SimpleTrigger，CronTirgger，DateIntervalTrigger，和 NthIncludedDayTrigger。

job 用于表示被调度的任务。主要有两种类型的 job：无状态的（stateless）和有状态的（stateful）。对于同一个 trigger 来说，有状态的 job 不能被并行执行，只有上一次触发的任务被执行完之后，才能触发下一次执行。Job 主要有两种属性：volatility 和 durability，其中 volatility 表示任务是否被持久化到数据库存储，而 durability 表示在没有 trigger 关联的时候任务是否被保留。两者都是在值为 true 的时候任务被持久化或保留。一个 job 可以被多个 trigger 关联，但是一个 trigger 只能关联一个 job。

scheduler 由 scheduler 工厂创建：DirectSchedulerFactory 或者 StdSchedulerFactory。 第二种工厂 StdSchedulerFactory 使用较多，因为 DirectSchedulerFactory 使用起来不够方便，需要作许多详细的手工编码设置。 Scheduler 主要有三种：RemoteMBeanScheduler， RemoteScheduler 和 StdScheduler。<br/>
![Alt text](/images/quartz1.jpg)<br/>
3. quartz线程

Quartz 中，有两类线程，Scheduler 调度线程和任务执行线程，其中任务执行线程通常使用一个线程池维护一组线程。
![Alt text](/images/quartz2.jpg)<br/>
Scheduler 调度线程主要有两个： 执行常规调度的线程，和执行 misfired trigger 的线程。常规调度线程轮询存储的所有 trigger，如果有需要触发的 trigger，即到达了下一次触发的时间，则从任务执行线程池获取一个空闲线程，执行与该 trigger 关联的任务。Misfire 线程是扫描所有的 trigger，查看是否有 misfired trigger，如果有的话根据 misfire 的策略分别处理。<br/><br/>
4. Quartz中运作的主要类如下

* QuartzScheduler： 任务调度器（内部使用）
* TreadPool： Quartz线程池，干活的线程就是从这里分配出去并管理的。默认的是SimpleTreadPool
* QuartzSchedulerThread： Quartz主线程，负责找到需要触发的作业，并交给TreadPool执行
* JobStore： 贮存器，负责提供JobDetail和Trigger，分RAMJobStore和JDBCJobStore两种，在quartz.properties中配置
* Trigger： 触发器父类，负责控制作业的触发时间。常用的是SimpleTrigger和CronTrigger
* SchedulerFactoryBean： Spring与Quartz的一个连接类，负责Quartz的初始化和启动工作。
该类实现了InitializingBean，SmartLifecycle接口。所以初始化和启动是由Spring负责调用的。


### 6.设置配置文件 quartz.properties 
 * org.quartz.scheduler.instanceName：DefaultQuartzScheduler //调度标识名 集群中每一个实例都必须使用相同的名称 （区分特定的调度器实例）
 * org.quartz.scheduler.instanceId ：AUTO //ID设置为自动获取 每一个必须不同 （所有调度器实例中是唯一的） 
 * org.quartz.jobStore.class ：org.quartz.impl.jdbcjobstore.JobStoreTX //数据保存方式为持久化 
 * org.quartz.jobStore.tablePrefix ： QRTZ_ //表的前缀 
 * org.quartz.jobStore.useProperties ： true //设置为TRUE不会出现序列化非字符串类到 BLOB 时产生的类版本问题
 * org.quartz.jobStore.isClustered ： false //加入集群 true 为集群 false不是集群
 * org.quartz.jobStore.clusterCheckinInterval：20000 //调度实例失效的检查时间间隔 
 * org.quartz.jobStore.misfireThreshold ：60000 //容许的最大作业延长时间 
 * org.quartz.threadPool.class：org.quartz.simpl.SimpleThreadPool //ThreadPool 实现的类名 
 * org.quartz.threadPool.threadCount ： 10 //线程数量 
 * org.quartz.threadPool.threadPriority ： 5（threadPriority 属性的最大值是常量 java.lang.Thread.MAX_PRIORITY，等于10。最小值为常量 java.lang.Thread.MIN_PRIORITY，为1） //线程优先级 
 * org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread： true //自创建父线程 
 * org.quartz.jobStore.dataSource ： qzDS //数据库别名
 * org.quartz.dataSource.qzDS.driver:com.mysql.jdbc.Driver //设置数据源
 * org.quartz.dataSource.qzDS.URL:jdbc:mysql://localhost:3306/quartz 
 * org.quartz.dataSource.qzDS.user:root 
 * org.quartz.dataSource.qzDS.password:123456 
 * org.quartz.dataSource.qzDS.maxConnection:10 
 

### 7.JDBC插入表顺序
主要的JDBC操作类，执行sql顺序。 

* Simple_trigger ：插入顺序 
qrtz_job_details —> qrtz_triggers —> qrtz_simple_triggers 
qrtz_fired_triggers 
* Cron_Trigger：插入顺序 
qrtz_job_details —> qrtz_triggers —> qrtz_cron_triggers 
qrtz_fired_triggers



### 8. 恢复调度后不同的Trigger，misfire对应的处理规则
1. 暂停后恢复调度，防止作业重跑问题
	* CronTrigger ：withMisfireHandlingInstructionDoNothing
	* SimpleTrigger ：withMisfireHandlingInstructionNextWithExistingCount
	
	同时要在quartz.properties 中配置容许的最大作业延长时间    org.quartz.jobStore.misfireThreshold ：60000 

2. CronTrigger <br/><br/>
	withMisfireHandlingInstructionDoNothing
	 * 不触发立即执行
	 * 等待下次Cron触发频率到达时刻开始按照Cron频率依次执行
	 
	withMisfireHandlingInstructionIgnoreMisfires
	 * 以错过的第一个频率时间立刻开始执行
	 * 重做错过的所有频率周期后
	 * 当下一次触发频率发生时间大于当前时间后，再按照正常的Cron频率依次执行
	 
	withMisfireHandlingInstructionFireAndProceed
	  * 以当前时间为触发频率立刻触发一次执行
	  * 然后按照Cron频率依次执行
	  <br/><br/>
3. SimpleTrigger <br/><br/>
	withMisfireHandlingInstructionFireNow
	 * 以当前时间为触发频率立即触发执行
	 * 执行至FinalTIme的剩余周期次数
	 * 以调度或恢复调度的时刻为基准的周期频率，FinalTime根据剩余次数和当前时间计算得到
	 * 调整后的FinalTime会略大于根据starttime计算的到的FinalTime值
	
	withMisfireHandlingInstructionIgnoreMisfires
	 * 以错过的第一个频率时间立刻开始执行
	 * 重做错过的所有频率周期
	 * 当下一次触发频率发生时间大于当前时间以后，按照Interval的依次执行剩下的频率
	 * 共执行RepeatCount+1次
	 
	withMisfireHandlingInstructionNextWithExistingCount
	 * 不触发立即执行
	 * 等待下次触发频率周期时刻，执行至FinalTime的剩余周期次数
	 * 以startTime为基准计算周期频率，并得到FinalTime
	 * 即使中间出现pause，resume以后保持FinalTime时间不变
	 
	withMisfireHandlingInstructionNowWithExistingCount
	 * 以当前时间为触发频率立即触发执行
	 * 执行至FinalTIme的剩余周期次数
	 * 以调度或恢复调度的时刻为基准的周期频率，FinalTime根据剩余次数和当前时间计算得到
	 * 调整后的FinalTime会略大于根据starttime计算的到的FinalTime值
	 
	withMisfireHandlingInstructionNextWithRemainingCount
	 * 不触发立即执行
	 * 等待下次触发频率周期时刻，执行至FinalTime的剩余周期次数
	 * 以startTime为基准计算周期频率，并得到FinalTime
	 * 即使中间出现pause，resume以后保持FinalTime时间不变
	 
	withMisfireHandlingInstructionNowWithRemainingCount
	 * 以当前时间为触发频率立即触发执行
	 * 执行至FinalTIme的剩余周期次数
	 * 以调度或恢复调度的时刻为基准的周期频率，FinalTime根据剩余次数和当前时间计算得到
	 * 调整后的FinalTime会略大于根据starttime计算的到的FinalTime值
	 
	MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT
	 * 此指令导致trigger忘记原始设置的starttime和repeat-count
	 * 触发器的repeat-count将被设置为剩余的次数
	 * 这样会导致后面无法获得原始设定的starttime和repeat-count值

### 9. 示例