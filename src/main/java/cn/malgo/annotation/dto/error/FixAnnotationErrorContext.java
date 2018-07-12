package cn.malgo.annotation.dto.error;

public interface FixAnnotationErrorContext {
  int getStart();

  int getEnd();

  Object getInfo();
}
