package com.landray.kmss.km.review.webservice;

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
	    
	    //调用服务端方法，并传入参数user
		Object[] result = client.invoke("helloUser",user);

		System.out.println(result[0]);
	}
}
