package com.microservice.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.dataAccessLayer.entity.AnnotationParallel;
import com.microservice.dataAccessLayer.mapper.AnnotationParallelMapper;
import com.microservice.enums.AnnotationParallelStateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by cjl on 2018/4/16.
 */
@Service
public class AnnotationParallelService {

    @Autowired
    private AnnotationParallelMapper annotationParallelMapper;

    /**
     * 条件，分页查询annotation_sentence表
     */
    public Page<AnnotationParallel> queryAnnotationByCondition(int pageIndex, int pageSize, List<String> stateList, String userModifier){
        Page<AnnotationParallel> pageInfo=PageHelper.startPage(pageIndex,pageSize);
        AnnotationParallel paramAnnotation=new AnnotationParallel();
//        paramAnnotation.setState(state);
        paramAnnotation.setUserModifier(userModifier);
        annotationParallelMapper.listAnnotationParallelByCondition(paramAnnotation,stateList);
        return pageInfo;
    }

    /**
     * 新增annotation_sentence中标注的单位标注
     * @param anId
     */
    public AnnotationParallel getAnnotationParallel(int anId){
        return annotationParallelMapper.getAnnotationParallelById(anId);
    }

    /**
     * 更新annotation_sentence
     */
    public int updateAnnotationParallel(AnnotationParallel annotationParallel){

        return annotationParallelMapper.updateAnnotationParallelSelective(annotationParallel);
    }

    /**
     * 分配标注数据给指定的用户
     * @param idArr
     * @param userModifier
     */
    public void designateAnnotationParallel(List<Integer> idArr,int userModifier){
        annotationParallelMapper.updateAnnotationParallelUserModifierByIdArr(idArr,userModifier,AnnotationParallelStateEnum.DISTRIBUTIONED.getMessage());
    }

    /**
     * 审核标注数据
     * @param
     */
    public void batchExamineAnnotationParallel(List<Integer> idArr){
        annotationParallelMapper.batchUpdateAnnotationParallelStateByIdArr(idArr, AnnotationParallelStateEnum.EXAMINED.getMessage());
    }

}
