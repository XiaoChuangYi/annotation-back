package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithm;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationOperateService;
import cn.malgo.annotation.service.AlgorithmApiService;
import cn.malgo.annotation.utils.AnnotationConvert;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Created by cjl on 2018/5/31. */
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
      int id,
      AnnotationCombine annotationCombine,
      String manualAnnotation,
      String autoAnnotation,
      int roleId) {
    // 手动标注入库
    annotationCombine.setManualAnnotation(manualAnnotation);
    if (roleId >= AnnotationRoleStateEnum.labelStaff.getRole()) {
      annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
    }
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
      if (roleId >= AnnotationRoleStateEnum.labelStaff.getRole()) {
        annotationCombine.setFinalAnnotation(finalAnnotationList.get(0).getAnnotation());
      } else {
        annotationCombine.setReviewedAnnotation(finalAnnotationList.get(0).getAnnotation());
      }
      annotationCombineRepository.save(annotationCombine);
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
            addAnnotationRequest.getId(),
            annotationCombine,
            manualAnnotation,
            autoAnnotation,
            roleId);
    return annotation;
  }

  @Override
  public String deleteAnnotation(DeleteAnnotationRequest deleteAnnotationRequest, int roleId) {
    AddAnnotationRequest addAnnotationRequest = new AddAnnotationRequest();
    addAnnotationRequest.setId(deleteAnnotationRequest.getId());
    addAnnotationRequest.setStartPosition(deleteAnnotationRequest.getStartPosition());
    addAnnotationRequest.setAutoAnnotation(deleteAnnotationRequest.getAutoAnnotation());
    addAnnotationRequest.setEndPosition(deleteAnnotationRequest.getEndPosition());
    addAnnotationRequest.setTerm(deleteAnnotationRequest.getTerm());
    addAnnotationRequest.setType("Token");
    return addAnnotation(addAnnotationRequest, roleId);
  }

  @Override
  public String updateAnnotation(UpdateAnnotationRequest updateAnnotationRequest, int roleId) {
    AddAnnotationRequest addAnnotationRequest = new AddAnnotationRequest();
    addAnnotationRequest.setId(updateAnnotationRequest.getId());
    addAnnotationRequest.setTerm(updateAnnotationRequest.getTerm());
    addAnnotationRequest.setType(updateAnnotationRequest.getNewType());
    addAnnotationRequest.setStartPosition(updateAnnotationRequest.getStartPosition());
    addAnnotationRequest.setEndPosition(updateAnnotationRequest.getEndPosition());
    addAnnotationRequest.setAutoAnnotation(updateAnnotationRequest.getAutoAnnotation());
    return addAnnotation(addAnnotationRequest, roleId);
  }
}
