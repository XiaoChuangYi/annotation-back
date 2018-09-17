package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.request.anno.DesignateAnnotationRequest;
import cn.malgo.annotation.request.anno.ListAnnotationRequest;
import cn.malgo.annotation.request.OneKeyDesignateAnnotationRequest;
import cn.malgo.service.model.UserDetails;
import org.springframework.data.domain.Page;

public interface AnnotationService {
  /** 条件查询任务标注 */
  Page<AnnotationNew> listAnnotationNew(ListAnnotationRequest listAnnotationCombineRequest);

  /** 批量指派标注数据给特定用户 */
  void designateAnnotationNew(DesignateAnnotationRequest designateAnnotationRequest);

  void oneKeyDesignateAnnotationNew(OneKeyDesignateAnnotationRequest request);

  void annotationSingleCommit(UserDetails user, AnnotationNew annotationNew);
}
