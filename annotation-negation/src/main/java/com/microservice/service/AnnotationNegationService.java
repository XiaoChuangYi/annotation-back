package com.microservice.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.dataAccessLayer.entity.AnnotationNegation;
import com.microservice.dataAccessLayer.mapper.AnnotationNegationMapper;
import com.microservice.enums.AnnotationNegationStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by cjl on 2018/4/16.
 */
@Service
public class AnnotationNegationService {

    @Autowired
    private AnnotationNegationMapper annotationNegationMapper;

    /**
     * 条件，分页查询annotation_sentence表
     */
    public Page<AnnotationNegation> queryAnnotationByCondition(int pageIndex, int pageSize, List<String> stateList, String userModifier){
        Page<AnnotationNegation> pageInfo=PageHelper.startPage(pageIndex,pageSize);
        AnnotationNegation paramAnnotation=new AnnotationNegation();
//        paramAnnotation.setState(state);
        paramAnnotation.setUserModifier(userModifier);
        annotationNegationMapper.listAnnotationNegationByCondition(paramAnnotation,stateList);
        return pageInfo;
    }

    /**
     * 新增annotation_sentence中标注的单位标注
     * @param anId
     */
    public AnnotationNegation getAnnotationNegation(int anId){
        return annotationNegationMapper.getAnnotationNegationById(anId);
    }

    /**
     * 更新annotation_sentence
     */
    public int updateAnnotationNegation(AnnotationNegation annotationNegation){

        return annotationNegationMapper.updateAnnotationNegationSelective(annotationNegation);
    }

    /**
     * 分配标注数据给指定的用户
     * @param idArr
     * @param userModifier
     */
    public void designateAnnotationNegation(List<Integer> idArr,int userModifier){
        annotationNegationMapper.updateAnnotationNegationUserModifierByIdArr(idArr,userModifier,AnnotationNegationStateEnum.DISTRIBUTIONED.getMessage());
    }

    /**
     * 审核标注数据
     * @param
     */
    public void batchExamineAnnotationNegation(List<Integer> idArr){
        annotationNegationMapper.batchUpdateAnnotationNegationStateByIdArr(idArr, AnnotationNegationStateEnum.EXAMINED.getMessage());
    }

}
