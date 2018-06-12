package com.malgo.service;

import com.malgo.request.brat.AddAnnotationRequest;
import com.malgo.request.brat.DeleteAnnotationRequest;
import com.malgo.request.brat.UpdateAnnotationRequest;

/** Created by cjl on 2018/6/12. */
public interface AnnotationRelationService {
  String addAnnotation(AddAnnotationRequest addAnnotationRequest, int roleId);

  String deleteAnnotation(DeleteAnnotationRequest deleteAnnotationRequest, int roleId);

  String updateAnnotation(UpdateAnnotationRequest updateAnnotationRequest, int roleId);
}
