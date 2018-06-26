package cn.malgo.annotation.service;

import cn.malgo.core.definition.Entity;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.FixAnnotationEntity;
import cn.malgo.annotation.exception.InvalidInputException;

import java.util.List;

public interface FixAnnotationErrorService {
  /**
   * 在annotation中将包含[start, end]的所有标注修改为新的entities
   *
   * @param annotation 标注数据
   * @param start 起始位置
   * @param end 结束位置（包含）
   * @param entities 修改的新的entities
   * @return 返回修改过的entities
   * @throws {@link InvalidInputException} 如果修复不成功
   */
  List<Entity> fixAnnotation(
      Annotation annotation, int start, int end, List<FixAnnotationEntity> entities);
}
