package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import org.apache.commons.lang3.tuple.Pair;

public interface AnnotationBlockService {
  /**
   * 获取block，如果不存在则创建，同时会创建对应的{@link AnnotationCombine}
   *
   * @param annotationType 标注类型
   * @param text 文本
   * @return Block以及是否是新建的
   */
  Pair<AnnotationTaskBlock, Boolean> getOrCreateAnnotation(
      final AnnotationTypeEnum annotationType, final String text);
}
