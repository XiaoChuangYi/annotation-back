package cn.malgo.annotation.vo;

import cn.malgo.core.definition.brat.BratPosition;
import lombok.Value;

@Value
public class RelationSearchResponse {
  private AnnotationBlockBratVO annotationBlockBratVO;
  private BratPosition bratPosition;
}
