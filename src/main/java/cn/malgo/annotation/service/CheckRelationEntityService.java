package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;

public interface CheckRelationEntityService {

  boolean checkRelationEntityBeforeAdd(
      AddAnnotationRequest addAnnotationRequest, AnnotationCombine annotationCombine);

  boolean checkRelationEntityBeforeUpdate(
      UpdateAnnotationRequest updateAnnotationRequest, AnnotationCombine annotationCombine);
}
