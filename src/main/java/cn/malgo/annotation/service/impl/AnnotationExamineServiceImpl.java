package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.service.AnnotationExamineService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnnotationExamineServiceImpl implements AnnotationExamineService {

  @Override
  public List<Long> batchAnnotationExamine(List<AnnotationNew> annotationNews) {
    final List<Long> forbidList = new ArrayList<>();
    annotationNews
        .stream()
        .forEach(
            annotationNew -> {
              if (handleCurrentAnnotation(annotationNew).intValue() > 0) {
                log.info("id为{},状态为{}的标注无法被审核提交", annotationNew.getId(), annotationNew.getState());
                forbidList.add(annotationNew.getId());
              }
            });
    return forbidList;
  }

  @Override
  public Long singleAnnotationExamine(AnnotationNew annotationNew) {
    return handleCurrentAnnotation(annotationNew);
  }

  private Long handleCurrentAnnotation(AnnotationNew annotationNew) {
    return Long.valueOf(0);
  }
}
