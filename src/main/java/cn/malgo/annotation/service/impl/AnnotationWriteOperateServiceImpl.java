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
        if (addAnnotationGroupRequest.isAddEntity()) {
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
      case disease:
        annotation =
            AnnotationConvert.addRelationEntitiesAnnotation(
                oldAnnotation,
                addAnnotationGroupRequest.getType(),
                addAnnotationGroupRequest.getStartPosition(),
                addAnnotationGroupRequest.getEndPosition(),
                addAnnotationGroupRequest.getTerm());
        break;
      case drug:
        annotation =
            AnnotationConvert.addRelationEntitiesAnnotation(
                oldAnnotation,
                addAnnotationGroupRequest.getType(),
                addAnnotationGroupRequest.getStartPosition(),
                addAnnotationGroupRequest.getEndPosition(),
                addAnnotationGroupRequest.getTerm());
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
      case disease:
        annotation =
            AnnotationConvert.deleteEntitiesAnnotation(
                oldAnnotation, deleteAnnotationGroupRequest.getTag());
        break;
      case drug:
        annotation =
            AnnotationConvert.deleteEntitiesAnnotation(
                oldAnnotation, deleteAnnotationGroupRequest.getTag());
    }
    return annotation;
  }

  @Override
  public String updateMetaDataAnnotation(
      UpdateAnnotationGroupRequest request, String oldAnnotation, int annotationType) {
    String annotation = "";
    switch (AnnotationTypeEnum.getByValue(annotationType)) {
      case wordPos:
        annotation =
            AnnotationConvert.handleCrossAnnotation(
                oldAnnotation,
                request.getTerm(),
                request.getNewType(),
                request.getStartPosition(),
                request.getEndPosition());
        break;
      case sentence:
        annotation =
            AnnotationConvert.updateEntitiesAnnotation(
                oldAnnotation, request.getTag(), request.getNewType());
        break;
      case relation:
        if (request.isUpdatingEntity()) {
          annotation =
              AnnotationConvert.updateEntitiesAnnotation(
                  oldAnnotation, request.getTag(), request.getNewType());
        } else {
          annotation =
              AnnotationConvert.updateRelationAnnotation(
                  oldAnnotation, request.getReTag(), request.getRelation());
        }
        break;
      case disease:
        annotation =
            AnnotationConvert.updateEntitiesAnnotation(
                oldAnnotation, request.getTag(), request.getNewType());
        break;
      case drug:
        annotation =
            AnnotationConvert.updateEntitiesAnnotation(
                oldAnnotation, request.getTag(), request.getNewType());
        break;
    }
    return annotation;
  }
}
