package com.idea.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.idea.quarzDemo.TestScheduler;
import static org.quartz.JobKey.jobKey;


@RestController
public class QuartzController {
	
	@Autowired
	private TestScheduler testScheduler;
	
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public String createTransGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			testScheduler.testCronScheduler();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}
	
	@RequestMapping(value = "/pause", method = RequestMethod.GET)
	public String pause(HttpServletRequest request, HttpServletResponse response) {
		try {
			testScheduler.pause(jobKey("name","group"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "success";
	}
	
	@RequestMapping(value = "/resume", method = RequestMethod.GET)
	public String resume(HttpServletRequest request, HttpServletResponse response) {
		try {
			testScheduler.resume(jobKey("name","group"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return "success";
	}
}
