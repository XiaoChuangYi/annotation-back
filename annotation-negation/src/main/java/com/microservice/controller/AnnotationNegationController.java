package com.microservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.microservice.dataAccessLayer.entity.AnnotationNegation;
import com.microservice.dataAccessLayer.entity.UserAccount;
import com.microservice.enums.AnnotationNegationStateEnum;
import com.microservice.result.AnnotationNegationBratVO;
import com.microservice.result.PageVO;
import com.microservice.result.ResultVO;
import com.microservice.service.AnnotationNegationService;
import com.microservice.utils.AnnotationNegationConvert;
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
@RequestMapping(value = "/annotationNegation")
public class AnnotationNegationController extends BaseController{

    @Autowired
    private AnnotationNegationService annotationNegationService;



    /**
     * 查询句子标注，分页，条件
     */
    @RequestMapping(value = "/queryAnnotationSentence.do")
    public ResultVO<PageVO<AnnotationNegationBratVO>> queryAnnotationNegation(@RequestBody JSONObject jsonParam, @ModelAttribute("userAccount") UserAccount userAccount){
        int pageIndex=jsonParam.getIntValue("pageIndex");
        int pageSize=jsonParam.getIntValue("pageSize");
        List<String> stateList= JSON.parseArray(JSON.toJSONString(jsonParam.get("states")),String.class);
//        String userModifier=jsonParam.getString("userModifier");

        Page<AnnotationNegation> pageInfo= annotationNegationService.queryAnnotationByCondition(pageIndex,pageSize,stateList,Integer.toString(userAccount.getId()));
        List<AnnotationNegationBratVO> annotationNegationBratVOList = AnnotationNegationConvert.convert2AnnotationBratVOList(pageInfo.getResult());
        PageVO<AnnotationNegationBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(annotationNegationBratVOList);
        return ResultVO.success(pageVO);
    }


    /**
     * 查询句子标注，分页，条件
     */
    @RequestMapping(value = "/queryAnnotationSentence2Distribution.do")
    public ResultVO<PageVO<AnnotationNegationBratVO>> queryAnnotationNegation2Distribution(@RequestBody JSONObject jsonParam){
        int pageIndex=jsonParam.getIntValue("pageIndex");
        int pageSize=jsonParam.getIntValue("pageSize");

        List<String> stateList= JSON.parseArray(JSON.toJSONString(jsonParam.get("states")),String.class);

        String userModifier=jsonParam.getString("userModifier");
        Page<AnnotationNegation> pageInfo= annotationNegationService.queryAnnotationByCondition(pageIndex,pageSize,stateList,userModifier);
        List<AnnotationNegationBratVO> annotationNegationBratVOList = AnnotationNegationConvert.convert2AnnotationBratVOList(pageInfo.getResult());
        PageVO<AnnotationNegationBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(annotationNegationBratVOList);
        return ResultVO.success(pageVO);
    }

    /**
     * 新增标注
     */
    @RequestMapping(value = "/addAnnotationSentence.do")
    public ResultVO<AnnotationNegationBratVO> addAnnotationNegation(@RequestBody JSONObject jsonParam, @ModelAttribute("userAccount") UserAccount userAccount){
        String type=jsonParam.getString("type");
        String term=jsonParam.getString("term");
        int anId=jsonParam.getIntValue("id");
        int startPosition=jsonParam.getIntValue("startPosition");
        int endPosition=jsonParam.getIntValue("endPosition");

        AnnotationNegation annotationNegation=annotationNegationService.getAnnotationNegation(anId);
        if(!annotationNegation.getUserModifier().equals(Integer.toString(userAccount.getId()))){
            return ResultVO.error("当前用户无法操作ID为'"+anId+"'的记录");
        }
        String newAnnotationNegation= AnnotationNegationConvert.addUnitAnnotationByLambda(annotationNegation.getAnnotationText(),
                type,startPosition,endPosition,term);

        AnnotationNegationBratVO finalAnnotationNegationBratVO=updateAnnotationNegationAnnotationText(anId,newAnnotationNegation);
        return ResultVO.success(finalAnnotationNegationBratVO);
    }

    /**
     * 删除标注
     */
    @RequestMapping(value = "/deleteAnnotationSentence.do")
    public ResultVO<AnnotationNegationBratVO> deleteAnnotationNegation(@RequestBody JSONObject jsonParam, @ModelAttribute("userAccount") UserAccount userAccount){
        int anId=jsonParam.getIntValue("id");
        String tag=jsonParam.getString("tag");

        //判断当前用户是否可以操作该条标注数据
        AnnotationNegation annotationNegation=annotationNegationService.getAnnotationNegation(anId);
        if(!annotationNegation.getUserModifier().equals(Integer.toString(userAccount.getId())))
            return ResultVO.error("当前用户无权操作该"+anId+"记录");

        String newAnnotationNegation= AnnotationNegationConvert.deleteUnitAnnotationByLambda(annotationNegation.getAnnotationText(),tag);
        //更新单条标注信息到数据库
        AnnotationNegationBratVO finalAnnotationNegationBratVO=updateAnnotationNegationAnnotationText(anId,newAnnotationNegation);
        return ResultVO.success(finalAnnotationNegationBratVO);
    }


    /**
     * 更新annotation_sentence表的annotation_text字段
     */
    private AnnotationNegationBratVO updateAnnotationNegationAnnotationText(int anId, String newAnnotationText){
        AnnotationNegation paramAnnotationNegation=new AnnotationNegation();
        paramAnnotationNegation.setId(anId);
        paramAnnotationNegation.setAnnotationText(newAnnotationText);

        paramAnnotationNegation.setGmtModified(new Date());
        paramAnnotationNegation.setState(AnnotationNegationStateEnum.ANNOTATIONING.getMessage());
        annotationNegationService.updateAnnotationNegation(paramAnnotationNegation);
        AnnotationNegation annotationNegation = annotationNegationService.getAnnotationNegation(anId);
        return AnnotationNegationConvert.convert2AnnotationBratVO(annotationNegation);
    }
    /**
     * 提交标注
     */
    @RequestMapping(value = "/commitAnnotationSentence.do")
    public ResultVO commitAnnotationNegation(@RequestBody JSONObject jsonParam){
        int anId=jsonParam.getIntValue("id");
        AnnotationNegation annotationNegation=new AnnotationNegation();
        annotationNegation.setId(anId);
        annotationNegation.setState(AnnotationNegationStateEnum.FINISH.getMessage());
        annotationNegation.setGmtModified(new Date());
        annotationNegationService.updateAnnotationNegation(annotationNegation);
        return ResultVO.success();
    }

    /**
     * 审核标注
     */
    @RequestMapping(value = "/finishAnnotationSentence.do")
    public ResultVO finishAnnotationNegation(@RequestBody JSONObject jsonParam){
        List<Integer> idArr= JSON.parseArray(JSON.toJSONString(jsonParam.get("idArr")),Integer.class);
        annotationNegationService.batchExamineAnnotationNegation(idArr);
        return ResultVO.success();
    }

    /**
     * 分配标注
     */
    @RequestMapping(value = "/distributionAnnotationSentence.do")
    public ResultVO distributionAnnotationNegation(@RequestBody JSONObject jsonParam){
        List<Integer> idArr= JSON.parseArray(JSON.toJSONString(jsonParam.get("idArr")),Integer.class);
        int userModifier=jsonParam.getIntValue("userModifier");
        annotationNegationService.designateAnnotationNegation(idArr,userModifier);
        return ResultVO.success();
    }
}
