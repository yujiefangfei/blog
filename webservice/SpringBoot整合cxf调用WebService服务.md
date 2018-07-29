
<center> <h1>SpringBoot整合cxf调用WebService服务</h1></center>

#1.WebService简介

WebService是一种跨编程语言和跨操作系统平台的远程调用技术。跨编程语言和跨操作平台，就是说服务端程序采用java编写，客户端程序则可以采用其他编程语言编写，反之亦然！跨操作系统平台则是指服务端程序和客户端程序可以在不同的操作系统上运行。远程调用，就是一台计算机a上的一个程序可以调用到另外一台计算机b上的一个对象的方法。

#2.发布WebService服务
  
  最近的工作中,主要调用第三方webserivce服务，为了方便就在本地模拟了一个webserivce服务端，因此服务端就简单用了java project。<br/>
生成一个webservice服务端分三个部分：服务端接口、实现接口、发布服务


1. 编写服务端接口

		package com.idea;

		import javax.jws.WebMethod;

		@javax.jws.WebService
		public interface TestWebService {
			@WebMethod
			String helloUser(User user);
		}
2. 实现接口

		package com.idea;

		@javax.jws.WebService
		public class TestWebServiceImpl implements TestWebService{

			@Override
			public String helloUser(User user) {
				return "name:"+user.getName()
				+" ,sex"+user.getSex();
			}
		}
3. 发布服务

		package com.idea;

		import javax.xml.ws.Endpoint;

		public class TestWebServicePublish {
			public static void main(String[] args){
				String address="http://localhost:8099/Webservice/   helloUser";  //服务发布路径，客户端调用的时候会用到
				Endpoint.publish(address, new TestWebServiceImpl());
				System.out.println("发布 helloUser webservice 成功！");
			}
		}
4. 以上用到User类，该类中的属性如下：


		public class User {
			private String name;
			private String sex;
		}
5. 运行TestWebServicePublish，控制台输出：发布 helloUser webservice 成功！<br/>访问http://localhost:8080/Webservice/helloUser?wsdl，可以看到该服务的wsdl文档


#3.SpringBoot整合cxf调用WebService服务
1. 在pom.xml文件中添加cxf的依赖

		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-ws</artifactId>
            <version>1.2.2.RELEASE</version>
        </dependency>
		<dependency>
    		<groupId>org.apache.cxf</groupId>
    		<artifactId>cxf-rt-frontend-jaxws</artifactId>
    		<version>3.0.4</version>
		</dependency>
		<dependency>
    		<groupId>org.apache.cxf</groupId>
    		<artifactId>cxf-rt-transports-http</artifactId>
    		<version>3.0.4</version>
		</dependency>
		<dependency>
    		<groupId>org.apache.cxf</groupId>
    		<artifactId>cxf-core</artifactId>
    		<version>3.0.4</version>
		</dependency>

2. 调用WebService服务
由于只是做示例，就把调用服务端的代码写在了spring boot的启动类中，代码如下：


		import java.lang.reflect.Method;
		import org.apache.cxf.endpoint.Client;
		import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
		import org.apache.cxf.staxutils.StaxUtils;
		import org.apache.cxf.transport.http.HTTPConduit;
		import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
		import org.springframework.boot.SpringApplication;
		import org.springframework.boot.autoconfigure.SpringBootApplication;

		@SpringBootApplication
		public class Application {
		public static void main(String[] args) throws Exception{
			SpringApplication.run(Application.class, args);
			testWebservice();
		}
	
		public static void testWebservice() throws Exception{
			//System.setProperty(StaxUtils.ALLOW_INSECURE_PARSER,"true");
			// 创建动态客户端
        	JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
        	// 创建客户端连接       
        	Client client = factory.createClient("http://localhost:8099/Webservice/helloUser?wsdl");
        	//设置超时时间
        	HTTPConduit conduit = (HTTPConduit) client.getConduit();
			HTTPClientPolicy policy = new HTTPClientPolicy();
			long timeout = 60*1000;//60秒
			policy.setConnectionTimeout(timeout);//连接超时
			policy.setReceiveTimeout(timeout);//响应超时
			conduit.setClient(policy);
		
			//获取User类的实例并赋值
       		 Object user = Thread.currentThread().getContextClassLoader().loadClass("com.idea.User").newInstance();
			//1.姓名
        	Method setName = user.getClass().getMethod("setName", String.class);
        	setName.invoke(user, "张三");//		    
			//2.性别
	    	Method setSex = user.getClass().getMethod("setSex", String.class);
	    	setSex.invoke(user, "男");		    
	    
	    	//调用服务端helloUser方法，并传入参数user
			Object[] result = client.invoke("helloUser",user);

			System.out.println(result[0]);
		}
		}
3. 首先要启动服务端，然后再启动客户端。<br/>
	启动客户端后，控制台会打印出调用服务端方法获取的返回值,如下图
	![Alt text](/images/webservice1.jpg)

#4.调用WebService服务可能遇到的问题
1. `java.lang.RuntimeException: Cannot create a secure XMLInputFactory`,解决这个问题，需要在创建动态客户端代码前面加上`System.setProperty(StaxUtils.ALLOW_INSECURE_PARSER,"true");`


		java.lang.RuntimeException: Cannot create a secure XMLInputFactory
        	at org.apache.cxf.staxutils.StaxUtils.createXMLInputFactory(StaxUtils.java:315)
        	at org.apache.cxf.staxutils.StaxUtils.getXMLInputFactory(StaxUtils.java:265)
        	at org.apache.cxf.staxutils.StaxUtils.createXMLStreamReader(StaxUtils.java:1701)
        	at org.apache.cxf.interceptor.StaxInInterceptor.handleMessage(StaxInInterceptor.java:123)
        	at org.apache.cxf.phase.PhaseInterceptorChain.doIntercept(PhaseInterceptorChain.java:307)
        	at org.apache.cxf.endpoint.ClientImpl.onMessage(ClientImpl.java:802)
        	at org.apache.cxf.transport.http.HTTPConduit$WrappedOutputStream.handleResponseInternal(HTTPConduit.java:1638)
        	at org.apache.cxf.transport.http.HTTPConduit$WrappedOutputStream.handleResponse(HTTPConduit.java:1527)
        	at org.apache.cxf.transport.http.HTTPConduit$WrappedOutputStream.close(HTTPConduit.java:1330)
        	at org.apache.cxf.transport.AbstractConduit.close(AbstractConduit.java:56)
        	at org.apache.cxf.transport.http.HTTPConduit.close(HTTPConduit.java:638)
        	at org.apache.cxf.interceptor.MessageSenderInterceptor$MessageSenderEndingInterceptor.handleMessage(MessageSenderInterceptor.java:62)
        	at org.apache.cxf.phase.PhaseInterceptorChain.doIntercept(PhaseInterceptorChain.java:307)
        	at org.apache.cxf.endpoint.ClientImpl.doInvoke(ClientImpl.java:516)
        	at org.apache.cxf.endpoint.ClientImpl.invoke(ClientImpl.java:425)

2. 包冲突：添加cxf相关依赖包之后，可能会跟原有项目中的包冲突。可以根据报错信息，排除冲突的包。