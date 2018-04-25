package com.microservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Created by cjl on 2018/4/11.
 */
@SessionAttributes("userAccount")
public class BaseController {

    @Value("${current.task.name}")
    protected String currentTaskName;

    @Value("${current.task.id}")
    protected int currentTaskId;
}
