package com.microservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.microservice.dataAccessLayer.entity.*;
import com.microservice.enums.AnnotationWordPosExerciseStateEnum;
import com.microservice.result.PageVO;
import com.microservice.result.ResultVO;
import com.microservice.service.exercise.AnnotationWordPosExerciseService;
import com.microservice.service.exercise.UserWordExerciseService;
import com.microservice.utils.AnnotationConvert;
import com.microservice.vo.AnnotationWordPosExerciseBratVO;
import com.microservice.vo.UserWordExerciseBratVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by cjl on 2018/5/17.
 */
@RestController
@RequestMapping(value = "/exercise")
public class AnnotationWordPosExerciseController extends BaseController{


    @Autowired
    private AnnotationWordPosExerciseService annotationWordPosExerciseService;

    @Autowired
    private UserWordExerciseService userWordExerciseService;

    /**
     * 指派界面，获取标准的习题集
     */
    @RequestMapping(value = "/queryStandardAnnotationRelateUser.do")
    public ResultVO<Map<String, Object>> queryStandardAnnotationRelateUser(@RequestBody JSONObject jsonParam) {
        int pageIndex = jsonParam.getIntValue("pageIndex");
        int pageSize = jsonParam.getIntValue("pageSize");
        int userModifier = jsonParam.getIntValue("userModifier");
        String state = jsonParam.getString("state");
        Map<String,Object> finalMap=annotationWordPosExerciseService.listAnnotationWordExercise(pageIndex,pageSize,userModifier,state);
        List<AnnotationWordPosExerciseBratVO> annotationSentExerciseBratVOList= AnnotationConvert.
                convert2StandardAnnotationWordBratVOList((List<AnnotationWordPosExercise>)finalMap.get("dataList"));
        finalMap.replace("dataList",annotationSentExerciseBratVOList);
        return ResultVO.success(finalMap);
    }

    /**
     * 根据用户ID和标准习题集的IDArr，新增指定习题集给指定的用户
     */
    @RequestMapping(value = "/distributionStandardAnnotationToUser.do")
    public ResultVO distributionStandardAnnotationToUser(@RequestBody JSONObject jsonParam) {
        List<Integer> idArr = JSON.parseArray(JSON.toJSONString(jsonParam.get("idArr")), Integer.class);
        int userModifier = jsonParam.getIntValue("userModifier");
        annotationWordPosExerciseService.designateAnnotationWordExercise(idArr,userModifier, AnnotationWordPosExerciseStateEnum.INIT.name());
        return ResultVO.success("批量指派完成！");
    }

    /**
     * 根据用户id重置指定用户的习题集
     */
    @RequestMapping(value = "/resetExerciseAnnotationByUserId.do")
    public ResultVO resetAnnotationExercisesByUserId(@RequestBody JSONObject jsonParam) {
        int userModifier = jsonParam.getIntValue("userModifier");
        annotationWordPosExerciseService.resetUserExercisesByUserModifier(userModifier);
        return ResultVO.success("当前用户重置完成！");
    }


    /**
     * 条件查询标准答案
     */
    @RequestMapping(value = "/queryStandardWordAnnotation.do")
    public ResultVO<PageVO<AnnotationWordPosExerciseBratVO>> queryStandardWordAnnotation(@RequestBody JSONObject jsonParam) {
        int pageIndex = jsonParam.getIntValue("pageIndex");
        int pageSize = jsonParam.getIntValue("pageSize");
        String originText = jsonParam.getString("originText");
        Page<AnnotationWordPosExercise> pageInfo=annotationWordPosExerciseService.listAnnotationSentExerciseByPaging(pageIndex,pageSize,originText);
        List<AnnotationWordPosExerciseBratVO> annotationSentExerciseBratVOList=AnnotationConvert.convert2StandardAnnotationWordBratVOList(pageInfo.getResult());
        PageVO<AnnotationWordPosExerciseBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(annotationSentExerciseBratVOList);
        return ResultVO.success(pageVO);
    }

    /**
     * 根据用户Id过滤查询练习员习题标注
     */
    @RequestMapping(value = "/queryPracticeAnnotation.do")
    public ResultVO<PageVO<UserWordExerciseBratVO>> queryPracticeAnnotation(@RequestBody JSONObject jsonParam,@ModelAttribute("userAccount") UserAccount userAccount) {
        int pageIndex = jsonParam.getIntValue("pageIndex");
        int pageSize = jsonParam.getIntValue("pageSize");
        String state = jsonParam.getString("state");
        int userModifier=jsonParam.getIntValue("userModifier");

        Page<UserWordExercise> pageInfo =userWordExerciseService.listUserWordExerciseAssociatePaging(pageIndex,pageSize,"管理员".equals(userAccount.getRole())?userModifier:userAccount.getId(),state);
        List<UserWordExerciseBratVO> userExercisesBratVOList= AnnotationConvert.convert2UserWordExerciseBratVOList(pageInfo.getResult());
        PageVO<UserWordExerciseBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(userExercisesBratVOList);
        return ResultVO.success(pageVO);
    }


    /**
     * 练习用户完成标注，提交练习标注
     */
    @RequestMapping(value = "/commitUserWordExercise.do")
    public ResultVO commitUserWordExercise(@RequestBody JSONObject jsonParam) {
        int id = jsonParam.getIntValue("id");
        int anId = jsonParam.getIntValue("anId");
        userWordExerciseService.commitUserWordExercise(id,anId);
        return ResultVO.success("提交成功！");
    }


    /**
     * 查询单个标准答案标注，进行界面刷新
     */
    @RequestMapping(value = "/getStandardAnnotationById.do")
    public ResultVO<AnnotationWordPosExerciseBratVO> getStandardAnnotationById(@RequestBody JSONObject jsonParam) {
        int id = jsonParam.getIntValue("id");
        AnnotationWordPosExercise annotationSentence=annotationWordPosExerciseService.getAnnotationWordPosExerciseById(id);
        AnnotationWordPosExerciseBratVO annotationWordPosExerciseBratVO=AnnotationConvert.convert2StandardAnnotationWordBratVO(annotationSentence);
        return ResultVO.success(annotationWordPosExerciseBratVO);
    }


    /**
     * 查询单个练习用户标注，进行界面刷新
     */
    @RequestMapping(value = "/getUserExerciseAnnotationById.do")
    public ResultVO<UserWordExerciseBratVO> getUserExerciseAnnotationById(@RequestBody JSONObject jsonParam) {
        int id = jsonParam.getIntValue("id");
        UserWordExercise userWordExercise=userWordExerciseService.getUserWordExerciseById(id);
        UserWordExerciseBratVO userWordExerciseBratVO=AnnotationConvert.convert2UserExercisesBratVO(userWordExercise);
        return ResultVO.success(userWordExerciseBratVO);
    }

    /**
     * 标准答案的标注的新增/删除/更新
     */
    /**
     * 新增标注
     */
    @RequestMapping(value = "/addStandardAnnotation.do")
    public ResultVO<AnnotationWordPosExerciseBratVO> addStandardAnnotation(@RequestBody JSONObject jsonParam) {
        String type = jsonParam.getString("type");
        String term = jsonParam.getString("term");
        int anId = jsonParam.getIntValue("id");
        int startPosition = jsonParam.getIntValue("startPosition");
        int endPosition = jsonParam.getIntValue("endPosition");

        AnnotationWordPosExercise annotationWordPosExercise = annotationWordPosExerciseService.getAnnotationWordPosExerciseById(anId);

//        String newAnnotation = AnnotationConvert.handleCrossAnnotation(annotationWordPosExercise.getStandardAnnotation(), term, type, startPosition, endPosition);
        String newAnnotation = AnnotationConvert.addUnitAnnotationByLambda(annotationWordPosExercise.getStandardAnnotation(), type, startPosition, endPosition,term);
        AnnotationWordPosExerciseBratVO finalAnnotationWordPosExerciseBratVO = updateStandardAnnotation(anId, newAnnotation);
        return ResultVO.success(finalAnnotationWordPosExerciseBratVO);
    }


    /**
     * 删除标注
     */
    @RequestMapping(value = "/deleteStandardAnnotation.do")
    public ResultVO<AnnotationWordPosExerciseBratVO> deleteStandardAnnotation(@RequestBody JSONObject jsonParam) {
        int anId = jsonParam.getIntValue("id");
        String tag = jsonParam.getString("tag");
        AnnotationWordPosExercise annotationWordPosExercise = annotationWordPosExerciseService.getAnnotationWordPosExerciseById(anId);
        String finalAnnotation = AnnotationConvert.deleteUnitAnnotationByLambda(annotationWordPosExercise.getStandardAnnotation(), tag);
        AnnotationWordPosExerciseBratVO finalAnnotationWordPosExerciseBratVO = updateStandardAnnotation(anId, finalAnnotation);
        return ResultVO.success(finalAnnotationWordPosExerciseBratVO);
    }


    /**
     * 更新标注
     */
    @RequestMapping(value = "/updateStandardAnnotation.do")
    public ResultVO<AnnotationWordPosExerciseBratVO> updateStandardAnnotation(@RequestBody JSONObject params) {
        int anId = params.getIntValue("anId");
        String tag = params.getString("tag");
        String newType = params.getString("newType");
        AnnotationWordPosExercise annotationWordPosExercise = annotationWordPosExerciseService.getAnnotationWordPosExerciseById(anId);
        String finalAnnotation = AnnotationConvert.updateUnitAnnotationTypeByLambda(annotationWordPosExercise.getStandardAnnotation(), newType, tag);
        AnnotationWordPosExerciseBratVO finalAnnotationWordPosExerciseBratVO = updateStandardAnnotation(anId, finalAnnotation);
        return ResultVO.success(finalAnnotationWordPosExerciseBratVO);
    }


    /**
     * 用户练习题集的新增/删除/更新
     */
    /**
     * 新增标注
     */
    @RequestMapping(value = "/addUserPracticeAnnotation.do")
    public ResultVO<UserWordExerciseBratVO> addUserPracticeAnnotation(@RequestBody JSONObject jsonParam, @ModelAttribute("userAccount") UserAccount userAccount) {
        String type = jsonParam.getString("type");
        String term = jsonParam.getString("term");
        int anId = jsonParam.getIntValue("id");
        int startPosition = jsonParam.getIntValue("startPosition");
        int endPosition = jsonParam.getIntValue("endPosition");

        UserWordExercise annotationExercise = userWordExerciseService.getUserWordExerciseById(anId);
        if (annotationExercise.getUserModifier() != userAccount.getId()) {
            return ResultVO.error("当前用户无法操作ID为'" + anId + "'记录!");
        }
//        String newAnnotationSentence = AnnotationConvert.handleCrossAnnotation(annotationExercise.getPracticeAnnotation(),term,
//                type, startPosition, endPosition);
        String newAnnotationSentence = AnnotationConvert.addUnitAnnotationByLambda(annotationExercise.getPracticeAnnotation(),
                type, startPosition, endPosition,term);
        UserWordExerciseBratVO finalUserExercisesBratVO = updateUserWordAnnotation(anId, newAnnotationSentence);
        return ResultVO.success(finalUserExercisesBratVO);
    }


    /**
     * 删除标注
     */
    @RequestMapping(value = "/deleteUserPracticeAnnotation.do")
    public ResultVO<UserWordExerciseBratVO> deleteUserPracticeAnnotation (@RequestBody JSONObject jsonParam, @ModelAttribute("userAccount") UserAccount userAccount) {
        int anId = jsonParam.getIntValue("id");
        String tag = jsonParam.getString("tag");

        //判断当前用户是否可以操作该条标注数据
        UserWordExercise annotationSentence = userWordExerciseService.getUserWordExerciseById(anId);
        if (annotationSentence.getUserModifier() != userAccount.getId())
            return ResultVO.error("当前用户无权操作该" + anId + "记录!");
        String newAnnotationSentence = AnnotationConvert.deleteUnitAnnotationByLambda(annotationSentence.getPracticeAnnotation(), tag);
        //更新单条标注信息到数据库
        UserWordExerciseBratVO finalAnnotationSentenceBratVO = updateUserWordAnnotation(anId, newAnnotationSentence);
        return ResultVO.success(finalAnnotationSentenceBratVO);
    }


    /**
     * 更新标注
     */
    @RequestMapping(value = "/updateUserPracticeAnnotation.do")
    public ResultVO<UserWordExerciseBratVO> updateUserPracticeAnnotation(@RequestBody JSONObject params, @ModelAttribute("userAccount") UserAccount userAccount) {
        int anId = params.getIntValue("anId");
        String tag = params.getString("tag");
        String newType = params.getString("newType");

        //判断当前用户是否可以操作该条标注数据
        UserWordExercise annotation = userWordExerciseService.getUserWordExerciseById(anId);
        if (annotation.getUserModifier() != userAccount.getId())
            return ResultVO.error("当前用户无权操作该" + anId + "记录!");
        String finalAnnotation = AnnotationConvert.updateUnitAnnotationTypeByLambda(annotation.getPracticeAnnotation(), tag, newType);
        //更新单条标注信息到数据库
        UserWordExerciseBratVO annotationBratVO = updateUserWordAnnotation(anId, finalAnnotation);
        return ResultVO.success(annotationBratVO);
    }


    /**
     * 更新user_word_exercise表的annotation_text字段
     */
    private UserWordExerciseBratVO updateUserWordAnnotation(int anId,String newAnnotationText){
        UserWordExercise paramUserExercises=new UserWordExercise();
        paramUserExercises.setId(anId);
        //管理员或者审核人员角色
        paramUserExercises.setPracticeAnnotation(newAnnotationText);
        paramUserExercises.setGmtModified(new Date());
        userWordExerciseService.updateUserWordExerciseSelective(paramUserExercises);
        UserWordExercise userExercises = userWordExerciseService.getUserWordExerciseById(anId);
        return AnnotationConvert.convert2UserExercisesBratVO(userExercises);
    }

    /**
     * 更新annotation_word_exercise表的standard_annotation字段
     */
    private AnnotationWordPosExerciseBratVO updateStandardAnnotation(int id, String newAnnotationText) {

        AnnotationWordPosExercise annotationWordExercise = new AnnotationWordPosExercise();
        annotationWordExercise.setId(id);
        annotationWordExercise.setStandardAnnotation(newAnnotationText);
        annotationWordExercise.setGmtModified(new Date());

        annotationWordPosExerciseService.updateAnnotationWordPosExerciseSelective(annotationWordExercise);
        AnnotationWordPosExercise finalAnnotationWordExercise = annotationWordPosExerciseService.getAnnotationWordPosExerciseById(id);
        return AnnotationConvert.convert2StandardAnnotationWordBratVO(finalAnnotationWordExercise);
    }

}
