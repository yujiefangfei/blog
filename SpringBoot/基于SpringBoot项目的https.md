
<center> <h1>基于SpringBoot项目的https</h1></center>
###1.生成和导入证书步骤：
1. 生成请求证书：   
1.1&nbsp;&nbsp;keytool -genkey -alias [Name] -keypass [Password] -keyalg RSA -keysize 2048 -validity 18250 -keystore [Name]<br/>
-alias:后的Name为要申请的证书的别名<br/>
-keypass：后的参数【Password]为证书加密密码，后续步骤还会用到<br/>
-keystore：后的参数【Name】为证书存放空间名称，可以随意填写<br/>

	例子,cmd然后输入<br/>`Keytool -genkey -alias www.test.com -keypass 12345678 -	keyalg RSA -keysize 2048 -validity 18250 -keystore www.test.com`
	![Alt text](/images/https1.jpg)
1.2&nbsp;&nbsp;keytool -certreq -keyalg RSA -alias [Name] -file request.csr -keystore [Name]<br/>
-alias：后的参数[name]必须与步骤1.1相同<br/>
-keystore：后的name必须与1.1中-keystore后的相同<br/>
	例子,cmd然后输入<br/>`keytool -certreq -keyalg RSA -alias [Name] -file request.csr -keystore [Name]` <br/>
会生成一个request.csr,用该请求文件换取颁发的证书(www.test.com.cer)和请求证书(mid.cer)

2. 导入中间证书<br/>
keytool -import -alias mid-cer -keystore Name -trustcacerts -storepass password -file mid.cer <br/>
Name、password必须与步骤1.1相同<br/>
例：`keytool -import -alias mid-cer -keystore www.test.com -trustcacerts -storepass 12345678 -file mid.cer`

3. 导入颁发的证书<br/>
Keytool -import -alias Name -keystore Name -trustcacerts -storepass password -file www.test.com.cer<br/>
Name、password必须与步骤1.1相同<br/>
例:`Keytool -import -alias www.test.com -keystore www.test.com -trustcacerts -storepass 12345678 -file www.test.com.cer`
上述步骤完成后，会生成一个以www.test.com命名的文件，这个文件就是项目中需要用到的。<br/>
4. 
###2.在springboot中配置https

1. 将www.test.com文件放入项目的src/main/resources文件夹下
2. 修改配置application.properties文件


		#https
		server.port=443
		server.ssl.key-store=classpath:www.test.com
		server.ssl.key-store-password=12345678
		server.ssl.keyStoreType=JKS
		server.ssl.keyAlias=www.test.com
		server.ssl.protocol=TLSv1.2
		server.ssl.enabled=true
		#server.ssl.cipher=ECDHE-RSA-AES256-SHA384
server.ssl.key-store: 设置证书存放路径<br/>
server.ssl.key-store-password：步骤1.1设置的密码<br/>
server.ssl.keyAlias：步骤1.1取的别名<br/>
server.ssl.protocol：https协议的类型。一些服务器会做限制，根据具体情况选择。分两大类：SSL,TLS。现在一般用TLS，SSL是TLS的前身。TLS有三个小版本：1.0 1.1 1.2  根据自己项目的具体情况设置。<br/>
3. 遇到的问题
  浏览器访问时报错：此网站无法提供安全连接。协议不受支持，客户端和服务器不支持一般SSL协议或加密套件。<br/>
  错误提示了SSL协议版本的问题。`server.ssl.protocol`就是用来配置协议的，改成跟服务器端一致的协议(如果不知道服务端协议可以一个一个的试)。<br/>
  `openssl s_client -connect serverAddress:port`命令可以查看使用协议的版本。
![Alt text](/images/https2.jpg)
