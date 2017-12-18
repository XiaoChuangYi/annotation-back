package cn.malgo.annotation.web.controller.bratconfig;

import cn.malgo.annotation.common.dal.model.result.TypeHierarchyNode;
import cn.malgo.annotation.core.service.config.VisualService;
import cn.malgo.annotation.web.controller.bratconfig.result.BratConfigVO;
import cn.malgo.annotation.web.result.ResultVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
}
