package com.malgo.service.impl;

import cn.malgo.core.definition.Entity;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.dto.AutoAnnotation;
import com.malgo.dto.UpdateAnnotationAlgorithm;
import com.malgo.entity.AnnotationCombine;
import com.malgo.exception.BusinessRuleException;
import com.malgo.request.brat.AddAnnotationRequest;
import com.malgo.request.brat.DeleteAnnotationRequest;
import com.malgo.request.brat.UpdateAnnotationRequest;
import com.malgo.service.AnnotationOperateService;
import com.malgo.service.AlgorithmApiService;
import com.malgo.utils.AnnotationConvert;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by cjl on 2018/5/31.
 */
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

  private AnnotationCombine getAnnotationCombine(int id) {
    Optional<AnnotationCombine> optional = annotationCombineRepository.findById(id);
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      if (annotationCombine.getAnnotationType() == 0) // 判断，annotationType=0为分词标注，继续处理
      {
        return annotationCombine;
      }
      throw new BusinessRuleException("no-permission-handle", "当前操作无法处理该类型");
    } else {
      throw new BusinessRuleException("no-such-element", "没有该条记录");
    }
  }

  private String handleAnnotationCombine(
      int id, AnnotationCombine annotationCombine, String manualAnnotation, String autoAnnotation) {
    // 手动标注入库
    annotationCombine.setManualAnnotation(manualAnnotation);
    annotationCombine = annotationCombineRepository.save(annotationCombine);
    // 最终标注不入库
    UpdateAnnotationAlgorithm updateAnnotationAlgorithm = new UpdateAnnotationAlgorithm();
    updateAnnotationAlgorithm.setId(id);
    updateAnnotationAlgorithm.setText(annotationCombine.getTerm());
    updateAnnotationAlgorithm.setAutoAnnotation(autoAnnotation);
    updateAnnotationAlgorithm.setManualAnnotation(manualAnnotation);
    updateAnnotationAlgorithm.setNewTerms(Arrays.asList());
    log.info("过算法后台，最终输入参数：{}", updateAnnotationAlgorithm);
    List<AutoAnnotation> finalAnnotationList =
        algorithmApiService.listRecombineAnnotationThroughAlgorithm(updateAnnotationAlgorithm);
    if (finalAnnotationList != null
        && finalAnnotationList.size() > 0
        && finalAnnotationList.get(0) != null) {
      return finalAnnotationList.get(0).getAnnotation();
    }
    return "";
  }

  @Override
  public String addAnnotation(AddAnnotationRequest addAnnotationRequest, int roleId) {
    AnnotationCombine annotationCombine = getAnnotationCombine(addAnnotationRequest.getId());
    String manualAnnotation = annotationCombine.getManualAnnotation();
    String autoAnnotation = addAnnotationRequest.getAutoAnnotation();
    manualAnnotation =
        AnnotationConvert.handleCrossAnnotation(
            manualAnnotation,
            addAnnotationRequest.getTerm(),
            addAnnotationRequest.getType(),
            addAnnotationRequest.getStartPosition(),
            addAnnotationRequest.getEndPosition());
    String annotation =
        handleAnnotationCombine(
            addAnnotationRequest.getId(), annotationCombine, manualAnnotation, autoAnnotation);
    return annotation;
  }

  @Override
  public String deleteAnnotation(DeleteAnnotationRequest deleteAnnotationRequest) {
    Entity entity = AnnotationConvert
        .getEntityFromAnnotation(deleteAnnotationRequest.getAutoAnnotation(),
            deleteAnnotationRequest.getTag());
    AddAnnotationRequest addAnnotationRequest = new AddAnnotationRequest();
    addAnnotationRequest.setId(deleteAnnotationRequest.getId());
    addAnnotationRequest.setStartPosition(entity.getStart());
    addAnnotationRequest.setAutoAnnotation(deleteAnnotationRequest.getAutoAnnotation());
    addAnnotationRequest.setEndPosition(entity.getEnd());
    addAnnotationRequest.setTerm(entity.getTerm());
    addAnnotationRequest.setType("Token");
    return addAnnotation(addAnnotationRequest, 0);
  }

  @Override
  public String updateAnnotation(UpdateAnnotationRequest updateAnnotationRequest) {
    Entity entity = AnnotationConvert
        .getEntityFromAnnotation(updateAnnotationRequest.getAutoAnnotation(), updateAnnotationRequest.getTag());
    AddAnnotationRequest addAnnotationRequest = new AddAnnotationRequest();
    addAnnotationRequest.setId(updateAnnotationRequest.getId());
    addAnnotationRequest.setTerm(entity.getTerm());
    addAnnotationRequest.setType(updateAnnotationRequest.getNewType());
    addAnnotationRequest.setStartPosition(entity.getStart());
    addAnnotationRequest.setEndPosition(entity.getEnd());
    addAnnotationRequest.setAutoAnnotation(updateAnnotationRequest.getAutoAnnotation());
    return addAnnotation(addAnnotationRequest,0);
  }
}
