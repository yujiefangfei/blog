package com.idea;

import javax.jws.WebMethod;

@javax.jws.WebService
public interface TestWebService {
	@WebMethod
	String helloUser(User user);
}
