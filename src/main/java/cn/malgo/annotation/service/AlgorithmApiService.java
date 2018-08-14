package cn.malgo.annotation.service;

import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithmRequest;

import java.util.List;

public interface AlgorithmApiService {
  List<AutoAnnotation> listRecombineAnnotationThroughAlgorithm(
      UpdateAnnotationAlgorithmRequest request);
}
