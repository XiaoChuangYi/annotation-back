package com.microservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.microservice.dataAccessLayer.entity.AnnotationParallel;
import com.microservice.dataAccessLayer.entity.UserAccount;
import com.microservice.enums.AnnotationParallelStateEnum;
import com.microservice.result.AnnotationParallelBratVO;
import com.microservice.result.PageVO;
import com.microservice.result.ResultVO;
import com.microservice.service.AnnotationParallelService;
import com.microservice.utils.AnnotationParallelConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * Created by cjl on 2018/4/16.
 */
@RestController
@RequestMapping(value = "/annotationAppose")
public class AnnotationParallelController extends BaseController{

    @Autowired
    private AnnotationParallelService annotationParallelService;



    /**
     * 查询句子标注，分页，条件
     */
    @RequestMapping(value = "/queryAnnotationSentence.do")
    public ResultVO<PageVO<AnnotationParallelBratVO>> queryAnnotationParallel(@RequestBody JSONObject jsonParam, @ModelAttribute("userAccount") UserAccount userAccount){
        int pageIndex=jsonParam.getIntValue("pageIndex");
        int pageSize=jsonParam.getIntValue("pageSize");
        List<String> stateList= JSON.parseArray(JSON.toJSONString(jsonParam.get("states")),String.class);
//        String userModifier=jsonParam.getString("userModifier");

        Page<AnnotationParallel> pageInfo= annotationParallelService.queryAnnotationByCondition(pageIndex,pageSize,stateList,Integer.toString(userAccount.getId()));
        List<AnnotationParallelBratVO> annotationParallelBratVOList = AnnotationParallelConvert.convert2AnnotationBratVOList(pageInfo.getResult());
        PageVO<AnnotationParallelBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(annotationParallelBratVOList);
        return ResultVO.success(pageVO);
    }


    /**
     * 查询句子标注，分页，条件
     */
    @RequestMapping(value = "/queryAnnotationSentence2Distribution.do")
    public ResultVO<PageVO<AnnotationParallelBratVO>> queryAnnotationParalle2Distribution(@RequestBody JSONObject jsonParam){
        int pageIndex=jsonParam.getIntValue("pageIndex");
        int pageSize=jsonParam.getIntValue("pageSize");

        List<String> stateList= JSON.parseArray(JSON.toJSONString(jsonParam.get("states")),String.class);

        String userModifier=jsonParam.getString("userModifier");
        Page<AnnotationParallel> pageInfo= annotationParallelService.queryAnnotationByCondition(pageIndex,pageSize,stateList,userModifier);
        List<AnnotationParallelBratVO> annotationParallelBratVOList = AnnotationParallelConvert.convert2AnnotationBratVOList(pageInfo.getResult());
        PageVO<AnnotationParallelBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(annotationParallelBratVOList);
        return ResultVO.success(pageVO);
    }

    /**
     * 新增标注
     */
    @RequestMapping(value = "/addAnnotationSentence.do")
    public ResultVO<AnnotationParallelBratVO> addAnnotationParallel(@RequestBody JSONObject jsonParam, @ModelAttribute("userAccount") UserAccount userAccount){
        String type=jsonParam.getString("type");
        String term=jsonParam.getString("term");
        int anId=jsonParam.getIntValue("id");
        int startPosition=jsonParam.getIntValue("startPosition");
        int endPosition=jsonParam.getIntValue("endPosition");

        AnnotationParallel annotationParallel=annotationParallelService.getAnnotationParallel(anId);
        if(!annotationParallel.getUserModifier().equals(Integer.toString(userAccount.getId()))){
            return ResultVO.error("当前用户无法操作ID为'"+anId+"'的记录");
        }
        String newAnnotationParalle= AnnotationParallelConvert.addUnitAnnotationByLambda(annotationParallel.getAnnotationText(),
                type,startPosition,endPosition,term);

        AnnotationParallelBratVO finalAnnotationParallelBratVO=updateAnnotationParallelAnnotationText(anId,newAnnotationParalle);
        return ResultVO.success(finalAnnotationParallelBratVO);
    }

    /**
     * 删除标注
     */
    @RequestMapping(value = "/deleteAnnotationSentence.do")
    public ResultVO<AnnotationParallelBratVO> deleteAnnotationParallel(@RequestBody JSONObject jsonParam,@ModelAttribute("userAccount") UserAccount userAccount){
        int anId=jsonParam.getIntValue("id");
        String tag=jsonParam.getString("tag");

        //判断当前用户是否可以操作该条标注数据
        AnnotationParallel annotationParallel=annotationParallelService.getAnnotationParallel(anId);
        if(!annotationParallel.getUserModifier().equals(Integer.toString(userAccount.getId())))
            return ResultVO.error("当前用户无权操作该"+anId+"记录");

        String newAnnotationParalle= AnnotationParallelConvert.deleteUnitAnnotationByLambda(annotationParallel.getAnnotationText(),tag);
        //更新单条标注信息到数据库
        AnnotationParallelBratVO finalAnnotationParallelBratVO=updateAnnotationParallelAnnotationText(anId,newAnnotationParalle);
        return ResultVO.success(finalAnnotationParallelBratVO);
    }


    /**
     * 更新annotation_sentence表的annotation_text字段
     */
    private AnnotationParallelBratVO updateAnnotationParallelAnnotationText(int anId,String newAnnotationText){
        AnnotationParallel paramAnnotationParalle=new AnnotationParallel();
        paramAnnotationParalle.setId(anId);
        paramAnnotationParalle.setAnnotationText(newAnnotationText);

        paramAnnotationParalle.setGmtModified(new Date());
        paramAnnotationParalle.setState(AnnotationParallelStateEnum.ANNOTATIONING.getMessage());
        annotationParallelService.updateAnnotationParallel(paramAnnotationParalle);
        AnnotationParallel annotationParallel = annotationParallelService.getAnnotationParallel(anId);
        return AnnotationParallelConvert.convert2AnnotationBratVO(annotationParallel);
    }
    /**
     * 提交标注
     */
    @RequestMapping(value = "/commitAnnotationSentence.do")
    public ResultVO commitAnnotationParallel(@RequestBody JSONObject jsonParam){
        int anId=jsonParam.getIntValue("id");
        AnnotationParallel annotationParalle=new AnnotationParallel();
        annotationParalle.setId(anId);
        annotationParalle.setState(AnnotationParallelStateEnum.FINISH.getMessage());
        annotationParalle.setGmtModified(new Date());
        annotationParallelService.updateAnnotationParallel(annotationParalle);
        return ResultVO.success();
    }

    /**
     * 审核标注
     */
    @RequestMapping(value = "/finishAnnotationSentence.do")
    public ResultVO finishAnnotationParallel(@RequestBody JSONObject jsonParam){
        List<Integer> idArr= JSON.parseArray(JSON.toJSONString(jsonParam.get("idArr")),Integer.class);
        annotationParallelService.batchExamineAnnotationParallel(idArr);
        return ResultVO.success();
    }

    /**
     * 分配标注
     */
    @RequestMapping(value = "/distributionAnnotationSentence.do")
    public ResultVO distributionAnnotationParallel(@RequestBody JSONObject jsonParam){
        List<Integer> idArr= JSON.parseArray(JSON.toJSONString(jsonParam.get("idArr")),Integer.class);
        int userModifier=jsonParam.getIntValue("userModifier");
        annotationParallelService.designateAnnotationParallel(idArr,userModifier);
        return ResultVO.success();
    }
}
