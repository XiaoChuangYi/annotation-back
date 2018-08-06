package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.request.DesignateAnnotationRequest;
import cn.malgo.annotation.request.ListAnnotationRequest;
import cn.malgo.annotation.request.RandomDesignateAnnotationRequest;
import org.springframework.data.domain.Page;

public interface AnnotationService {
  /** 条件查询任务标注 */
  Page<AnnotationNew> listAnnotationCombine(ListAnnotationRequest listAnnotationCombineRequest);

  /** 批量指派标注数据给特定用户 */
  void designateAnnotationCombine(DesignateAnnotationRequest designateAnnotationRequest);

  /** 随机批量指派标注数据给用户 */
  void randomDesignateAnnotationCombine(
      RandomDesignateAnnotationRequest randomDesignateAnnotationRequest);
}
