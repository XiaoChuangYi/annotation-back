package cn.malgo.annotation.service;

import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import lombok.extern.slf4j.Slf4j;

/** Created by cjl on 2018/5/31. */
public interface AnnotationOperateService {

  String addAnnotation(AddAnnotationRequest addAnnotationRequest, int roleId);

  String deleteAnnotation(DeleteAnnotationRequest deleteAnnotationRequest, int roleId);

  String updateAnnotation(UpdateAnnotationRequest updateAnnotationRequest, int roleId);
}
