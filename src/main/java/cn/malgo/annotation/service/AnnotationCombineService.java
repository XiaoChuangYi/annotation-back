package cn.malgo.annotation.service;

import cn.malgo.annotation.request.DesignateAnnotationRequest;
import cn.malgo.annotation.request.ListAnnotationCombineRequest;
import cn.malgo.annotation.request.RandomDesignateAnnotationRequest;
import org.springframework.data.domain.Page;

/** Created by cjl on 2018/5/29. */
public interface AnnotationCombineService {

  /** 条件查询任务标注 */
  Page listAnnotationCombine(ListAnnotationCombineRequest listAnnotationCombineRequest);

  /** 批量指派标注数据给特定用户 */
  void designateAnnotationCombine(DesignateAnnotationRequest designateAnnotationRequest);

  /** 随机批量指派标注数据给用户 */
  void randomDesignateAnnotationCombine(
      RandomDesignateAnnotationRequest randomDesignateAnnotationRequest);
}
