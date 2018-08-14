package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationBlockActionEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public interface AnnotationBlockService {
  /**
   * 获取block，如果不存在则创建，同时会创建对应的{@link AnnotationNew}
   *
   * @param annotationType 标注类型
   * @param text 文本
   * @param createAnnotationNew 是否创建AnnotationNew
   * @return Block以及是否是新建的
   */
  Pair<AnnotationTaskBlock, Boolean> getOrCreateAnnotation(
      final AnnotationTypeEnum annotationType,
      final String text,
      final boolean createAnnotationNew);

  void saveAnnotation(final AnnotationNew annotationNew);

  /**
   * block状态更新之后，同步{@link cn.malgo.annotation.entity.AnnotationTaskBlock}和{@link
   * cn.malgo.annotation.entity.AnnotationTask}的状态
   *
   * @param block block
   */
  void updateTaskState(final AnnotationTaskBlock block);

  /**
   * 重置block到标注系统中，变为待分配或待审核状态
   *
   * @param block target block
   * @param action 操作
   */
  AnnotationNew resetBlock(
      final AnnotationTaskBlock block,
      final AnnotationBlockActionEnum action,
      final String comment);

  void saveAnnotationAll(final List<AnnotationNew> annotationNews);
}
