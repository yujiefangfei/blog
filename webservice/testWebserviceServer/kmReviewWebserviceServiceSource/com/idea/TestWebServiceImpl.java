package com.idea;

@javax.jws.WebService
public class TestWebServiceImpl implements TestWebService{

	@Override
	public String helloUser(User user) {
		return "name:"+user.getName()
				+" ,sex"+user.getSex();
	}
}
