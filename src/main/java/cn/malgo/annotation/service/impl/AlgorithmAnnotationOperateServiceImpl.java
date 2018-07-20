package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithmRequest;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.AlgorithmApiService;
import cn.malgo.annotation.service.AnnotationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("algorithm")
@Slf4j
public class AlgorithmAnnotationOperateServiceImpl implements AnnotationOperateService {
  private final AnnotationCombineRepository annotationCombineRepository;
  private final AlgorithmApiService algorithmApiService;

  public AlgorithmAnnotationOperateServiceImpl(
      AnnotationCombineRepository annotationCombineRepository,
      AlgorithmApiService algorithmApiService) {
    this.algorithmApiService = algorithmApiService;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  private String handleAnnotationCombine(
      AnnotationCombine annotation, String manualAnnotation, String autoAnnotation) {
    annotation.setManualAnnotation(manualAnnotation);
    if (annotation.getStateEnum() == AnnotationCombineStateEnum.preAnnotation) {
      annotation.setState(AnnotationCombineStateEnum.annotationProcessing.name());
    }

    final UpdateAnnotationAlgorithmRequest updateAnnotationAlgorithmRequest =
        new UpdateAnnotationAlgorithmRequest(
            annotation.getId(),
            annotation.getTerm(),
            autoAnnotation,
            manualAnnotation,
            new ArrayList<>());
    log.info("过算法后台，最终输入参数：{}", updateAnnotationAlgorithmRequest);
    List<AutoAnnotation> finalAnnotationList =
        algorithmApiService.listRecombineAnnotationThroughAlgorithm(
            updateAnnotationAlgorithmRequest);

    if (finalAnnotationList != null
        && finalAnnotationList.size() > 0
        && finalAnnotationList.get(0) != null) {
      if (annotation.getStateEnum() == AnnotationCombineStateEnum.annotationProcessing) {
        annotation.setFinalAnnotation(finalAnnotationList.get(0).getAnnotation());
      } else {
        annotation.setReviewedAnnotation(finalAnnotationList.get(0).getAnnotation());
      }
      annotationCombineRepository.save(annotation);
      return finalAnnotationList.get(0).getAnnotation();
    }

    return "";
  }

  @Override
  public String addAnnotation(
      AnnotationCombine annotation, AddAnnotationRequest addAnnotationRequest) {
    final String autoAnnotation = addAnnotationRequest.getAutoAnnotation();
    final String manualAnnotation =
        AnnotationConvert.handleCrossAnnotation(
            annotation.getManualAnnotation(),
            addAnnotationRequest.getTerm(),
            addAnnotationRequest.getType(),
            addAnnotationRequest.getStartPosition(),
            addAnnotationRequest.getEndPosition());

    return handleAnnotationCombine(annotation, manualAnnotation, autoAnnotation);
  }

  @Override
  public String deleteAnnotation(AnnotationCombine annotation, DeleteAnnotationRequest request) {
    return addAnnotation(
        annotation,
        new AddAnnotationRequest(
            request.getId(),
            request.getTerm(),
            "Token",
            request.getStartPosition(),
            request.getEndPosition(),
            request.getAutoAnnotation()));
  }

  @Override
  public String updateAnnotation(AnnotationCombine annotation, UpdateAnnotationRequest request) {
    return addAnnotation(
        annotation,
        new AddAnnotationRequest(
            request.getId(),
            request.getTerm(),
            request.getNewType(),
            request.getStartPosition(),
            request.getEndPosition(),
            request.getAutoAnnotation()));
  }
}
