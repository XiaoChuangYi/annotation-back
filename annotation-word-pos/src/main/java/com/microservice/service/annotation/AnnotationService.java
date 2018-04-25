package com.microservice.service.annotation;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.microservice.dataAccessLayer.entity.Annotation;
import com.microservice.dataAccessLayer.mapper.AnnotationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by cjl on 2018/3/29.
 */
@Service
public class AnnotationService {

    @Autowired
    protected AnnotationMapper annotationMapper;

    /**
     * 根据用户账号信息和标注状态和标注文本信息分页查询标注数据
     * @param annotationState
     * @param modifier
     * @param term
     * @param pageIndex
     * @param pageSize
     */
    public Page<Annotation> listAnnotationByConditionPaging(String annotationState, String modifier,String term, int pageIndex, int pageSize){
        Page<Annotation> pageInfo= PageHelper.startPage(pageIndex,pageSize);
        Annotation pAnnotation=new Annotation();
        pAnnotation.setModifier(modifier);
        pAnnotation.setState(annotationState);
        pAnnotation.setTerm(term);
        annotationMapper.listAnnotationByCondition(pAnnotation,"gmt_created desc");
        return pageInfo;
    }

    /**
     * 根据标注状态信息集合分页查询标注数据
     * @param stateList
     * @param pageIndex
     * @param pageSize
     */
    public Page<Annotation> listAnnotationByStatesPaging(List<String> stateList,int pageIndex,int pageSize){
        Page<Annotation> pageInfo=PageHelper.startPage(pageIndex,pageSize);
        annotationMapper.listAnnotationByStateList(stateList,"state desc,gmt_created asc");
        return pageInfo;
    }

    /**
     * 根据主键ID查询特定的标注记录
     * @param id
     */
    public Annotation getAnnotationById(String id){
        return annotationMapper.getAnnotationById(id);
    }

    /**
     * 根据主键ID选择性选取字段，更新annotation
     * @param annotation
     */
    public void updateAnnotation(Annotation annotation){
        annotationMapper.updateAnnotationSelective(annotation);
    }

    /**
     * 批量更新标注annotation表的modifier字段
     * @param idList
     * @param modifier
     */
    public void batchUpdateAnnotationModifier(List<String> idList,String modifier){
        annotationMapper.batchUpdateAnnotationModifier(idList,modifier);
    }

    /**
     *批量更新标注表的最终和手动标注
     * @param annotationList
     */
    public void batchUpdateAnnotationFinalAndManual(List<Annotation> annotationList){
        annotationMapper.batchUpdateAnnotation(annotationList);
    }

    /**
     * 审核标注
     * @param anId
     */
    public void finishAnnotation(String anId){

    }

}
