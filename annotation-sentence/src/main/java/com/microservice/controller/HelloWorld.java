package com.microservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by cjl on 2018/4/23.
 */
@RestController
@RequestMapping("/")
public class HelloWorld {
    @RequestMapping("/hello-world")
    public String hellowolrd(){
        return "Hello World!";
    }
}
