package cn.malgo.annotation.request.block;

import lombok.Data;

@Data
public class ListRelevanceAnnotationRequest {
  private int pageIndex;
  private int pageSize;
  private String sourceType;
  private String targetType;
  private String sourceText;
  private String targetText;
  private String relation;
}
