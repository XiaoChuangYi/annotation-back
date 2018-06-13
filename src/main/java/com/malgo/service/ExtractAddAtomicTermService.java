package com.malgo.service;

import com.malgo.dto.UpdateAnnotationAlgorithm;
import com.malgo.entity.AnnotationCombine;

/** Created by cjl on 2018/6/13. */
public interface ExtractAddAtomicTermService {

  UpdateAnnotationAlgorithm extractAndAddAtomicTerm(AnnotationCombine annotationCombine);
}
