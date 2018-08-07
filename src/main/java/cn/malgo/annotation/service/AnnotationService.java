package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.request.DesignateAnnotationRequest;
import cn.malgo.annotation.request.ListAnnotationRequest;
import cn.malgo.annotation.request.OneKeyDesignateAnnotationRequest;
import cn.malgo.annotation.request.RandomDesignateAnnotationRequest;
import org.springframework.data.domain.Page;

public interface AnnotationService {
  /** 条件查询任务标注 */
  Page<AnnotationNew> listAnnotationNew(ListAnnotationRequest listAnnotationCombineRequest);

  /** 批量指派标注数据给特定用户 */
  void designateAnnotationNew(DesignateAnnotationRequest designateAnnotationRequest);

  /** 随机批量指派标注数据给用户 */
  void randomDesignateAnnotationNew(
      RandomDesignateAnnotationRequest randomDesignateAnnotationRequest);

  void oneKeyDesignateAnnotationNew(OneKeyDesignateAnnotationRequest request);
}
