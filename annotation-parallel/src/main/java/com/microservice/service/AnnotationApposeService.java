package com.microservice.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.dataAccessLayer.entity.AnnotationAppose;
import com.microservice.dataAccessLayer.mapper.AnnotationApposeMapper;
import com.microservice.enums.AnnotationApposeStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by cjl on 2018/4/16.
 */
@Service
public class AnnotationApposeService {

    @Autowired
    private AnnotationApposeMapper annotationApposeMapper;

    /**
     * 条件，分页查询annotation_sentence表
     */
    public Page<AnnotationAppose> queryAnnotationByCondition(int pageIndex, int pageSize, List<String> stateList, String userModifier){
        Page<AnnotationAppose> pageInfo=PageHelper.startPage(pageIndex,pageSize);
        AnnotationAppose paramAnnotation=new AnnotationAppose();
//        paramAnnotation.setState(state);
        paramAnnotation.setUserModifier(userModifier);
        annotationApposeMapper.listAnnotationParallelByCondition(paramAnnotation,stateList);
        return pageInfo;
    }

    /**
     * 新增annotation_sentence中标注的单位标注
     * @param anId
     */
    public AnnotationAppose getAnnotationParallel(int anId){
        return annotationApposeMapper.getAnnotationParallelById(anId);
    }

    /**
     * 更新annotation_sentence
     */
    public int updateAnnotationParallel(AnnotationAppose annotationParallel){

        return annotationApposeMapper.updateAnnotationParallelSelective(annotationParallel);
    }

    /**
     * 分配标注数据给指定的用户
     * @param idArr
     * @param userModifier
     */
    public void designateAnnotationParallel(List<Integer> idArr,int userModifier){
        annotationApposeMapper.updateAnnotationParallelUserModifierByIdArr(idArr,userModifier, AnnotationApposeStateEnum.DISTRIBUTIONED.getMessage());
    }

    /**
     * 审核标注数据
     * @param
     */
    public void batchExamineAnnotationParallel(List<Integer> idArr){
        annotationApposeMapper.batchUpdateAnnotationParallelStateByIdArr(idArr, AnnotationApposeStateEnum.EXAMINED.getMessage());
    }

}
