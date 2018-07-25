package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import org.apache.commons.lang3.tuple.Pair;

public interface OriginalDocService {
  Pair<OriginalDoc, Integer> createBlocks(OriginalDoc doc, AnnotationTypeEnum annotationType);
}
