package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithm;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.AlgorithmServiceException;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InternalServiceException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.GetAutoAnnotationRequest;
import cn.malgo.annotation.service.AlgorithmApiService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AlgorithmAnnotationVO;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Created by cjl on 2018/5/31. */
@Component
@Slf4j
public class GetAutoAnnotationBiz extends BaseBiz<GetAutoAnnotationRequest, AlgorithmAnnotationVO> {

  private final AlgorithmApiService algorithmApiService;
  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public GetAutoAnnotationBiz(
      AlgorithmApiService algorithmApiService,
      AnnotationCombineRepository annotationCombineRepository) {
    this.algorithmApiService = algorithmApiService;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(GetAutoAnnotationRequest getAutoAnnotationRequest)
      throws InvalidInputException {
    if (getAutoAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的标注id");
    }
  }

  @Override
  protected void authorize(int userId, int role, GetAutoAnnotationRequest getAutoAnnotationRequest)
      throws BusinessRuleException {
    // 1管理员, 2审核, 3标注, 4练习
    if (role <= 0 || role > 4) {
      throw new InternalServiceException("invalid-role", role + " is not a valid role");
    }
  }

  private UpdateAnnotationAlgorithm getUpdateAnnotationAlgorithm(
      AnnotationCombine annotationCombine) {
    UpdateAnnotationAlgorithm updateAnnotationAlgorithm = new UpdateAnnotationAlgorithm();
    updateAnnotationAlgorithm.setId(annotationCombine.getId());
    updateAnnotationAlgorithm.setText(annotationCombine.getTerm());
    updateAnnotationAlgorithm.setNewTerms(Arrays.asList());
    updateAnnotationAlgorithm.setManualAnnotation(annotationCombine.getManualAnnotation());
    return updateAnnotationAlgorithm;
  }

  private AlgorithmAnnotationVO getWordAnnotationVO(int role, AnnotationCombine annotation) {
    if (annotation.getState().equals(AnnotationCombineStateEnum.preAnnotation.name())) {
      final UpdateAnnotationAlgorithm updateAnnotationAlgorithm =
          getUpdateAnnotationAlgorithm(annotation);
      final List<AutoAnnotation> finalAnnotationList =
          algorithmApiService.listRecombineAnnotationThroughAlgorithm(updateAnnotationAlgorithm);
      String autoAnnotation = null;
      if (finalAnnotationList != null
          && finalAnnotationList.size() > 0
          && finalAnnotationList.get(0) != null) {
        autoAnnotation = finalAnnotationList.get(0).getAnnotation();
        annotation.setFinalAnnotation(AnnotationConvert.addUncomfirmed(autoAnnotation));
        annotation.setManualAnnotation("");
        annotationCombineRepository.save(annotation);
      } else {
        log.warn("调用算法后台病历分词预标注接口: {}, {}", annotation.getId(), autoAnnotation);
        throw new AlgorithmServiceException("algorithm-response-error", "调用算法后台病历分词预标注接口，返回异常null");
      }
    }
    return new AlgorithmAnnotationVO(
        "", AnnotationConvert.convert2AnnotationCombineBratVO(annotation));
  }

  private AlgorithmAnnotationVO getSentenceAnnotationVO(int role, AnnotationCombine annotation) {
    // TODO 过后端自己的分句算法
    return new AlgorithmAnnotationVO(
        annotation.getFinalAnnotation(),
        AnnotationConvert.convert2AnnotationCombineBratVO(annotation));
  }

  private AlgorithmAnnotationVO getRelationAnnotationVO(int role, AnnotationCombine annotation) {
    // TODO 过算法的关联算法
    return new AlgorithmAnnotationVO(
        annotation.getFinalAnnotation(),
        AnnotationConvert.convert2AnnotationCombineBratVO(annotation));
  }

  @Override
  protected AlgorithmAnnotationVO doBiz(
      int userId, int role, GetAutoAnnotationRequest getAutoAnnotationRequest) {
    final Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(getAutoAnnotationRequest.getId());

    if (optional.isPresent()) {
      AnnotationCombine annotation = optional.get();
      if (role == AnnotationRoleStateEnum.admin.getRole()
          && !StringUtils.equalsAny(
              annotation.getState(),
              AnnotationCombineStateEnum.errorPass.name(),
              AnnotationCombineStateEnum.innerAnnotation.name(),
              AnnotationCombineStateEnum.examinePass.name())) {
        throw new BusinessRuleException("invalid-state", annotation.getState() + "不应该被管理员继续标注");
      }

      if (role == AnnotationRoleStateEnum.auditor.getRole()
          && !StringUtils.equalsAny(
              annotation.getState(),
              AnnotationCombineStateEnum.preExamine.name(),
              AnnotationCombineStateEnum.abandon.name())) {
        throw new BusinessRuleException("invalid-state", annotation.getState() + "不应该被审核人员继续标注");
      }

      if (role >= AnnotationRoleStateEnum.labelStaff.getRole()
          && !StringUtils.equalsAny(
              annotation.getState(),
              AnnotationCombineStateEnum.preAnnotation.name(),
              AnnotationCombineStateEnum.annotationProcessing.name())) {
        throw new BusinessRuleException("invalid-state", annotation.getState() + "不应该被标注人员继续标注");
      }

      if (role == AnnotationRoleStateEnum.admin.getRole()) {
        // 管理员永远返回已审核结果
        return new AlgorithmAnnotationVO(
            annotation.getReviewedAnnotation(),
            AnnotationConvert.convert2AnnotationCombineBratVO(annotation));
      }

      if (role == AnnotationRoleStateEnum.auditor.getRole()
          && StringUtils.equalsAny(
              annotation.getState(),
              AnnotationCombineStateEnum.preExamine.name(),
              AnnotationCombineStateEnum.abandon.name())) {

        // 审核人员，而且已经被标注过，直接返回最后的标注结果
        return new AlgorithmAnnotationVO(
            annotation.getAnnotationType() == 0
                ? annotation.getReviewedAnnotation()
                : annotation.getFinalAnnotation(),
            AnnotationConvert.convert2AnnotationCombineBratVO(annotation));
      }
      switch (AnnotationTypeEnum.getByValue(annotation.getAnnotationType())) {
        case wordPos:
          // 分词
          return getWordAnnotationVO(role, annotation);

        case sentence:
          return getSentenceAnnotationVO(role, annotation);

        case relation:
          return getRelationAnnotationVO(role, annotation);
      }
    }

    throw new InvalidInputException("invalid-id", getAutoAnnotationRequest.getId() + "不存在");
  }
}
