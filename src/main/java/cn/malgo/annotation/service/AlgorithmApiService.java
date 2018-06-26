package cn.malgo.annotation.service;

import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithm;
import java.util.List;

/** Created by cjl on 2018/5/31. */
public interface AlgorithmApiService {
  List<AutoAnnotation> listAutoAnnotationThroughAlgorithm(int anId);

  List<AutoAnnotation> listRecombineAnnotationThroughAlgorithm(
      UpdateAnnotationAlgorithm updateAnnotationAlgorithm);
}
