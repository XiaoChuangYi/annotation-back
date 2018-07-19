package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;

public interface AnnotationOperateService {
  String addAnnotation(AnnotationCombine annotation, AddAnnotationRequest request);

  String deleteAnnotation(AnnotationCombine annotation, DeleteAnnotationRequest request);

  String updateAnnotation(AnnotationCombine annotation, UpdateAnnotationRequest request);
}
