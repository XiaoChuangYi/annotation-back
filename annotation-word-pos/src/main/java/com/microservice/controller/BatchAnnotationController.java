package com.microservice.controller;


import com.microservice.result.ResultVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by cjl on 2018/4/25.
 */
@RestController
@RequestMapping(value = "/annotation_word_pos")
public class BatchAnnotationController {


    @RequestMapping(value = "/batchInitAnnotation.do")
    public ResultVO batchInitAnnotation(){

        return ResultVO.success();
    }
}
