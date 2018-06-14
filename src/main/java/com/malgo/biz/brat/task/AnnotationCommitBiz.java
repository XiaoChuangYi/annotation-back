package com.malgo.biz.brat.task;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.dto.AutoAnnotation;
import com.malgo.dto.UpdateAnnotationAlgorithm;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.CommitAnnotationRequest;
import com.malgo.service.AlgorithmApiService;
import com.malgo.service.ExtractAddAtomicTermService;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/1. */
@Component
public class AnnotationCommitBiz extends BaseBiz<CommitAnnotationRequest, Object> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final AlgorithmApiService algorithmApiService;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;

  @Autowired
  public AnnotationCommitBiz(
      AlgorithmApiService algorithmApiService,
      AnnotationCombineRepository annotationCombineRepository,
      ExtractAddAtomicTermService extractAddAtomicTermService) {
    this.algorithmApiService = algorithmApiService;
    this.annotationCombineRepository = annotationCombineRepository;
    this.extractAddAtomicTermService = extractAddAtomicTermService;
  }

  @Override
  protected void validateRequest(CommitAnnotationRequest commitAnnotationRequest)
      throws InvalidInputException {
    if (commitAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (commitAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected void authorize(int userId, int role, CommitAnnotationRequest commitAnnotationRequest)
      throws BusinessRuleException {
    if (role > 2) {
      Optional<AnnotationCombine> optional =
          annotationCombineRepository.findById(commitAnnotationRequest.getId());
      if (optional.isPresent()) {
        AnnotationCombine annotationCombine = optional.get();
        if (userId != annotationCombine.getAssignee()) {
          throw new BusinessRuleException("no-permission-commit-current-record", "当前用户没有权限提交该条记录");
        }
        if (annotationCombine.getAnnotationType() == 0) {
          if (StringUtils.isBlank(commitAnnotationRequest.getAutoAnnotation())) {
            throw new InvalidInputException("invalid-autoAnnotation", "参数autoAnnotation为空！");
          }
        }
      }
    }
  }

  @Override
  protected Object doBiz(CommitAnnotationRequest commitAnnotationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(commitAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      if (annotationCombine.getAnnotationType() == 0) { // 分词标注提交
        //        String manualAnnotation = annotationCombine.getManualAnnotation();
        //        List<Entity> entities =
        // AnnotationConvert.getEntitiesFromAnnotation(manualAnnotation);
        //        List<AtomicTerm> atomicTermList = atomicTermRepository.findAll();
        //        Iterator<Entity> iterator = entities.iterator();
        //        while (iterator.hasNext()) {
        //          Entity current = iterator.next();
        //          if (atomicTermList
        //                  .stream()
        //                  .filter(
        //                      atomicTerm ->
        //                          current.getType().equals(atomicTerm.getAnnotationType())
        //                              && current.getTerm().equals(atomicTerm.getTerm()))
        //                  .count()
        //              > 0) {
        //            iterator.remove();
        //          }
        //        }
        //        // todo,当然还有其它的过滤规则
        //        UpdateAnnotationAlgorithm updateAnnotationAlgorithm = new
        // UpdateAnnotationAlgorithm();
        //        updateAnnotationAlgorithm.setId(commitAnnotationRequest.getId());
        //
        // updateAnnotationAlgorithm.setAutoAnnotation(commitAnnotationRequest.getAutoAnnotation());
        //        updateAnnotationAlgorithm.setText(annotationCombine.getTerm());
        //        updateAnnotationAlgorithm.setManualAnnotation(manualAnnotation);
        //        if (entities.size() > 0) {
        //          List<NewTerm> newTermList =
        //              IntStream.range(0, entities.size())
        //                  .mapToObj(
        //                      (int i) -> new NewTerm(entities.get(i).getTerm(),
        // entities.get(i).getType()))
        //                  .collect(Collectors.toList());
        //          updateAnnotationAlgorithm.setNewTerms(newTermList);
        //          List<AtomicTerm> atomicTerms =
        //              IntStream.range(0, entities.size())
        //                  .mapToObj(
        //                      (int i) ->
        //                          new AtomicTerm(
        //                              entities.get(i).getTerm(),
        //                              entities.get(i).getType(),
        //                              commitAnnotationRequest.getId()))
        //                  .collect(Collectors.toList());
        //          atomicTermRepository.saveAll(atomicTerms);
        //        } else {
        //          updateAnnotationAlgorithm.setNewTerms(Arrays.asList());
        //        }
        UpdateAnnotationAlgorithm updateAnnotationAlgorithm =
            extractAddAtomicTermService.extractAndAddAtomicTerm(annotationCombine);
        updateAnnotationAlgorithm.setAutoAnnotation(commitAnnotationRequest.getAutoAnnotation());
        List<AutoAnnotation> autoAnnotationList =
            algorithmApiService.listRecombineAnnotationThroughAlgorithm(updateAnnotationAlgorithm);
        if (autoAnnotationList == null || autoAnnotationList.get(0) == null) {
          throw new BusinessRuleException("null-response", "调用算法后台数据返回null");
        }
        annotationCombine.setState(AnnotationCombineStateEnum.preExamine.name());
        annotationCombine.setFinalAnnotation(autoAnnotationList.get(0).getAnnotation());
        annotationCombine.setManualAnnotation("");
        annotationCombine.setReviewedAnnotation(annotationCombine.getFinalAnnotation());
        annotationCombineRepository.save(annotationCombine);
      } else {
        // 分句，关联提交
        annotationCombine.setState(AnnotationCombineStateEnum.preExamine.name());
        annotationCombine.setFinalAnnotation(annotationCombine.getManualAnnotation());
        annotationCombine.setReviewedAnnotation(annotationCombine.getFinalAnnotation());
        annotationCombineRepository.save(annotationCombine);
      }
    }
    return null;
  }
}
