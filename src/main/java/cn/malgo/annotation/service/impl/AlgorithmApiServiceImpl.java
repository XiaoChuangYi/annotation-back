package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithmRequest;
import cn.malgo.annotation.service.AlgorithmApiService;
import cn.malgo.annotation.service.feigns.AlgorithmApiClient;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.DependencyServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class AlgorithmApiServiceImpl implements AlgorithmApiService {
  private final String algorithmUrl;
  private final AlgorithmApiClient algorithmApiClient;

  @Autowired
  public AlgorithmApiServiceImpl(
      @Value("${pro.serverApiUrl}") final String algorithmUrl,
      AlgorithmApiClient algorithmApiClient) {
    this.algorithmUrl = algorithmUrl;
    this.algorithmApiClient = algorithmApiClient;
  }

  @Override
  public List<AutoAnnotation> listRecombineAnnotationThroughAlgorithm(
      UpdateAnnotationAlgorithmRequest request) {
    List<AutoAnnotation> autoAnnotationList;

    try {
      List<UpdateAnnotationAlgorithmRequest> data = Collections.singletonList(request);
      log.info("调用算法接口：{}的请求参数：{}", algorithmUrl + "/api/batch-update-tokenize-pos", data);
      autoAnnotationList = algorithmApiClient.batchUpdateAnnotationTokenizePos(data);
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
      throw new DependencyServiceException(
          "call-algorithm-api-failed: " + ex.getLocalizedMessage());
    }

    return autoAnnotationList;
  }
}
