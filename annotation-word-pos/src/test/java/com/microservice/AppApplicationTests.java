package com.microservice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microservice.dataAccessLayer.entity.AnAtomicTerm;
import com.microservice.service.atomicterm.AnAtomicTermService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppApplicationTests {


	@Autowired
	private AnAtomicTermService anAtomicTermService;

	@Test
	@Ignore
	public void contextLoads() {
		List<AnAtomicTerm> anAtomicTermList= anAtomicTermService.listAnAtomicTermByPagingCondition("","","",1,10,"");
		System.out.println(">>>>>>>>>>>>>>>>>>anAtomicTermList："+ JSON.toJSONString(anAtomicTermList));
	}

	@Test
	public void test() {
		System.out.println(">>>>>>>>>>>>>>>>>>anAtomicTermList：");
	}

}
