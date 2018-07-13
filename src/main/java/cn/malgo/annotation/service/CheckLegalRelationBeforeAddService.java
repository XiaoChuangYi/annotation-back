package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;

public interface CheckLegalRelationBeforeAddService {

  boolean checkRelationIsNotLegalBeforeAdd(AddRelationRequest addRelationRequest);

  boolean checkRelationIsNotLegalBeforeAdd(
      AddAnnotationGroupRequest addAnnotationGroupRequest, AnnotationTaskBlock annotationTaskBlock);

  boolean checkRelationIsNotLegalBeforeUpdate(UpdateRelationRequest updateRelationRequest);

  boolean checkRelationIsNotLegalBeforeUpdate(
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest,
      AnnotationTaskBlock annotationTaskBlock);

  boolean checkRelationIsNotLegalBeforeUpdateEntity(
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest,
      AnnotationTaskBlock annotationTaskBlock);

  boolean checkRelationIsNotLegalBeforeUpdateEntity(
      UpdateAnnotationRequest updateAnnotationRequest);
}
