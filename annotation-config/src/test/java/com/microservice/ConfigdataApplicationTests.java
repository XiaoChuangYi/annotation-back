package com.microservice;

import com.microservice.service.TermService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigdataApplicationTests {

	@Autowired
	private TermService termService;

	@Test
	public void contextLoads() {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>count："+termService.countGroupsByOriginName());
	}

}
