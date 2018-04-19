package com.microservice.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microservice.pojo.TypeHierarchyNode;
import com.microservice.result.ResultVO;
import com.microservice.service.render.RenderService;
import com.microservice.vo.BratConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cjl on 2018/3/29.
 */
@RestController
@RequestMapping(value = "/microServiceRender")
public class RenderController {

    @Autowired
    private RenderService renderService;

    @RequestMapping(value = "/getBratConfig.do")
    public ResultVO<BratConfigVO> getBratConfig(){
        BratConfigVO bratConfigVO=new BratConfigVO();
        JSONObject uiNames=new JSONObject();
        uiNames.put("entities","entities");
        uiNames.put("events","events");
        uiNames.put("relations","relations");
        uiNames.put("attributes","attributes");
        bratConfigVO.setUi_names(uiNames);
        bratConfigVO.setRelation_attribute_types(new JSONArray());
        List<TypeHierarchyNode> typeHierarchyNodeList =renderService.getDrawingSection();
        JSONArray array=renderService.fillTypeConfiguration(typeHierarchyNodeList);
        bratConfigVO.setEntity_types(array);
        return  ResultVO.success(bratConfigVO);
    }
}
