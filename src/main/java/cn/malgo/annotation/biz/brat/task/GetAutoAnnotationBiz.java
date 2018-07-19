package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.biz.brat.task.entities.BaseAnnotationBiz;
import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithmRequest;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.GetAutoAnnotationRequest;
import cn.malgo.annotation.service.AlgorithmApiService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AlgorithmAnnotationVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.DependencyServiceException;
import cn.malgo.service.exception.InternalServerException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
  protected void validateRequest(GetAutoAnnotationRequest request) throws InvalidInputException {
    if (request.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的标注id");
    }
  }

  private UpdateAnnotationAlgorithmRequest getUpdateAnnotationAlgorithm(
      AnnotationCombine annotationCombine) {
    UpdateAnnotationAlgorithmRequest updateAnnotationAlgorithmRequest = new UpdateAnnotationAlgorithmRequest();
    updateAnnotationAlgorithmRequest.setId(annotationCombine.getId());
    updateAnnotationAlgorithmRequest.setText(annotationCombine.getTerm());
    updateAnnotationAlgorithmRequest.setNewTerms(new ArrayList<>());
    updateAnnotationAlgorithmRequest.setManualAnnotation(annotationCombine.getManualAnnotation());
    return updateAnnotationAlgorithmRequest;
  }

  private AlgorithmAnnotationVO getWordAnnotationVO(AnnotationCombine annotation) {
    if (annotation.getState().equals(AnnotationCombineStateEnum.preAnnotation.name())) {
      final UpdateAnnotationAlgorithmRequest updateAnnotationAlgorithmRequest =
          getUpdateAnnotationAlgorithm(annotation);
      final List<AutoAnnotation> finalAnnotationList =
          algorithmApiService.listRecombineAnnotationThroughAlgorithm(updateAnnotationAlgorithmRequest);
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
        throw new DependencyServiceException("调用算法后台病历分词预标注接口，返回异常null");
      }
    }
    return new AlgorithmAnnotationVO(
        "", AnnotationConvert.convert2AnnotationCombineBratVO(annotation));
  }

  private AlgorithmAnnotationVO getSentenceAnnotationVO(AnnotationCombine annotation) {
    // TODO 过后端自己的分句算法
    return new AlgorithmAnnotationVO(
        annotation.getFinalAnnotation(),
        AnnotationConvert.convert2AnnotationCombineBratVO(annotation));
  }

  private AlgorithmAnnotationVO getRelationAnnotationVO(AnnotationCombine annotation) {
    // TODO 过算法的关联算法
    return new AlgorithmAnnotationVO(
        annotation.getFinalAnnotation(),
        AnnotationConvert.convert2AnnotationCombineBratVO(annotation));
  }

  @Override
  protected AlgorithmAnnotationVO doBiz(
      final GetAutoAnnotationRequest request, final UserDetails user) {
    final Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(request.getId());

    if (optional.isPresent()) {
      final AnnotationCombine annotation = optional.get();
      BaseAnnotationBiz.checkPermission(annotation, user);

      final AnnotationCombineStateEnum state =
          AnnotationCombineStateEnum.valueOf(annotation.getState());

      switch (state) {
        case preExamine:
        case abandon:
          if (!user.hasPermission(Permissions.EXAMINE)) {
            throw new BusinessRuleException("permission-denied", "无权限");
          }

          // 已经被标注过，直接返回最后的标注结果
          return new AlgorithmAnnotationVO(
              annotation.getAnnotationType() == 0
                  ? annotation.getReviewedAnnotation()
                  : annotation.getFinalAnnotation(),
              AnnotationConvert.convert2AnnotationCombineBratVO(annotation));

        case preAnnotation:
        case annotationProcessing:
          if (!user.hasPermission(Permissions.ANNOTATE)) {
            throw new BusinessRuleException("permission-denied", "无权限");
          }

          switch (AnnotationTypeEnum.getByValue(annotation.getAnnotationType())) {
            case wordPos:
              // 分词
              return getWordAnnotationVO(annotation);

            case sentence:
              return getSentenceAnnotationVO(annotation);

            case relation:
              return getRelationAnnotationVO(annotation);
          }

        default:
          throw new InternalServerException("未知状态");
      }
    }

    throw new InvalidInputException("invalid-id", request.getId() + "不存在");
  }
}
