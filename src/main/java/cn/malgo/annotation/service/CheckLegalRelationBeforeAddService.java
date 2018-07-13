package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;

public interface CheckLegalRelationBeforeAddService {

  boolean checkRelationIsNotLegalBeforeAdd(AddRelationRequest addRelationRequest, int roleId);

  boolean checkRelationIsNotLegalBeforeAdd(
      AddAnnotationGroupRequest addAnnotationGroupRequest,
      AnnotationTaskBlock annotationTaskBlock,
      int roleId);

  boolean checkRelationIsNotLegalBeforeUpdate(
      UpdateRelationRequest updateRelationRequest, int roleId);

  boolean checkRelationIsNotLegalBeforeUpdate(
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest,
      AnnotationTaskBlock annotationTaskBlock,
      int roleId);

  boolean checkRelationIsNotLegalBeforeUpdateEntity(
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest,
      AnnotationTaskBlock annotationTaskBlock);

  boolean checkRelationIsNotLegalBeforeUpdateEntity(
      UpdateAnnotationRequest updateAnnotationRequest, int roleId);
}
