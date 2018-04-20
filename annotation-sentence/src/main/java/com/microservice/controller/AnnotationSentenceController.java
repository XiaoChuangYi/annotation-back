package com.microservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.microservice.dataAccessLayer.entity.AnnotationSentence;
import com.microservice.dataAccessLayer.entity.UserAccount;
import com.microservice.enums.AnnotationSentenceStateEnum;
import com.microservice.result.AnnotationSentenceBratVO;
import com.microservice.result.PageVO;
import com.microservice.result.ResultVO;
import com.microservice.service.AnnotationSentenceService;
import com.microservice.utils.AnnotationConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cjl on 2018/4/16.
 */
@RestController
@RequestMapping(value = "/annotationSentence")
public class AnnotationSentenceController extends BaseController{

    @Autowired
    private AnnotationSentenceService annotationSentenceService;



    /**
     * 查询句子标注，分页，条件
     */
    @RequestMapping(value = "/queryAnnotationSentence.do")
    public ResultVO<PageVO<AnnotationSentenceBratVO>> queryAnnotationSentence(@RequestBody JSONObject jsonParam, @ModelAttribute("userAccount") UserAccount userAccount){
        int pageIndex=jsonParam.getIntValue("pageIndex");
        int pageSize=jsonParam.getIntValue("pageSize");
        List<String> stateList= JSON.parseArray(JSON.toJSONString(jsonParam.get("states")),String.class);
//        String userModifier=jsonParam.getString("userModifier");

        Page<AnnotationSentence> pageInfo= annotationSentenceService.queryAnnotationByCondition(pageIndex,pageSize,stateList,Integer.toString(userAccount.getId()));
        //如果根据当前用户的信息没有查找到被分配的标注，则自动给他分配其它的标注
        if(pageInfo.getResult().size()==0){
            stateList=new ArrayList<>();
            stateList.add(AnnotationSentenceStateEnum.UN_DISTRIBUTION.getMessage());
            pageInfo=annotationSentenceService.queryAnnotationUnDistribution(pageIndex,pageSize,stateList);
            if(pageInfo.getResult().size()>0){
                //将当前未分配的标注授权给当前的用户
                AnnotationSentence autoDistributionAnnotationSentence=pageInfo.get(0);
                autoDistributionAnnotationSentence.setState(AnnotationSentenceStateEnum.DISTRIBUTIONED.getMessage());
                autoDistributionAnnotationSentence.setUserModifier(Integer.toString(userAccount.getId()));
                annotationSentenceService.updateAnnotationSentence(autoDistributionAnnotationSentence);
            }
        }
        List<AnnotationSentenceBratVO> annotationSentenceBratVOList =AnnotationConvert.convert2AnnotationBratVOList(pageInfo.getResult());
        PageVO<AnnotationSentenceBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(annotationSentenceBratVOList);
        return ResultVO.success(pageVO);
    }


    /**
     * 查询句子标注，分页，条件
     */
    @RequestMapping(value = "/queryAnnotationSentence2Distribution.do")
    public ResultVO<PageVO<AnnotationSentenceBratVO>> queryAnnotationSentence2Distribution(@RequestBody JSONObject jsonParam){
        int pageIndex=jsonParam.getIntValue("pageIndex");
        int pageSize=jsonParam.getIntValue("pageSize");

        List<String> stateList= JSON.parseArray(JSON.toJSONString(jsonParam.get("states")),String.class);

        String userModifier=jsonParam.getString("userModifier");
        Page<AnnotationSentence> pageInfo= annotationSentenceService.queryAnnotationByCondition(pageIndex,pageSize,stateList,userModifier);
        List<AnnotationSentenceBratVO> annotationSentenceBratVOList =AnnotationConvert.convert2AnnotationBratVOList(pageInfo.getResult());
        PageVO<AnnotationSentenceBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(annotationSentenceBratVOList);
        return ResultVO.success(pageVO);
    }

    /**
     * 新增标注
     */
    @RequestMapping(value = "/addAnnotationSentence.do")
    public ResultVO<AnnotationSentenceBratVO> addAnnotationSentence(@RequestBody JSONObject jsonParam, @ModelAttribute("userAccount") UserAccount userAccount){
        String type=jsonParam.getString("type");
        String term=jsonParam.getString("term");
        int anId=jsonParam.getIntValue("id");
        int startPosition=jsonParam.getIntValue("startPosition");
        int endPosition=jsonParam.getIntValue("endPosition");

        AnnotationSentence annotationSentence=annotationSentenceService.getAnnotationSentence(anId);
        if(!annotationSentence.getUserModifier().equals(Integer.toString(userAccount.getId()))){
            return ResultVO.error("当前用户无法操作ID为'"+anId+"'的记录");
        }
        String newAnnotationSentence=AnnotationConvert.addUnitAnnotationByLambda(annotationSentence.getAnnotationText(),
                type,startPosition,endPosition,term);

        AnnotationSentenceBratVO finalAnnotationSentenceBratVO=updateAnnotationSentenceAnnotationText(anId,newAnnotationSentence);
        return ResultVO.success(finalAnnotationSentenceBratVO);
    }

    /**
     * 删除标注
     */
    @RequestMapping(value = "/deleteAnnotationSentence.do")
    public ResultVO<AnnotationSentenceBratVO> deleteAnnotationSentence(@RequestBody JSONObject jsonParam,@ModelAttribute("userAccount") UserAccount userAccount){
        int anId=jsonParam.getIntValue("id");
        String tag=jsonParam.getString("tag");

        //判断当前用户是否可以操作该条标注数据
        AnnotationSentence annotationSentence=annotationSentenceService.getAnnotationSentence(anId);
        if(!annotationSentence.getUserModifier().equals(Integer.toString(userAccount.getId())))
            return ResultVO.error("当前用户无权操作该"+anId+"记录");

        String newAnnotationSentence=AnnotationConvert.deleteUnitAnnotationByLambda(annotationSentence.getAnnotationText(),tag);
        //更新单条标注信息到数据库
        AnnotationSentenceBratVO finalAnnotationSentenceBratVO=updateAnnotationSentenceAnnotationText(anId,newAnnotationSentence);
        return ResultVO.success(finalAnnotationSentenceBratVO);
    }


    /**
     * 更新annotation_sentence表的annotation_text字段
     */
    private AnnotationSentenceBratVO updateAnnotationSentenceAnnotationText(int anId,String newAnnotationText){
        AnnotationSentence paramAnnotationSentence=new AnnotationSentence();
        paramAnnotationSentence.setId(anId);
        paramAnnotationSentence.setAnnotationText(newAnnotationText);

        paramAnnotationSentence.setGmtModified(new Date());
        paramAnnotationSentence.setState(AnnotationSentenceStateEnum.ANNOTATIONING.getMessage());
        annotationSentenceService.updateAnnotationSentence(paramAnnotationSentence);
        AnnotationSentence annotationSentence = annotationSentenceService.getAnnotationSentence(anId);
        return AnnotationConvert.convert2AnnotationBratVO(annotationSentence);
    }
    /**
     * 提交标注
     */
    @RequestMapping(value = "/commitAnnotationSentence.do")
    public ResultVO commitAnnotationSentence(@RequestBody JSONObject jsonParam){
        int anId=jsonParam.getIntValue("id");
        AnnotationSentence annotationSentence=new AnnotationSentence();
        annotationSentence.setId(anId);
        annotationSentence.setState(AnnotationSentenceStateEnum.FINISH.getMessage());
        annotationSentence.setGmtModified(new Date());
        annotationSentenceService.updateAnnotationSentence(annotationSentence);
        return ResultVO.success();
    }

    /**
     * 审核标注
     */
    @RequestMapping(value = "/finishAnnotationSentence.do")
    public ResultVO finishAnnotationSentence(@RequestBody JSONObject jsonParam){
        List<Integer> idArr= JSON.parseArray(JSON.toJSONString(jsonParam.get("idArr")),Integer.class);
        annotationSentenceService.batchExamineAnnotationSentence(idArr);
        return ResultVO.success();
    }

    /**
     * 分配标注
     */
    @RequestMapping(value = "/distributionAnnotationSentence.do")
    public ResultVO distributionAnnotationSentence(@RequestBody JSONObject jsonParam){
        List<Integer> idArr= JSON.parseArray(JSON.toJSONString(jsonParam.get("idArr")),Integer.class);
        int userModifier=jsonParam.getIntValue("userModifier");
        annotationSentenceService.designateAnnotationSentence(idArr,userModifier);
        return ResultVO.success();
    }
}
