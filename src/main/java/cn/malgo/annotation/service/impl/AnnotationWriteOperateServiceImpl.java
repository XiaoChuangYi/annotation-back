package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.service.AnnotationWriteOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AnnotationWriteOperateServiceImpl implements AnnotationWriteOperateService {

  private static final String wordPosDeleteType = "Token";

  @Override
  public String addMetaDataAnnotation(
      AddAnnotationGroupRequest addAnnotationGroupRequest,
      String oldAnnotation,
      int annotationType) {
    String annotation = "";
    switch (AnnotationTypeEnum.getByValue(annotationType)) {
      case wordPos:
        annotation =
            AnnotationConvert.handleCrossAnnotation(
                oldAnnotation,
                addAnnotationGroupRequest.getTerm(),
                addAnnotationGroupRequest.getType(),
                addAnnotationGroupRequest.getStartPosition(),
                addAnnotationGroupRequest.getEndPosition());
        break;
      case sentence:
        annotation =
            AnnotationConvert.addEntitiesAnnotation(
                oldAnnotation,
                addAnnotationGroupRequest.getType(),
                addAnnotationGroupRequest.getStartPosition(),
                addAnnotationGroupRequest.getEndPosition(),
                addAnnotationGroupRequest.getTerm());
        break;
      case relation:
        if (StringUtils.isAllBlank(
            addAnnotationGroupRequest.getRelation(),
            addAnnotationGroupRequest.getSourceTag(),
            addAnnotationGroupRequest.getTargetTag())) {
          annotation =
              AnnotationConvert.addRelationEntitiesAnnotation(
                  oldAnnotation,
                  addAnnotationGroupRequest.getType(),
                  addAnnotationGroupRequest.getStartPosition(),
                  addAnnotationGroupRequest.getEndPosition(),
                  addAnnotationGroupRequest.getTerm());
        } else {
          annotation =
              AnnotationConvert.addRelationsAnnotation(
                  oldAnnotation,
                  addAnnotationGroupRequest.getSourceTag(),
                  addAnnotationGroupRequest.getTargetTag(),
                  addAnnotationGroupRequest.getRelation());
        }
        break;
    }
    return annotation;
  }

  @Override
  public String deleteMetaDataAnnotation(
      DeleteAnnotationGroupRequest deleteAnnotationGroupRequest,
      String oldAnnotation,
      int annotationType) {
    String annotation = "";
    switch (AnnotationTypeEnum.getByValue(annotationType)) {
      case wordPos:
        annotation =
            AnnotationConvert.handleCrossAnnotation(
                oldAnnotation,
                deleteAnnotationGroupRequest.getTerm(),
                wordPosDeleteType,
                deleteAnnotationGroupRequest.getStartPosition(),
                deleteAnnotationGroupRequest.getEndPosition());
        break;
      case relation:
        if (StringUtils.isBlank(deleteAnnotationGroupRequest.getReTag())) {
          annotation =
              AnnotationConvert.deleteEntitiesAnnotation(
                  oldAnnotation, deleteAnnotationGroupRequest.getTag());
        } else {
          annotation =
              AnnotationConvert.deleteRelationsAnnotation(
                  oldAnnotation, deleteAnnotationGroupRequest.getReTag());
        }
        break;
      case sentence:
        annotation =
            AnnotationConvert.deleteEntitiesAnnotation(
                oldAnnotation, deleteAnnotationGroupRequest.getTag());
        break;
    }
    return annotation;
  }

  @Override
  public String updateMetaDataAnnotation(
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest,
      String oldAnnotation,
      int annotationType) {
    String annotation = "";
    switch (AnnotationTypeEnum.getByValue(annotationType)) {
      case wordPos:
        annotation =
            AnnotationConvert.handleCrossAnnotation(
                oldAnnotation,
                updateAnnotationGroupRequest.getTerm(),
                updateAnnotationGroupRequest.getNewType(),
                updateAnnotationGroupRequest.getStartPosition(),
                updateAnnotationGroupRequest.getEndPosition());
        break;
      case sentence:
        annotation =
            AnnotationConvert.updateEntitiesAnnotation(
                oldAnnotation,
                updateAnnotationGroupRequest.getTag(),
                updateAnnotationGroupRequest.getNewType());
        break;
      case relation:
        if (StringUtils.isAllBlank(
            updateAnnotationGroupRequest.getReTag(), updateAnnotationGroupRequest.getRelation())) {
          annotation =
              AnnotationConvert.updateEntitiesAnnotation(
                  oldAnnotation,
                  updateAnnotationGroupRequest.getTag(),
                  updateAnnotationGroupRequest.getNewType());
        } else {
          annotation =
              AnnotationConvert.updateRelationAnnotation(
                  oldAnnotation,
                  updateAnnotationGroupRequest.getReTag(),
                  updateAnnotationGroupRequest.getRelation());
        }
        break;
    }
    return annotation;
  }
}
