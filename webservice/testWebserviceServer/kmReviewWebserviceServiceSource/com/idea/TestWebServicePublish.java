package com.idea;

import javax.xml.ws.Endpoint;

public class TestWebServicePublish {
	public static void main(String[] args){
		String address="http://localhost:8099/Webservice/helloUser";
		Endpoint.publish(address, new TestWebServiceImpl());
		System.out.println("���� helloUser webservice �ɹ���");
	}
}
