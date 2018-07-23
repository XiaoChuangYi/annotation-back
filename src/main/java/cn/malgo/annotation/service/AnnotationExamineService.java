package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationCombine;
import java.util.List;

public interface AnnotationExamineService {

  List<Long> batchAnnotationExamine(List<AnnotationCombine> annotationCombines);

  Long singleAnnotationExamine(AnnotationCombine annotationCombine);
}
