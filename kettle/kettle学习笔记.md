
<center> <h1>kettle学习笔记</h1></center>
###1.简介
Kettle是免费开源的基于java的企业级ETL工具。<br/>
特点：

1. 免费开源：基于java的免费开源的软件，对商业用户也没有限制  
2. 易配置：可以在Window、Linux、Unix上运行，绿色无需安装，数据抽取高效稳定
3. 不同数据库：ETL工具集，它允许你管理来自不同数据库的数据
4. 两种脚本文件：transformation和job，transformation完成针对数据的基础转换，job则完成整个工作流的控制
5. 图形界面设计：通过图形界面设计实现做什么业务，无需写代码去实现
6. 定时功能：在Job下的start模块，有一个定时功能，可以每日，每周等方式进行定时

###2.Kettle家族
目前包括4个产品：Spoon、Pan、CHEF、Kitchen
  
1. Spoon：允许你通过图形界面来设计ETL转换过程（Transformation）
2. Pan：允许你批量运行由Spoon设计的ETL转换 (例如使用一个时间调度器)。Pan是一个后台执行的程序，没有图形界面
3. CHEF：允许你创建任务（Job）。 任务通过允许每个转换，任务，脚本等等，更有利于自动化更新数据仓库的复杂工作。任务通过允许每个转换，任务，脚本等等。任务将会被检查，看看是否正确地运行了
4. Kitchen：允许你批量使用由Chef设计的任务 (例如使用一个时间调度器)。KITCHEN也是一个后台运行的程序

###3.Kettle的transformation和job
1. Kettle的执行分为两个层次：Job和Transformation。两个层次的最主要区别在于数据传递和运行方式。
	![Alt text](/images/kettle1.jpg)
2. Transformation（转换）<br/>
	*  Transformation（转换）是由一系列被称之为step（步骤）的逻辑工作的网络。转换本质上是数据流。转换可以从文本文件中读取数据，过滤，然后排序，最后将数据加载到数据库。本质上，转换是一组图形化的数据转换配置的逻辑结构。<br/>
	*  转换的两个相关的主要组成部分是step（步骤）和hops（节点连接）。
	*  转换文件的扩展名是.ktr。<br/>
	*  Steps（步骤）是转换的建筑模块，比如一个文本文件输入或者一个表输出就是一个步骤。在PDI中有140多个步骤，它们按不同功能进行分类，比如输入类、输出类、脚本类等。每个步骤用于完成某种特定的功能，通过配置一系列的步骤就可以完成你所需要完成的任务。<br/>
	*  Hops（节点连接）是数据的通道，用于连接两个步骤，使得元数据从一个步骤传递到另一个步骤。在的转换中，它像似顺序执行发生的，但事实并非如此。节点连接决定了贯穿在步骤之间的数据流，步骤之间的顺序不是转换执行的顺序。当执行一个转换时，每个步骤都以自己的线程启动，并不断的接受和推送数据。<br/>

	
3. Jobs（作业）<br/>
	*  Jobs（作业）是基于作业流模型的，协调数据源、执行过程和相关依赖性的ETL活动。
	*  Jobs（作业）将功能性和实体过程聚合在了一起。一个工作中展示的任务有从FTP获取文件、核查一个必须存在的数据库表是否存在、执行一个转换、发送邮件通知一个转换中的错误等。最终工作的结果可能是数据仓库的更新等。<br/>
	*  作业由工作节点连接、工作实体和工作设置组成。<br/>
	*  作业文件的扩展名是.kjb。
4. Variable（变量）<br/>
	*  根据变量的作用域，变量被分为两类：环境变量和kettle变量。<br/>
	*  环境变量：环境变量可以通过edit menu下面的set environment  variables对话框进行设置。使用环境变量的唯一的问题是，它不能被动态的使用。如果在同一个应用服务器中执行两个或多个使用同一环境变量的转换，将可能发生冲突。环境变量在所以使用jvm的应用中可见。<br/>
	*  Kettle变量：用于在一个小的动态范围内存储少量的信息。Kettle变量是kettle本地的，作用范围可以是一个工作或转换，在工作或转换中可以设置或修改。Set  variable步骤用来设置与此变量有关的工作从此设置其作用域，如：父工作、祖父工作或根工作。<br/>

###4.嵌入和扩展kettle功能

在工作中遇到了需要扩展kettle功能，并且在自己的java应用程序中执行kttle，即官网中描述的开发自定义插件以扩展PDI功能或将PDI引擎集成到自己的Java应用程序中。下面就分别来说明这两种方式：
####4.1 开发自定义kettle插件
kttle插件主要有以下四种类型：
[官方文档](https://help.pentaho.com/Documentation/8.1/Developer_Center/PDI/Extend) 

* kettle-sdk-step-plugin
* kettle-sdk-jobentry-plugin
* kettle-sdk-database-plugin
* kettle-sdk-partitioner-plugin

####4.2 将PDI引擎集成到自己的Java应用程序中
可以在其他Java应用程序中构建和运行PDI转换和作业 
[kttle wiki demo](https://wiki.pentaho.com/display/EAI/Pentaho+Data+Integration+-+Java+API+Examples) 

1. Run Transformations<br/>
分两种：一是执行.ktr文件或者reposritory；二是执行动态构建的转换。<br/>
步骤如下：<br/>
	* 初始化kettle环境<br/>
	`KettleEnvironment.init()`
	* 准备transfromation<br/>
	Transfromation(转换)由TransMeta对象表示,可以从.ktr文件，PDI存储库加载此对象，也可以动态生成它。
	* 执行transformation<br/>
	可执行的Trans对象派生自传递给构造函数的TransMeta对象,Trans对象启动，然后异步执行。要确保Trans对象的所有步骤都已完成，要调用`waitUntilFinished（）`。
	* 查看执行结果<br/>
	用`getResult()`来查看执行结果
	* 关闭监听<br/>
	`KettleEnvironment.shutdown()`
2. Run Jobs<br/>
步骤：<br/>
	* 初始化kettle环境<br/>
	`KettleEnvironment.init()`
	* 准备Job<br/>
	Job(作业)的定义由JobMeta对象表示,可以从.ktb 文件，PDI存储库加载此对象，也可以动态生成它。
	* 执行Job<br/>
	可执行作业对象派生自传递给构造函数的JobMeta对象,Job对象启动，然后在单独的线程中执行。要等待作业完成，请调用`waitUntilFinished`（）。
	* 查看执行结果<br/>
	用`getResult()`来查看执行结果
	* 关闭监听
	`KettleEnvironment.shutdown()`
3. Dynamically Build Transformations<br/>
步骤如下：<br/>
 * 初始化kettle环境<br/>
	`KettleEnvironment.init()`
 * 创建和配置转换定义对象<br/>
	转换定义由TransMeta对象表示。使用默认构造函数创建此对象。转换定义包括名称，声明的参数和所需的数据库连接。
 * 使用转换步骤填充TransMeta对象<br/>
	 + 通过直接实例化其类来创建该步骤，并使用其get和set方法对其进行配置。
	 +  获取步骤ID字符串。每个PDI步骤都有一个可以从PDI插件注册表中检索的ID。简单方法：`PluginRegistry.getInstance().getPluginId(StepPluginType.class, theStepMetaObject)`. 
	 + 通过将步骤ID字符串，名称和配置的步骤对象传递给构造函数来创建`org.pentaho.di.trans.step.StepMeta`的实例。StepMeta的一个实例封装了步骤属性，并控制了步骤在PDI客户端（Spoon）画布上的位置以及与跳跃的连接
	 + 创建StepMeta对象后，调用`setDrawn（true）`和`setLocation（x，y）`以确保步骤在PDI客户端画布上正确显示
	 + 通过在转换定义对象上调用`addStep（）`，将步骤添加到转换中。
 * 连接Hops<br/>
	 + 一旦将步骤添加到转换定义中，它们就需要通过Hops连接。要创建跃点，请创建`org.pentaho.di.trans.TransHopMeta`的实例，将From和To步骤作为参数传递给构造函数。通过调用`addTransHop（）`将跃点添加到转换定义中。
	 + 在通过Hops添加并连接所有步骤之后，可以通过调用`getXML（）`并在PDI客户端中将其打开以进行检查，将转换定义对象序列化为.ktr文件
4. Dynamically Build Jobs
 * 初始化kettle环境<br/>
	`KettleEnvironment.init()`
 * 创建和配置Job定义对象<br/>
	作业定义由JobMeta对象表示。使用默认构造函数创建此对象。作业定义包括名称，声明的参数和所需的数据库连接。
 * 使用Job Entries填充JobMeta对象<br/>
     + 通过直接实例化其类来创建Job Entrie，并使用其get和set方法对其进行配置。Job Entries位于`org.pentaho.di.job.entries`的子包中。
     + 通过将在上一步中创建的Job Entries传递给构造函数来创建`org.pentaho.di.job.entry.JobEntryCopy`的实例。JobEntryCopy的实例封装Job Entries的属性，并控制PDI客户端画布上的条目的位置以及与Hops的连接。
     + 创建后，调用`setDrawn（true）`和`setLocation（x，y）`以确保条目在PDI客户端画布上正确显示。
     + 通过在作业定义对象上调用`addJobEntry（）`将Job Entries添加到Job 。通过创建JobEntryCopy的多个实例并传入相同的Job Entries实例，可以在画布上的多个位置放置相同的Job Entries。
 * 连接Hops<br/>
将Job Entries添加到作业定义后，需要通过Hops连接它们，要创建一个Hops，请通过将From和To条目作为参数传递给构造函数来创建`org.pentaho.di.job.JobHopMeta`的实例。通过调用`setConditional（）`和`setEvaluation（true / false）`将其配置为绿色或红色跃点。如果是无条件跳，请调用`setUnconditional（）`。通过调用`addJobHop（）`将Hops加到作业定义。

###4.学习资料
* 官方文档： [http://infocenter.pentaho.com](http://infocenter.pentaho.com)
* 开发帮助文档：[http://wiki.pentaho.com/](http://wiki.pentaho.com/)
* 源码：[https://github.com/pentaho/pentaho-kettle.](https://github.com/pentaho/pentaho-kettle)
* JavaDoc: [http://community.pentaho.com/javadoc/](http://community.pentaho.com/javadoc/)
* Bug报告地址： [http://jira.pentaho.com/browse/PDI](http://jira.pentaho.com/browse/PDI)
* 官方论坛： [http://forums.pentaho.org/forumdisplay.php?f=135](http://forums.pentaho.org/forumdisplay.php?f=135)
* 中文论坛： [http://www.pentahochina.com](http://www.pentahochina.com)