package com.microservice.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.dataAccessLayer.entity.AnnotationSentence;
import com.microservice.dataAccessLayer.mapper.AnnotationSentenceMapper;
import com.microservice.enums.AnnotationSentenceStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by cjl on 2018/4/16.
 */
@Service
public class AnnotationSentenceService {

    @Autowired
    private AnnotationSentenceMapper annotationSentenceMapper;

    /**
     * 条件，分页查询annotation_sentence表
     */
    public Page<AnnotationSentence> queryAnnotationByCondition(int pageIndex, int pageSize, List<String> stateList, String userModifier){
        Page<AnnotationSentence> pageInfo=PageHelper.startPage(pageIndex,pageSize);
        AnnotationSentence paramAnnotation=new AnnotationSentence();
//        paramAnnotation.setState(state);
        paramAnnotation.setUserModifier(userModifier);
        annotationSentenceMapper.listAnnotationSentenceByCondition(paramAnnotation,stateList);
        return pageInfo;
    }

    /**
     * 主动分配，其它未分配指派的标注信息
     */
    public Page<AnnotationSentence> queryAnnotationUnDistribution(int pageIndex, int pageSize, List<String> stateList,String userModifier){
        Page<AnnotationSentence> pageInfo=PageHelper.startPage(pageIndex,pageSize);
        AnnotationSentence paramAnnotation=new AnnotationSentence();
        paramAnnotation.setUserModifier(userModifier);
        annotationSentenceMapper.listAutoDistributionAnnotationSentence(paramAnnotation,stateList);
        return pageInfo;
    }


    /**
     * 新增annotation_sentence中标注的单位标注
     * @param anId
     */
    public AnnotationSentence getAnnotationSentence(int anId){
        return annotationSentenceMapper.getAnnotationSentenceById(anId);
    }

    /**
     * 更新annotation_sentence
     */
    public int updateAnnotationSentence(AnnotationSentence annotationSentence){

        return annotationSentenceMapper.updateAnnotationSentenceSelective(annotationSentence);
    }

    /**
     * 分配标注数据给指定的用户
     * @param idArr
     * @param userModifier
     */
    public void designateAnnotationSentence(List<Integer> idArr,int userModifier){
        annotationSentenceMapper.updateAnnotationSentenceUserModifierByIdArr(idArr,userModifier,AnnotationSentenceStateEnum.DISTRIBUTIONED.getMessage());
    }

    /**
     * 审核标注数据
     * @param
     */
    public void batchExamineAnnotationSentence(List<Integer> idArr){
        annotationSentenceMapper.batchUpdateAnnotationSentenceStateByIdArr(idArr, AnnotationSentenceStateEnum.EXAMINED.getMessage());
    }

}
