package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.AutoAnnotationRequest;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithm;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.exception.AlgorithmServiceException;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.service.AlgorithmApiService;
import cn.malgo.annotation.service.feigns.AlgorithmApiClient;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Created by cjl on 2018/5/31. */
@Service
@Slf4j
public class AlgorithmApiServiceImpl implements AlgorithmApiService {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final AlgorithmApiClient algorithmApiClient;

  @Autowired
  public AlgorithmApiServiceImpl(
      AnnotationCombineRepository annotationCombineRepository,
      AlgorithmApiClient algorithmApiClient) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.algorithmApiClient = algorithmApiClient;
  }

  @Value("${pro.serverApiUrl}")
  private String algorithmUrl;

  @Override
  public List<AutoAnnotation> listAutoAnnotationThroughAlgorithm(int anId) {
    Optional<AnnotationCombine> optional = annotationCombineRepository.findById(anId);
    if (optional.isPresent()) {
      List<AutoAnnotation> autoAnnotationList;
      AutoAnnotationRequest autoAnnotationRequest = new AutoAnnotationRequest();
      autoAnnotationRequest.setId(optional.get().getId());
      autoAnnotationRequest.setText(optional.get().getTerm().trim());
      try {
        log.info(
            "调用算法接口：{}的请求参数：{}",
            algorithmUrl + "/api/batch-mr-tokenize-pos",
            Arrays.asList(autoAnnotationRequest));
        autoAnnotationList =
            algorithmApiClient.listCasePrepareAnnotation(Arrays.asList(autoAnnotationRequest));
        log.info(
            "调用算法接口：{}的返回结果：{}", algorithmUrl + "/api/batch-mr-tokenize-pos", autoAnnotationList);
      } catch (Exception ex) {
        log.error(
            "调用算法接口：{}失败，错误原因：{}",
            algorithmUrl + "/api/batch-mr-tokenize-pos",
            ex.getLocalizedMessage());
        throw new AlgorithmServiceException("call-algorithm-api-failed", "调用算法后台预标注接口失败");
      }
      return autoAnnotationList;
    } else {
      return null;
    }
  }

  @Override
  public List<AutoAnnotation> listRecombineAnnotationThroughAlgorithm(
      UpdateAnnotationAlgorithm updateAnnotationAlgorithm) {
    List<AutoAnnotation> autoAnnotationList;
    try {
      List<UpdateAnnotationAlgorithm> updateAnnotationAlgorithmList =
          Arrays.asList(updateAnnotationAlgorithm);
      log.info(
          "调用算法接口：{}的请求参数：{}",
          algorithmUrl + "/api/batch-update-tokenize-pos",
          updateAnnotationAlgorithmList);
      autoAnnotationList =
          algorithmApiClient.batchUpdateAnnotationTokenizePos(updateAnnotationAlgorithmList);
      log.info(
          "调用算法接口：{}的返回结果：{}", algorithmUrl + "/api/batch-update-tokenize-pos", autoAnnotationList);
      if (autoAnnotationList == null || autoAnnotationList.get(0) == null) {
        throw new BusinessRuleException("null-response", "算法后台返回结果为null");
      }
    } catch (Exception ex) {
      log.error(
          "调用算法接口：{}失败，错误原因：{}",
          algorithmUrl + "/api/batch-update-tokenize-pos",
          ex.getLocalizedMessage());
      throw new AlgorithmServiceException("call-algorithm-api-failed", ex.getLocalizedMessage());
    }
    return autoAnnotationList;
  }
}
