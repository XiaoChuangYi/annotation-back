package cn.malgo.annotation.service;

import cn.malgo.annotation.dto.UpdateAnnotationAlgorithm;
import cn.malgo.annotation.entity.AnnotationCombine;

/** Created by cjl on 2018/6/13. */
public interface ExtractAddAtomicTermService {

  UpdateAnnotationAlgorithm extractAndAddAtomicTerm(AnnotationCombine annotationCombine);
}
