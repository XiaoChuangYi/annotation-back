package cn.malgo.annotation.service;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;

public interface CheckRelationEntityService {
  boolean checkRelationEntityBeforeAdd(AddAnnotationRequest request, Annotation annotationNew);

  boolean checkRelationEntityBeforeUpdate(
      UpdateAnnotationRequest request, Annotation annotationNew);

  boolean addRelationEntityCheckAnchorSide(
      AddAnnotationRequest addAnnotationRequest, Annotation annotationNew);

  boolean updateRelationEntityCheckAnchorSide(
      UpdateAnnotationRequest updateAnnotationRequest, Annotation annotationNew);

  boolean hasIsolatedAnchor(Annotation annotationNew);
}
