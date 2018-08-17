package cn.malgo.annotation.service;

import cn.malgo.annotation.dto.UpdateAnnotationAlgorithmRequest;
import cn.malgo.annotation.entity.AnnotationNew;
import java.util.List;

/** Created by cjl on 2018/6/13. */
public interface ExtractAddAtomicTermService {

  UpdateAnnotationAlgorithmRequest extractAndAddAtomicTerm(AnnotationNew annotationNew);
}
