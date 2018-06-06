package com.malgo.service.impl;

import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.request.brat.AddAnnotationRequest;
import com.malgo.request.brat.DeleteAnnotationRequest;
import com.malgo.request.brat.UpdateAnnotationRequest;
import com.malgo.service.AnnotationOperateService;
import com.malgo.utils.AnnotationConvert;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by cjl on 2018/5/31.
 */
@Service("local-final")
@Slf4j
public class AnnotationOperateServiceFinalImpl implements AnnotationOperateService{


  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public AnnotationOperateServiceFinalImpl(AnnotationCombineRepository annotationCombineRepository){
    this.annotationCombineRepository=annotationCombineRepository;
  }

  //练习人员 分句是对finalAnnotation操作
  //如何复用呢，分词/分句审核人员，新增标注，是对review_annotation字段做操作，
  @Override
  public String addAnnotation(AddAnnotationRequest addAnnotationRequest) {
    Optional<AnnotationCombine> optional =annotationCombineRepository.findById(addAnnotationRequest.getId());
    if(optional.isPresent()){
      AnnotationCombine annotationCombine=optional.get();
      String newAnnotation=AnnotationConvert.addEntitiesAnnotation(annotationCombine.getFinalAnnotation()
          ,addAnnotationRequest.getType(),addAnnotationRequest.getStartPosition()
          ,addAnnotationRequest.getEndPosition(),addAnnotationRequest.getTerm()
          );
      return newAnnotation;
    }
    return "";
  }

  @Override
  public String deleteAnnotation(DeleteAnnotationRequest deleteAnnotationRequest) {
    Optional<AnnotationCombine> optional=annotationCombineRepository.findById(deleteAnnotationRequest.getId());
    if(optional.isPresent()){
      AnnotationCombine annotationCombine=optional.get();
      String newAnnotation=AnnotationConvert.deleteEntitiesAnnotation(annotationCombine.getFinalAnnotation(),deleteAnnotationRequest.getTag());
      return newAnnotation;
    }
    return "";
  }

  @Override
  public String updateAnnotation(UpdateAnnotationRequest updateAnnotationRequest) {
    Optional<AnnotationCombine> optional=annotationCombineRepository.findById(updateAnnotationRequest.getId());
    if(optional.isPresent()){
      AnnotationCombine annotationCombine=optional.get();
      String newAnnotation=AnnotationConvert.updateEntitiesAnnotation(annotationCombine.getFinalAnnotation(),
          updateAnnotationRequest.getTag(),updateAnnotationRequest.getNewType());
      return newAnnotation;
    }
    return "";
  }

  @Override
  public void test() {
    log.info("local-final");
  }

}
