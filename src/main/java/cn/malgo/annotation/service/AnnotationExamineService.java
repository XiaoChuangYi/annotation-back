package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationNew;
import java.util.List;

public interface AnnotationExamineService {

  List<Long> batchAnnotationExamine(List<AnnotationNew> annotationNews);

  Long singleAnnotationExamine(AnnotationNew annotationNew);
}
