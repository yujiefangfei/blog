前提：已经准备好三个linux(centos)节点
<center> <h1>hadoop伪分布式集群搭建</h1></center>
###1.安装jdk
1. 下载：<http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase7-521261.html>   
2. 解压： tar -zxvf jdk-7u79-linux-x64.gz
3. 配置环境变量：需要root权限，然后输入命令 vim /etc/profile

		export JAVA_HOME=/home/java/jdk1.7.0_79 （jdk安装位置）
		export CLASSPATH=.:$JAVA_HOME/jre/lib/rt.jar:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
		export PATH=$PATH:$JAVA_HOME/bin

4. 使配置生效： source /etc/profile
###2.安装hadoop2.7
  先在master节点安装，然后在copy到其他slave节点，在此master节点为：192.168.0.182<br />

1. 下载:hadoop-2.7.6.tar.gz  <http://hadoop.apache.org/releases.html>
2. 解压：tar -xzvf hadoop-2.7.6.tar.gz /home/hadoop
3. 在hadoop-2.7.6文件夹下创建存放数据的文件夹：tmp、hdfs、hdfs/data、hdfs/name
4. 配置core-site.xml文件

		<configuration>
			<!--指定namenode的地址-->
			<property>
				<name>fs.defaultFS</name>
				<value>hdfs://192.168.0.182:9000</value>
			</property>
			<!--用来指定使用hadoop时产生文件的存放目录-->
			<property>
				<name>hadoop.tmp.dir</name>
				<value>file:/home/hadoop/tmp</value>
			</property>
			<!--用作序列化文件处理时读写buffer的大小-->
			<property>
				<name>io.file.buffer.size</name>
				<value>131702</value>
			</property>
		</configuration>
5. 配置hdfs-site.xml

		<configuration>
			<!--指定hdfs中namenode的存储位置-->
			<property>
				<name>dfs.namenode.name.dir</name>
				<value>file:/home/hadoop/hdfs/name</value>
			</property>
			<!--指定hdfs中datanode的存储位置-->
			<property>
				<name>dfs.datanode.data.dir</name>
				<value>file:/home/hadoop/hdfs/data</value>
			</property>
			<!--指定hdfs保存数据的副本数量-->
			<property>
				<name>dfs.replication</name>
				<value>2</value>
			</property>
			<!--指定hdfs中SecondaryNameNode的存储位置-->
			<property>
				<name>dfs.namenode.secondary.http-address</name>
				<value>192.168.0.182:9001</value>
			</property>
			<!--开启WebHDFS服务-->
			<property>
				<name>dfs.webhdfs.enabled</name>
				<value>true</value>
			</property>
		</configuration>
6. 配置mapred-site.xml

		<configuration>
			<!-- 使用 Yarn 框架执行 map-reduce 处理程序 -->
			<property>
				<name>mapreduce.framework.name</name>
				<value>yarn</value>
			</property>
			<!--指定MapReduce JobHistory Server地址-->
			<property>
				<name>mapreduce.jobhistory.address</name>
				<value>192.168.0.182:10020</value>
			</property>
			<!--指定MapReduce JobHistory Server Web UI地址-->
			<property>
				<name>mapreduce.jobhistory.webapp.address</name>
				<value>192.168.0.182:19888</value>
			</property>
		</configuration>
7. 配置yarn-site.xml

		<configuration>
			<!--以下两条是设置shuffle service -->
			<property>
				<name>yarn.nodemanager.aux-services</name>
				<value>mapreduce_shuffle</value>
			</property>
			<property>
				<name>yarn.nodemanager.auxservices.mapreduce.shuffle.class</name>
				<value>org.apache.hadoop.mapred.ShuffleHandler</value>
			</property>
			 <!--ResourceManager对客户端暴露的地址,客户端通过该地址向RM提交应用程序，杀死应用程序等-->  
			<property>
				<name>yarn.resourcemanager.address</name>
				<value>192.168.0.182:8032</value>
			</property>
			<!--ResourceManager 对ApplicationMaster暴露的访问地址。ApplicationMaster通过该地址向RM申请资源、释放资源等-->
			<property>
				<name>yarn.resourcemanager.scheduler.address</name>
				<value>192.168.0.182:8030</value>
			</property>
			<!--ResourceManager 对NodeManager暴露的地址.。NodeManager通过该地址向RM汇报心跳，领取任务等 -->
			<property>
				<name>yarn.resourcemanager.resource-tracker.address</name>
				<value>192.168.0.182:8031</value>
			</property>
			<!--ResourceManager 对管理员暴露的访问地址。管理员通过该地址向RM发送管理命令等-->
			<property>
				<name>yarn.resourcemanager.admin.address</name>
				<value>192.168.0.182:8033</value>
			</property>
			<!--ResourceManager对外web ui地址。用户可通过该地址在浏览器中查看集群各类信息。-->
			<property>
				<name>yarn.resourcemanager.webapp.address</name>
				<value>192.168.0.182:8088</value>
			</property>
			<!--NodeManager总的可用物理内存,默认值是8192MB,即使你的机器内存不够8192MB，YARN也会按照这些内存来使用,因此，这个值通常一定要配置-->
			<property>
				<name>yarn.nodemanager.resource.memory-mb</name>
				<value>768</value>
			</property>
		</configuration>
8. 配置hadoop-env.sh、yarn-env.sh的JAVA_HOME

		export JAVA_HOME=/home/java/jdk1.7.0_79
9. 配置slaves，删除默认的localhost，增加2个从节点：

		192.168.0.183 
		192.168.0.184 
10. 将配置好的Hadoop复制到各个节点对应位置上：

		scp -r /home/hadoop 192.168.0.183:/home/
		scp -r /home/hadoop 192.168.0.184:/home/
11. 在Master服务器启动hadoop
	* 要先进入hadoop目录下
	* 初始化，输入命令，bin/hdfs namenode -format
	* 全部启动：sbin/start-all.sh
	* 停止：sbin/stop-all.sh
	* 输入jps，可查看相关启动信息
	* 也可以访问Hadoop的默认端口号50070，打开http://masert:50070网址，查看Hadoop相关信息

12. web访问，先要开放端口或者直接关闭防火墙
	* 输入命令，systemctl stop firewalld.service
	* 浏览器打开http://192.168.0.182:8088/
	* 浏览器打开http://192.168.0.182:50070/
13. 测试wordcount
	* 首先要在本地/home下新建一个word.txt，并写入一些英文单词到该文件。
	* 在hdfs上新建一个input目录`hadoop fs -mkdir /input`
	* 将本地的word.txt文件放入hdfs的input目录下 `hapdoop fs -put /home/word.txt  /input`
	* 在hadoop上执行wordcount程序：<br/>`hadoop jar /home/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.6.jar wordcount /input  /output`
	* output为输出目录，不能事先建好
	* 查看输出结果：<br/>
	`hadoop fs -ls output`<br/>
	`hadoop fs -cat output/part-r-00000`
###3.遇到的问题
1. HDFS block丢失过多进入安全模式（Safe mode）的解决方法
	

		The number of live datanodes 3 has reached the minimum number 0.
		Safe mode will be turned off automatically once the thresholds have been reached.
		Caused by: org.apache.hadoop.hdfs.server.namenode.SafeModeException: Log not rolled.
 		Name node is in safe mode.
		The reported blocks 632758 needs additional 5114 blocks to reach the threshold 0.9990 of total blocks 638510.
		The number of live datanodes 3 has reached the minimum number 0.

		Safe mode will be turned off automatically once the thresholds have been reached.

		at org.apache.hadoop.hdfs.server.namenode.FSNamesystem.checkNameNodeSafeMode

		(FSNamesystem.java:1209)

   		 ... 12 more	
	原因分析(Cause Analysis)*

由于系统断电，内存不足等原因导致dataNode丢失超过设置的丢失百分比，系统自动进入安全模式

解决办法(Solution)*

	安装HDFS客户端，并执行如下命令：

	* 步骤 1   执行命令退出安全模式：`hadoop dfsadmin -safemode leave`
	* 步骤 2   执行健康检查，删除损坏掉的`block：  hdfs fsck  /  -delete`

注意: 这种方式会出现数据丢失，损坏的block会被删掉
