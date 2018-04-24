package com.microservice.controller;

import com.microservice.dataAccessLayer.entity.Type;
import com.microservice.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cjl on 2018/4/10.
 */
@RestController
@RequestMapping(value = "/microServiceType")
public class TypeController {


    @Autowired
    private TypeService typeService;



    /**
     * 查询所有状态为'ENABLE'的type类型
     */
    @RequestMapping(value = "/listType.do")
    public List<Type> listType(int taskId){
        return typeService.listEnableType(taskId);
    }

    /**
     * 条件，分页查询type
     */
//    @RequestMapping(value = "/getPaginationType.do")
//    public ResultVO<PageVO<Type>> getAllType(){
//        Page<AnType> page =typeService.listEnableTypeByPagingCondition(request.getPageNum(),request.getPageSize(),request.getTypeCode(),request.getTypeName());
//        PageVO<AnType> pageVO = new PageVO(page);
//        return ResultVO.success(pageVO);
//    }
}
