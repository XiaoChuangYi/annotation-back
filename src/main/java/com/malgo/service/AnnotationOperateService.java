package com.malgo.service;

import com.malgo.request.brat.AddAnnotationRequest;
import com.malgo.request.brat.DeleteAnnotationRequest;
import com.malgo.request.brat.UpdateAnnotationRequest;
import lombok.extern.slf4j.Slf4j;

/** Created by cjl on 2018/5/31. */
public interface AnnotationOperateService {

  String addAnnotation(AddAnnotationRequest addAnnotationRequest, int roleId);

  String deleteAnnotation(DeleteAnnotationRequest deleteAnnotationRequest);

  String updateAnnotation(UpdateAnnotationRequest updateAnnotationRequest);

}
