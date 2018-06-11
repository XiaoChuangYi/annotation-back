package com.malgo.biz.brat.task;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.dto.AutoAnnotation;
import com.malgo.entity.AnnotationCombine;
import com.malgo.exception.AlgorithmServiceException;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.GetAutoAnnotationRequest;
import com.malgo.service.AlgorithmApiService;
import com.malgo.utils.AnnotationConvert;
import com.malgo.vo.AlgorithmAnnotationVO;
import com.malgo.vo.AnnotationCombineBratVO;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/5/31. */
@Component
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
      throws BusinessRuleException {}

  @Override
  protected AlgorithmAnnotationVO doBiz(GetAutoAnnotationRequest getAutoAnnotationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(getAutoAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      List<AutoAnnotation> autoAnnotationList =
          algorithmApiService.listAutoAnnotationThroughAlgorithm(getAutoAnnotationRequest.getId());
      if (autoAnnotationList != null && autoAnnotationList.size() > 0) {
        AutoAnnotation autoAnnotation = autoAnnotationList.get(0);
        if (autoAnnotation != null) {
          autoAnnotation.setAnnotation(
              AnnotationConvert.addUncomfirmed(autoAnnotation.getAnnotation()));
          annotationCombine.setFinalAnnotation(autoAnnotation.getAnnotation());
          AnnotationCombineBratVO annotationCombineBratVO =
              AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
          AlgorithmAnnotationVO algorithmAnnotationVO = new AlgorithmAnnotationVO();
          algorithmAnnotationVO.setAutoAnnotation(autoAnnotation.getAnnotation());
          algorithmAnnotationVO.setAnnotationCombineBratVO(annotationCombineBratVO);
          return algorithmAnnotationVO;
        } else {
          throw new AlgorithmServiceException(
              "algorithm-response-error", "调用算法后台病历分词预标注接口，返回异常null");
        }
      } else {
        throw new AlgorithmServiceException("algorithm-response-error", "调用算法后台病历分词预标注接口，返回异常null");
      }
    }
    return null;
  }
}
