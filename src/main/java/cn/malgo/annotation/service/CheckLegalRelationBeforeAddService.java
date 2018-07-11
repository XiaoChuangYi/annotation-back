package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.AddRelationRequest;

public interface CheckLegalRelationBeforeAddService {

  boolean checkRelationIsNotLegalBeforeAdd(AddRelationRequest addRelationRequest, int roleId);

  boolean checkRelationIsNotLegalBeforeAdd(
      AddAnnotationGroupRequest addAnnotationGroupRequest,
      AnnotationTaskBlock annotationTaskBlock,
      int roleId);
}
