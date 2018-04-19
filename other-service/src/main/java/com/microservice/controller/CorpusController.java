package com.microservice.controller;

import com.microservice.service.CorpusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by cjl on 2018/4/12.
 */
@RestController
@RequestMapping(value = "/corpus")
public class CorpusController {

    @Autowired
    private CorpusService corpusService;

//    @RequestMapping(value = "/")
}
