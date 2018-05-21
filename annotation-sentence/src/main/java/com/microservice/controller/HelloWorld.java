package com.microservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.microservice.result.ResultVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by cjl on 2018/4/23.
 */
@RestController
@RequestMapping("/")
public class HelloWorld {

    @RequestMapping("/hello-world")
    public String helloWorld(){
        return "Hello World!";
    }


    @RequestMapping(value = "/test-exception")
    public ResultVO testException(@RequestBody JSONObject jsonParam){
        String test=jsonParam.getString("test");
        System.out.println(">>>test:"+test);
        return ResultVO.success();
    }

}
