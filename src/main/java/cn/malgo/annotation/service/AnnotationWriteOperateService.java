package cn.malgo.annotation.service;

import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;

public interface AnnotationWriteOperateService {

  String addMetaDataAnnotation(
      AddAnnotationGroupRequest addAnnotationGroupRequest,
      String oldAnnotation,
      int annotationType);

  String deleteMetaDataAnnotation(
      DeleteAnnotationGroupRequest deleteAnnotationGroupRequest,
      String oldAnnotation,
      int annotationType);

  String updateMetaDataAnnotation(
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest,
      String oldAnnotation,
      int annotationType);
}
