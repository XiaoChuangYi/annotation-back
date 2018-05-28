package com.microservice.controller;

import com.github.pagehelper.Page;
import com.microservice.dataAccessLayer.entity.AnnotationWordPos;
import com.microservice.request.*;
import com.microservice.result.AnnotationBratVO;
import com.microservice.result.PageVO;
import com.microservice.result.ResultVO;
import com.microservice.service.annotation.AnnotationService;
import com.microservice.utils.AnnotationRelevantConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


/**
 * Created by cjl on 2018/5/23.
 */
@RestController
@RequestMapping(value = "/relevant")
public class RelevantAnnotationController {


    @Autowired
    private AnnotationService annotationService;

    /**
     * 全局查询接口
     */
    @RequestMapping(value = "queryAnnotationRelation.do")
    public ResultVO<PageVO<AnnotationBratVO>> queryAnnotationRelation(@RequestBody RelationQuery relationQuery) {
        Page<AnnotationWordPos> pageInfo=annotationService.listAnnotationByStatesPaging(Arrays.asList(""),relationQuery.getPageIndex(),relationQuery.getPageSize());
        List<AnnotationBratVO> annotationBratVOList=AnnotationRelevantConvert.convert2AnnotationBratVOList(pageInfo.getResult());
        PageVO<AnnotationBratVO> pageVO=new PageVO(pageInfo,false);
        pageVO.setDataList(annotationBratVOList);
        return ResultVO.success(pageVO);
    }


    /**
     * 关联标注，新增标签之间的(relation)关系
     */
    @RequestMapping(value = "/addAnnotationRelation.do")
    public ResultVO addAnnotationRelation(@RequestBody RelationAdd relationAdd) {
        String check = relationAdd.check(relationAdd);
        if (check.length() > 0)
            return ResultVO.error(check);
        AnnotationWordPos annotation = annotationService.getAnnotationById(relationAdd.getAnId());
        String finalAnnotation = AnnotationRelevantConvert.addRelationAnnotation(annotation.getFinalAnnotation(), relationAdd.getSourceTag(), relationAdd.getTargetTag(), relationAdd.getRelation());
        AnnotationBratVO annotationBratVO = updateAnnotationReBratVO(relationAdd.getAnId(), finalAnnotation);
        return ResultVO.success(annotationBratVO);
    }

    /**
     * 关联标注，删除标签之间的(relation)关系
     */
    @RequestMapping(value = "/deleteAnnotationRelation.do")
    public ResultVO deleteAnnotationRelation(@RequestBody RelationDelete relationDelete) {
        String check = relationDelete.check(relationDelete);
        if (check.length() > 0)
            return ResultVO.error(check);

        AnnotationWordPos annotation = annotationService.getAnnotationById(relationDelete.getAnId());
        String finalAnnotation = AnnotationRelevantConvert.deleteRelationAnnotation(annotation.getFinalAnnotation(), relationDelete.getrTag());
        AnnotationBratVO annotationBratVO = updateAnnotationReBratVO(relationDelete.getAnId(), finalAnnotation);
        return ResultVO.success(annotationBratVO);
    }

    /**
     * 关联标注，更新标签之间的(relation)关系
     */
    @RequestMapping(value = "/updateAnnotationRelation.do")
    public ResultVO updateAnnotationRelation(@RequestBody RelationUpdate relationUpdate) {
        String check = relationUpdate.check(relationUpdate);
        if (check.length() > 0)
            return ResultVO.error(check);
        AnnotationWordPos annotationWordPos = annotationService.getAnnotationById(relationUpdate.getAnId());
        String finalAnnotation = AnnotationRelevantConvert.updateRelationAnnotation(annotationWordPos.getFinalAnnotation(), relationUpdate.getrTag(), relationUpdate.getRelation());
        AnnotationBratVO annotationBratVO = updateAnnotationReBratVO(relationUpdate.getAnId(), finalAnnotation);
        return ResultVO.success(annotationBratVO);
    }

    /**
     * 关联标注，更新标签的sourceTag或者是targetTag
     */
    @RequestMapping(value = "/updateRelationTag.do")
    public ResultVO updateRelationTag(@RequestBody RelationTagUpdate relationTagUpdate) {
        String check = relationTagUpdate.check(relationTagUpdate);
        if (check.length() > 0)
            return ResultVO.error(check);
        AnnotationWordPos annotationWordPos = annotationService.getAnnotationById(relationTagUpdate.getAnId());
        String finalAnnotation = AnnotationRelevantConvert.updateRelationTag(annotationWordPos.getFinalAnnotation(), relationTagUpdate.getrTag(), relationTagUpdate.getSourceTag(), relationTagUpdate.getTargetTag());
        AnnotationBratVO annotationBratVO = updateAnnotationReBratVO(relationTagUpdate.getAnId(), finalAnnotation);
        return ResultVO.success(annotationBratVO);
    }

    private AnnotationBratVO updateAnnotationReBratVO(String anId, String finalAnnotation) {
        //更新单条标注信息到数据库
        AnnotationWordPos paramAnnotation = new AnnotationWordPos();
        paramAnnotation.setId(anId);
        paramAnnotation.setFinalAnnotation(finalAnnotation);
        annotationService.updateAnnotation(paramAnnotation);
        AnnotationWordPos newAnnotation = annotationService.getAnnotationById(anId);
        AnnotationBratVO annotationBratVO = AnnotationRelevantConvert.convert2AnnotationBratVO(newAnnotation);
        return annotationBratVO;
    }
}
