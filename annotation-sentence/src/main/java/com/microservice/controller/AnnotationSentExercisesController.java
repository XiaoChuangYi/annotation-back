package com.microservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.microservice.dataAccessLayer.entity.AnnotationSentenceExercise;
import com.microservice.dataAccessLayer.entity.UserAccount;
import com.microservice.dataAccessLayer.entity.UserExercises;
import com.microservice.enums.AnnotationSentExercisesStateEnum;
import com.microservice.result.PageVO;
import com.microservice.result.ResultVO;
import com.microservice.service.AnnotationSentExercisesService;
import com.microservice.service.UserExercisesService;
import com.microservice.utils.AnnotationConvert;
import com.microservice.vo.AnnotationSentExerciseBratVO;
import com.microservice.vo.UserExercisesBratVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by cjl on 2018/5/3.
 */
@RestController
@RequestMapping(value = "/annotationSentExercises")
public class AnnotationSentExercisesController extends BaseController{


    @Autowired
    private AnnotationSentExercisesService annotationSentExercisesService;

    @Autowired
    private UserExercisesService userExercisesService;


    /**
     * 指派界面，获取标准习题集
     */
    @RequestMapping(value = "/queryStandardAnnotationToDistribution.do")
    public ResultVO<Map<String,Object>> queryStandardAnnotationToDistribution(@RequestBody JSONObject jsonParam,@ModelAttribute("userAccount") UserAccount userAccount){
        int pageIndex=jsonParam.getIntValue("pageIndex");
        int pageSize=jsonParam.getIntValue("pageSize");
        int userModifier=jsonParam.getIntValue("userModifier");
        String state=jsonParam.getString("state");

        Map<String,Object> finalMap=annotationSentExercisesService.listAnnotationSentExercise(pageIndex,pageSize,userModifier,state);
        List<AnnotationSentExerciseBratVO> annotationSentExerciseBratVOList= AnnotationConvert.
                convert2AnnotationSentBratVOList((List<AnnotationSentenceExercise>)finalMap.get("dataList"));
        finalMap.replace("dataList",annotationSentExerciseBratVOList);

        return ResultVO.success(finalMap);
    }

    /**
     * 根据用户Id过滤查询练习员习题标注
     */
    @RequestMapping(value = "/queryPracticeAnnotationToDistribution.do")
    public ResultVO<PageVO<UserExercisesBratVO>> queryPracticeAnnotationToDistribution(@RequestBody JSONObject jsonParam,@ModelAttribute("userAccount") UserAccount userAccount){
        int pageIndex=jsonParam.getIntValue("pageIndex");
        int pageSize=jsonParam.getIntValue("pageSize");
        int userModifier=jsonParam.getIntValue("userModifier");
        String state=jsonParam.getString("state");
        Page<UserExercises> pageInfo =userExercisesService.listUserExercisesAssociatePaging(pageIndex,pageSize,userModifier,state);
        List<UserExercisesBratVO> userExercisesBratVOList= AnnotationConvert.convert2UserExercisesBratVOList(pageInfo.getResult());
        PageVO<UserExercisesBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(userExercisesBratVOList);
        return ResultVO.success(pageVO);
    }


    @RequestMapping(value = "/queryPracticeAnnotation.do")
    public ResultVO<PageVO<UserExercisesBratVO>> queryPracticeAnnotation(@RequestBody JSONObject jsonParam,@ModelAttribute("userAccount") UserAccount userAccount){
        int pageIndex=jsonParam.getIntValue("pageIndex");
        int pageSize=jsonParam.getIntValue("pageSize");
        String state=jsonParam.getString("state");
        Page<UserExercises> pageInfo =userExercisesService.listUserExercisesPaging(pageIndex,pageSize,userAccount.getId(),state);
        List<UserExercisesBratVO> userExercisesBratVOList= AnnotationConvert.convert2UserExercisesBratVOList(pageInfo.getResult());
        PageVO<UserExercisesBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(userExercisesBratVOList);
        return ResultVO.success(pageVO);
    }

    @RequestMapping(value = "/queryStandardAnnotation.do")
    public ResultVO<PageVO<AnnotationSentExerciseBratVO>> queryStandardAnnotation(@RequestBody JSONObject jsonParam){
        int pageIndex=jsonParam.getIntValue("pageIndex");
        int pageSize=jsonParam.getIntValue("pageSize");
        String originText=jsonParam.getString("originText");
        Page<AnnotationSentenceExercise> pageInfo=annotationSentExercisesService.listAnnotationSentExerciseByPaging(pageIndex,pageSize,originText);
        List<AnnotationSentExerciseBratVO> annotationSentExerciseBratVOList=AnnotationConvert.convert2AnnotationSentBratVOList(pageInfo.getResult());
        PageVO<AnnotationSentExerciseBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(annotationSentExerciseBratVOList);
        return ResultVO.success(pageVO);
    }


    /**
     *  根据user_modifier，默认指派所有的习题集
     */
    @RequestMapping(value = "/initUserExercisesAnnotation.do")
    public ResultVO initUserExercisesAnnotation(@RequestBody JSONObject jsonParam){
        int userModifier=jsonParam.getIntValue("userModifier");
        annotationSentExercisesService.initUserExercises(userModifier);
        return ResultVO.success();
    }


    /**
     * 根据用户id重置状态
     */
    @RequestMapping(value = "/resetAnnotationExercisesByUserId.do")
    public ResultVO resetAnnotationExercisesByUserId(@RequestBody JSONObject jsonParam){
        int userModifier=jsonParam.getIntValue("userModifier");
        annotationSentExercisesService.resetUserExercisesByUserModifier(userModifier);
        return ResultVO.success();
    }


    /**
     * 批量重置
     */
    @RequestMapping(value = "/distributionAnnotationExercisesToUser.do")
    public ResultVO distributionAnnotationToUser(@RequestBody JSONObject jsonParam){
        List<Integer> idArr= JSON.parseArray(JSON.toJSONString(jsonParam.get("idArr")),Integer.class);
        int userModifier=jsonParam.getIntValue("userModifier");
        annotationSentExercisesService.designateAnnotationSentExercises(idArr,userModifier, AnnotationSentExercisesStateEnum.INIT.name());
        return ResultVO.success("成功批量指派！");
    }

    /**
     * 完成标注，提交练习标注
     */
    @RequestMapping(value = "/commitUserExercises.do")
    public ResultVO finishAnnotationExercises(@RequestBody JSONObject jsonParam){
        int id=jsonParam.getIntValue("id");
        int anId=jsonParam.getIntValue("anId");
        userExercisesService.commitAnnotationSentExercises(id,anId);
        return ResultVO.success("提交成功！");
    }


    /**
     * 查询单个标注
     */
    @RequestMapping(value = "/getAnnotationById.do")
    public ResultVO<AnnotationSentExerciseBratVO> getAnnotationById(@RequestBody JSONObject jsonParam){
        int id=jsonParam.getIntValue("id");
        AnnotationSentenceExercise annotationSentence=annotationSentExercisesService.getAnnotationSentExerciseById(id);
        AnnotationSentExerciseBratVO annotationSentenceBratVO=AnnotationConvert.convert2AnnotationSentBratVO(annotationSentence);
        return  ResultVO.success(annotationSentenceBratVO);
    }


    /**
     * 查询单个标注
     */
    @RequestMapping(value = "/getUserExercisesAnnotationById.do")
    public ResultVO<UserExercisesBratVO> getUserExercisesAnnotationById(@RequestBody JSONObject jsonParam){
        int id=jsonParam.getIntValue("id");
        UserExercises annotationSentence=userExercisesService.getUserExercisesById(id);
        UserExercisesBratVO annotationSentenceBratVO=AnnotationConvert.convert2UserExercisesBratVO(annotationSentence);
        return  ResultVO.success(annotationSentenceBratVO);
    }

    /**
     * 新增标注
     */
    @RequestMapping(value = "/addStandardAnnotation.do")
    public ResultVO<AnnotationSentExerciseBratVO> addStandardAnnotation(@RequestBody JSONObject jsonParam){
        String type=jsonParam.getString("type");
        String term=jsonParam.getString("term");
        int anId=jsonParam.getIntValue("id");
        int startPosition=jsonParam.getIntValue("startPosition");
        int endPosition=jsonParam.getIntValue("endPosition");

        if(StringUtils.isBlank(term))
            return ResultVO.error("选中文本内容为空，无法新增！");
        AnnotationSentenceExercise annotationExercise=annotationSentExercisesService.getAnnotationSentExerciseById(anId);

        String newAnnotationSentence = AnnotationConvert.addUnitAnnotation(annotationExercise.getStandardAnnotation(),
                type, startPosition, endPosition, term.trim());
        AnnotationSentExerciseBratVO finalAnnotationExerciseBratVO=updateStandardAnnotation(anId,newAnnotationSentence);
        return ResultVO.success(finalAnnotationExerciseBratVO);
    }


    /**
     * 删除标注
     */
    @RequestMapping(value = "/deleteStandardAnnotation.do")
    public ResultVO<AnnotationSentExerciseBratVO> deleteStandardAnnotation(@RequestBody JSONObject jsonParam){
        int anId=jsonParam.getIntValue("id");
        String tag=jsonParam.getString("tag");

        //判断当前用户是否可以操作该条标注数据
        AnnotationSentenceExercise annotationExercise=annotationSentExercisesService.getAnnotationSentExerciseById(anId);

        String newAnnotationSentence= AnnotationConvert.deleteUnitAnnotation(annotationExercise.getStandardAnnotation(), tag);
        //更新单条标注信息到数据库
        AnnotationSentExerciseBratVO finalAnnotationSentenceBratVO=updateStandardAnnotation(anId,newAnnotationSentence);
        return ResultVO.success(finalAnnotationSentenceBratVO);
    }


    /**
     *  更新标注
     */
    @RequestMapping(value = "/updateStandardAnnotation.do")
    public  ResultVO<AnnotationSentExerciseBratVO> updateStandardAnnotation(@RequestBody JSONObject params){
        int anId=params.getIntValue("anId");
        String tag=params.getString("tag");
        String newType=params.getString("newType");

        //判断当前用户是否可以操作该条标注数据
        AnnotationSentenceExercise annotationExercise=annotationSentExercisesService.getAnnotationSentExerciseById(anId);

        String finalAnnotation= AnnotationConvert.changeUnitAnnotation(annotationExercise.getStandardAnnotation(), tag, newType);
        //更新单条标注信息到数据库
        AnnotationSentExerciseBratVO annotationBratVO=updateStandardAnnotation(anId,finalAnnotation);
        return ResultVO.success(annotationBratVO);
    }


    /**
     * 新增标注
     */
    @RequestMapping(value = "/addUserPracticeAnnotation.do")
    public ResultVO<UserExercisesBratVO> addUserPracticeAnnotation(@RequestBody JSONObject jsonParam, @ModelAttribute("userAccount") UserAccount userAccount){
        String type=jsonParam.getString("type");
        String term=jsonParam.getString("term");
        int anId=jsonParam.getIntValue("id");
        int startPosition=jsonParam.getIntValue("startPosition");
        int endPosition=jsonParam.getIntValue("endPosition");
        if(StringUtils.isBlank(term))
            return ResultVO.error("选中文本内容为空，无法新增！");
        UserExercises annotationExercise=userExercisesService.getUserExercisesById(anId);
        if (annotationExercise.getUserModifier()!=userAccount.getId()) {
            return ResultVO.error("当前用户无法操作ID为'" + anId + "'记录!");
        }
        String newAnnotationSentence = AnnotationConvert.addUnitAnnotation(annotationExercise.getPracticeAnnotation(),
                    type, startPosition, endPosition, term);
        UserExercisesBratVO finalUserExercisesBratVO=updateAnnotationSentenceAnnotationText(anId,newAnnotationSentence);
        return ResultVO.success(finalUserExercisesBratVO);
    }


    /**
     * 删除标注
     */
    @RequestMapping(value = "/deleteUserPracticeSentence.do")
    public ResultVO<UserExercisesBratVO> deleteUserPracticeSentence(@RequestBody JSONObject jsonParam,@ModelAttribute("userAccount") UserAccount userAccount){
        int anId=jsonParam.getIntValue("id");
        String tag=jsonParam.getString("tag");

        //判断当前用户是否可以操作该条标注数据
        UserExercises annotationSentence=userExercisesService.getUserExercisesById(anId);
        if (annotationSentence.getUserModifier()!=userAccount.getId())
            return ResultVO.error("当前用户无权操作该" + anId + "记录!");
        String newAnnotationSentence= AnnotationConvert.deleteUnitAnnotation(annotationSentence.getPracticeAnnotation(), tag);
        //更新单条标注信息到数据库
        UserExercisesBratVO finalAnnotationSentenceBratVO=updateAnnotationSentenceAnnotationText(anId,newAnnotationSentence);
        return ResultVO.success(finalAnnotationSentenceBratVO);
    }


    /**
     *  更新标注
     */
    @RequestMapping(value = "/updateUserPracticeAnnotation.do")
    public  ResultVO<UserExercisesBratVO> updateUserPracticeAnnotation(@RequestBody JSONObject params,@ModelAttribute("userAccount") UserAccount userAccount){
        int anId=params.getIntValue("anId");
        String tag=params.getString("tag");
        String newType=params.getString("newType");

        //判断当前用户是否可以操作该条标注数据
        UserExercises annotation=userExercisesService.getUserExercisesById(anId);
        if (annotation.getUserModifier()!=userAccount.getId())
            return ResultVO.error("当前用户无权操作该"+anId+"记录!");
        String finalAnnotation= AnnotationConvert.changeUnitAnnotation(annotation.getPracticeAnnotation(), tag, newType);
        //更新单条标注信息到数据库
        UserExercisesBratVO annotationBratVO=updateAnnotationSentenceAnnotationText(anId,finalAnnotation);
        return ResultVO.success(annotationBratVO);
    }

    /**
     * 更新annotation_sentence表的annotation_text字段
     */
    private UserExercisesBratVO updateAnnotationSentenceAnnotationText(int anId,String newAnnotationText){
        UserExercises paramUserExercises=new UserExercises();
        paramUserExercises.setId(anId);
        //管理员或者审核人员角色
        paramUserExercises.setPracticeAnnotation(newAnnotationText);
        paramUserExercises.setGmtModified(new Date());
        userExercisesService.updateUserExercisesSelective(paramUserExercises);
        UserExercises userExercises = userExercisesService.getUserExercisesById(anId);
        return AnnotationConvert.convert2UserExercisesBratVO(userExercises);
    }

    /**
     * 更新annotation_sentence表的annotation_text字段
     */
    private AnnotationSentExerciseBratVO updateStandardAnnotation(int id,String newAnnotationText){

        AnnotationSentenceExercise annotationSentenceExercise=new AnnotationSentenceExercise();
        annotationSentenceExercise.setId(id);
        annotationSentenceExercise.setStandardAnnotation(newAnnotationText);
        annotationSentenceExercise.setGmtModified(new Date());

        annotationSentExercisesService.updateAnnotationSentExerciseSelective(annotationSentenceExercise);
        AnnotationSentenceExercise finalAnnotationSentenceExercise=annotationSentExercisesService.getAnnotationSentExerciseById(id);
        return AnnotationConvert.convert2AnnotationSentBratVO(finalAnnotationSentenceExercise);
    }
}
