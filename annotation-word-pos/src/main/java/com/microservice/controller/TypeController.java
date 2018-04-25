package com.microservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.microservice.dataAccessLayer.entity.Type;
import com.microservice.result.PageVO;
import com.microservice.result.ResultVO;
import com.microservice.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cjl on 2018/4/19.
 */

@RestController
@RequestMapping(value = "/type")
public class TypeController extends BaseController{

    @Autowired
    private TypeService typeService;

    /**
     * 查询所有状态为'ENABLE'的type类型
     */
    @RequestMapping(value = "/listType.do")
    public List<Type> listType(){
        return typeService.listEnableType(currentTaskId);
    }

    /**
     * 条件，分页查询type
     */
    @RequestMapping(value = "/queryTypeByCondition.do")
    public ResultVO<PageVO<Type>> getAllType(@RequestBody JSONObject jsonParam){
        int pageIndex=jsonParam.getIntValue("pageIndex");
        int pageSize=jsonParam.getIntValue("pageSize");
        String typeCode=jsonParam.getString("typeCode");
        String typeName=jsonParam.getString("typeName");
        Page<Type> page =typeService.listEnableTypeByPagingCondition(pageIndex,pageSize,typeCode,typeName,currentTaskId);
        PageVO<Type> pageVO = new PageVO(page);
        return ResultVO.success(pageVO);
    }

    /**
     * 根据typeId获取其对应的子节点
     */
    @RequestMapping(value = "/queryChildrenTypeByTypeId.do")
    public ResultVO<List<Type>> queryChildrenTypeByTypeId(@RequestBody JSONObject jsonParam){
        String typeId=jsonParam.getString("typeId");
        List<Type> childrenTypeList=typeService.listChildrenTypeByParentId(typeId,currentTaskId);
        return ResultVO.success(childrenTypeList);
    }


    /**
     * 新增type
     */
    @RequestMapping(value = "/addType.do")
    public ResultVO addType(@RequestBody JSONObject jsonParam){
        String typeCode=jsonParam.getString("typeCode");
        String typeName=jsonParam.getString("typeName");
        String parentId=jsonParam.getString("parentId");
        typeService.saveType(parentId,typeName,typeCode,currentTaskId);
        return ResultVO.success();
    }
    /**
     * 更新type
     */
    @RequestMapping(value = "/updateType.do")
    public ResultVO updateType(@RequestBody JSONObject jsonParam){
        String newParentId=jsonParam.getString("newParentId");
        String typeId=jsonParam.getString("typeId");
        String typeName=jsonParam.getString("typeName");
        String oldParentId=jsonParam.getString("oldParentId");
        typeService.updateType(newParentId,typeId,typeName,oldParentId,currentTaskId);
        return ResultVO.success();
    }
    /**
     * 删除type
     */
    @RequestMapping(value = "/deleteType.do")
    public  ResultVO deleteType(@RequestBody JSONObject jsonParam){
        String typeId=jsonParam.getString("typeId");
        typeService.removeType(typeId,currentTaskId);
        return ResultVO.success();
    }


}
