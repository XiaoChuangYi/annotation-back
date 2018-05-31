package com.malgo.service.impl;

import com.alibaba.fastjson.JSON;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.dto.AutoAnnotation;
import com.malgo.dto.AutoAnnotationRequest;
import com.malgo.entity.AnnotationCombine;
import com.malgo.exception.AlgorithmServiceException;
import com.malgo.service.AlgorithmApiService;
import com.malgo.service.feigns.AlgorithmApiClient;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by cjl on 2018/5/31.
 */
@Service
@Slf4j
public class AlgorithmApiServiceImpl implements AlgorithmApiService {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final AlgorithmApiClient algorithmApiClient;

  @Autowired
  public AlgorithmApiServiceImpl(AnnotationCombineRepository annotationCombineRepository,
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
      autoAnnotationRequest.setText(optional.get().getTerm());
      try {
        log.info("调用算法接口：{}的请求参数：{}",algorithmUrl+"/api/batch-mr-tokenize",
            JSON.toJSONString(Arrays.asList(autoAnnotationRequest)));
        autoAnnotationList = algorithmApiClient
            .listCasePrepareAnnotation(Arrays.asList(autoAnnotationRequest));
        log.info("调用算法接口：{}的返回结果：{}", algorithmUrl+"/api/batch-mr-tokenize",
            JSON.toJSONString(autoAnnotationList));
      } catch (Exception ex) {
        log.error("调用算法接口：{}失败，错误原因：{}", algorithmUrl+"/api/batch-mr-tokenize", ex.getLocalizedMessage());
        throw new AlgorithmServiceException("call-algorithm-api-failed", "调用算法后台预标注接口失败");
      }
      return autoAnnotationList;
    } else {
      return null;
    }
  }
}
