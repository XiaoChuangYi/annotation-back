package cn.malgo.annotation.service;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.FixAnnotationEntity;
import cn.malgo.annotation.dto.error.FixAnnotationErrorContext;
import cn.malgo.annotation.dto.error.FixAnnotationErrorData;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.core.definition.Entity;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public interface AnnotationErrorProvider {
  AnnotationErrorEnum getErrorEnum();

  List<AlgorithmAnnotationWordError> find(List<Annotation> annotations);

  default List<Entity> fix(
      Annotation annotation, int start, int end, List<FixAnnotationEntity> entities)
      throws InvalidInputException {
    throw new NotImplementedException(getClass() + " doesn't have fix function");
  }

  default List<Entity> fix(Annotation annotation, int start, int end, FixAnnotationErrorData data)
      throws InvalidInputException {
    return fix(annotation, start, end, data.getEntities());
  }

  default List<Entity> fix(
      Annotation annotation, FixAnnotationErrorContext context, FixAnnotationErrorData data)
      throws InvalidInputException {
    return fix(annotation, context.getStart(), context.getEnd(), data);
  }
}
