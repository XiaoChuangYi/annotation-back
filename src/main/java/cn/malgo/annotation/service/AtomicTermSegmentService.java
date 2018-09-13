package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AtomicTerm;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.core.definition.Entity;
import java.util.List;

public interface AtomicTermSegmentService {
  List<Entity> seg(final AnnotationTypeEnum annotationType, final String text);

  void addAtomicTerms(final AnnotationTypeEnum annotationType, final List<AtomicTerm> newTerms);
}
