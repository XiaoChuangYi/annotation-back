package cn.malgo.annotation.web.controller.bratconfig;

import cn.malgo.annotation.common.dal.model.Draw;
import cn.malgo.annotation.common.dal.model.result.TypeHierarchyNode;
import cn.malgo.annotation.core.service.config.VisualService;
import cn.malgo.annotation.web.controller.bratconfig.request.QueryDrawRequest;
import cn.malgo.annotation.web.controller.bratconfig.result.BratConfigVO;
import cn.malgo.annotation.web.result.PageVO;
import cn.malgo.annotation.web.result.ResultVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by cjl on 2017/12/13.
 */
@RestController
@RequestMapping(value = "/config")
public class BratConfigController {

    @Autowired
    private VisualService visualService;

    /**
     * 从数据库中获取渲染配置
     */
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
        List<TypeHierarchyNode> typeHierarchyNodeList =visualService.getDrawingSection();
        JSONArray array=visualService.fillTypeConfiguration(typeHierarchyNodeList);
        bratConfigVO.setEntity_types(array);
        return  ResultVO.success(bratConfigVO);
    }
    /**
     *分页查询DRAW表
     */
    @RequestMapping(value = "/queryPaginationDraw.do")
    public ResultVO<PageVO<Draw>> queryPaginationDraw(QueryDrawRequest request){
        Page<Draw> page=visualService.queryOnePageDraw(request.getType(),request.getColor(),request.getPageNum(),request.getPageSize());
        PageVO<Draw> pageVO = new PageVO(page);
        return ResultVO.success(pageVO);
    }
    /**
     * 更据id更新draw表的渲染参数drawName
     */
    @RequestMapping(value = "/updateDrawArgumentsById.do")
    public ResultVO updateDrawArgumentsById(int id,String drawName){
        visualService.updateDrawById(id,drawName,"");
        return  ResultVO.success();
    }
    /**
     * 新增draw
     */
    @RequestMapping(value="/addDrawArguments.do")
    public ResultVO addDrawArguments(int id,String drawName,String typeLabel){
        visualService.addDraw(id,drawName,typeLabel);
        return  ResultVO.success();
    }
    /**
     * 更据id更新draw表的渲染参数typeLabel
     */
    @RequestMapping(value = "/updateTypeLabelById.do")
    public ResultVO updateTypeLabelById(int id,String typeLabel){
        visualService.updateDrawById(id,"",typeLabel);
        return  ResultVO.success();
    }
}
