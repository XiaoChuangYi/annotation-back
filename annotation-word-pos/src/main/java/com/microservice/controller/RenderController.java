package com.microservice.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.microservice.dataAccessLayer.entity.BratDraw;
import com.microservice.dataAccessLayer.entity.Draw;
import com.microservice.pojo.TypeHierarchyNode;
import com.microservice.result.PageVO;
import com.microservice.result.ResultVO;
import com.microservice.service.RenderService;
import com.microservice.vo.BratConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cjl on 2018/3/29.
 */
@RestController
@RequestMapping(value = "/render")
public class RenderController extends BaseController{

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
        List<TypeHierarchyNode> typeHierarchyNodeList =renderService.getDrawingSection(1);
        JSONArray array=renderService.fillTypeConfiguration(typeHierarchyNodeList,1);
        bratConfigVO.setEntity_types(array);
        return  ResultVO.success(bratConfigVO);
    }

    /**
     *分页查询DRAW表
     */
    @RequestMapping(value = "/queryPaginationDraw.do")
    public ResultVO<PageVO<Draw>> queryPaginationDraw(@RequestBody JSONObject jsonParam){
        int pageIndex=jsonParam.getIntValue("pageIndex");
        int pageSize=jsonParam.getIntValue("pageSize");
        String type=jsonParam.getString("type");
        String color=jsonParam.getString("color");
        Page<BratDraw> page=renderService.listDrawByCondition(pageIndex,pageSize,color,type,currentTaskId);
        PageVO<BratDraw> pageVO = new PageVO(page);
        return ResultVO.success(pageVO);
    }
    /**
     * 更据id更新draw表的渲染参数drawName
     */
    @RequestMapping(value = "/updateDrawArgumentsById.do")
    public ResultVO updateDrawArgumentsById(@RequestBody JSONObject jsonParam){
        int id=jsonParam.getIntValue("id");
        String drawName=jsonParam.getString("drawName");
        renderService.updateDrawById(id,drawName,"");
        return  ResultVO.success();
    }
    /**
     * 新增draw
     */
    @RequestMapping(value="/addDrawArguments.do")
    public ResultVO addDrawArguments(@RequestBody JSONObject jsonParam){
        int id=jsonParam.getIntValue("id");
        String typeLabel=jsonParam.getString("typeLabel");
        String drawName=jsonParam.getString("drawName");
        renderService.addDraw(id,drawName,typeLabel,currentTaskId);
        return  ResultVO.success();
    }
    /**
     * 更据id更新draw表的渲染参数typeLabel
     */
    @RequestMapping(value = "/updateTypeLabelById.do")
    public ResultVO updateTypeLabelById(@RequestBody JSONObject jsonParam){
        int id=jsonParam.getIntValue("id");
        String typeLabel=jsonParam.getString("typeLabel");
        renderService.updateDrawById(id,"",typeLabel);
        return  ResultVO.success();
    }
}
