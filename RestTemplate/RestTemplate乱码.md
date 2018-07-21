
<center> <h1>RestTemplate的post请求中文乱码问题</h1></center>
###1.问题重现
需调用的接口如下：

		@RequestMapping(value = "/test", method = RequestMethod.POST)
		public Object createTransGet(HttpServletRequest request, HttpServletResponse response,@RequestBody String text) {
			System.out.println(text);
			return "ok";
		}
RestTemplate调用接口的代码如下：

		@Test
		public void test9() throws UnsupportedEncodingException{
			RestTemplate restTemplate = new RestTemplate();					
		    String url = "http://localhost:8080/test";
			JSONObject content = new JSONObject();
			content.put("content","内容内容内容内容内容内容内容");
			String user="用户";
			JSONObject obj = new JSONObject();
			obj.put("user", user);
			obj.put("id", 1111111);
			obj.put("text", content);			
			String result = restTemplate.postForObject(url, obj.toString(),String.class);
			System.out.println(result);
		}
执行结果：

![Alt text](/images/restTemplate1.jpg)
###2.解决办法
在使用RestTemplate发送post请求的时候，设置编码方式为UTF-8。代码如下：

  	@Test
	public void test9() throws UnsupportedEncodingException{
		    String url = "http://localhost:8080/test";
			JSONObject content = new JSONObject();
			content.put("content","内容内容内容内容内容内容内容");
			String user="用户";
			JSONObject obj = new JSONObject();
			obj.put("user", user);
			obj.put("id", 1111111);
			obj.put("text", content);
			//解决乱码					
			StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));
			RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build();
	        HttpHeaders headers = new HttpHeaders();
	        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
	        headers.setContentType(type);
	        headers.add("Accept", MediaType.APPLICATION_JSON.toString()); 	             	         
	    	HttpEntity<String> formEntity = new HttpEntity<String>(obj.toString(), headers);    	 
			System.out.println(formEntity.getBody());
	    	String result = restTemplate.postForObject(url, formEntity, String.class);
	    	System.out.println(result);
	}
执行结果：<br/>
从执行结果看，中文乱码已经不存在了。
![Alt text](/images/restTemplate2.jpg)
###3.出现乱码的原因
Spring中的RestTemplate其实也是封装了HttpClient，在项目中用RestTemplate的postForObject方法请求接口时出现乱码，既然出现乱码应该就是编码不一致导致的。下面分析一下原因：<br/>
1. 先看RestTemplate的postForObject方法

		@Override
		public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables)
			throws RestClientException {

			RequestCallback requestCallback = httpEntityCallback(request, responseType);
			HttpMessageConverterExtractor<T> responseExtractor =new 
							HttpMessageConverterExtractor<T>(responseType, getMessageConverters(), logger);
			return execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
		}
HttpEntityCallback(request, responseType)：返回一个请求回调实现，它将给定的对象写入请求流。
	

	/**
	 * Returns a request callback implementation that writes the given object to the
	 * request stream.
	 */
	protected <T> RequestCallback httpEntityCallback(Object requestBody, Type responseType) {
		return new HttpEntityRequestCallback(requestBody, responseType);
	}
HttpEntityRequestCallback继承了AcceptHeaderRequestCallback；
AcceptHeaderRequestCallback实现了RequestCallback；RequestCallback接口只有一个方法

	public interface RequestCallback {

	/**
	 * Gets called by {@link RestTemplate#execute} with an opened {@code ClientHttpRequest}.
	 * Does not need to care about closing the request or about handling errors:
	 * this will all be handled by the {@code RestTemplate}.
	 * @param request the active HTTP request
	 * @throws IOException in case of I/O errors
	 */
	void doWithRequest(ClientHttpRequest request) throws IOException;

	}
HttpEntityRequestCallback实现RequestCallback的doWithRequest方法的内容，主要是写入Request 对象流。
这两个类都使用HttpMessageConverter 的实现类来选择具体的Converter canRead canWrite<br/>
2. HttpMessageConverter是什么<br/>
从RestTemplate类的构造方法中可以看出，在初始化RestTemplate的时候就会加载一些HttpMessageConverter
		
	/**
	 * Create a new instance of the {@link RestTemplate} using default settings.
	 * Default {@link HttpMessageConverter}s are initialized.
	 */
	public RestTemplate() {
		this.messageConverters.add(new ByteArrayHttpMessageConverter());
		this.messageConverters.add(new StringHttpMessageConverter());
		this.messageConverters.add(new ResourceHttpMessageConverter());
		this.messageConverters.add(new SourceHttpMessageConverter<Source>());
		this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());

		if (romePresent) {
			this.messageConverters.add(new AtomFeedHttpMessageConverter());
			this.messageConverters.add(new RssChannelHttpMessageConverter());
		}

		if (jackson2XmlPresent) {
			this.messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
		}
		else if (jaxb2Present) {
			this.messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
		}

		if (jackson2Present) {
			this.messageConverters.add(new MappingJackson2HttpMessageConverter());
		}
		else if (gsonPresent) {
			this.messageConverters.add(new GsonHttpMessageConverter());
		}
	}
从构造方法中可以看到加载了好几个类型的HttpMessageConverter。出现乱码的请求出现在中文中，中文又是一个String类型的，从上面HttpMessageConverter类名上，可以看出StringHttpMessageConverter是来处理中文的；出现乱码的情况肯定是编码出现了不一致，看一下StringHttpMessageConverter的编码是什么。<br/>
从下面的源码可以看出StringHttpMessageConverter的默认编码是ISO-8859-1

	public class StringHttpMessageConverter extends AbstractHttpMessageConverter<String> {

		public static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");
		private final List<Charset> availableCharsets;
		private boolean writeAcceptCharset = true;

		/**
		 * A default constructor that uses {@code "ISO-8859-1"} as the default charset.
		 * @see #StringHttpMessageConverter(Charset)
	 	*/
		public StringHttpMessageConverter() {
			this(DEFAULT_CHARSET);
		}
		……
	}
 