package cn.malgo.annotation.service;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;

public interface CheckRelationEntityService {
  boolean checkRelationEntityBeforeAdd(AddAnnotationRequest request, Annotation annotationCombine);

  boolean checkRelationEntityBeforeUpdate(
      UpdateAnnotationRequest request, Annotation annotationCombine);

  boolean hasIsolatedAnchor(Annotation annotationCombine);
}
