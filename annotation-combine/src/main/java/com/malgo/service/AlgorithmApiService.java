package com.malgo.service;

import com.malgo.dto.AutoAnnotation;
import com.malgo.dto.UpdateAnnotationAlgorithm;
import java.util.List;

/**
 * Created by cjl on 2018/5/31.
 */
public interface AlgorithmApiService {
  List<AutoAnnotation> listAutoAnnotationThroughAlgorithm(int anId);

  List<AutoAnnotation> listRecombineAnnotationThroughAlgorithm(UpdateAnnotationAlgorithm updateAnnotationAlgorithm);
}
