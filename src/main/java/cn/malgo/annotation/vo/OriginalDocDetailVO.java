package cn.malgo.annotation.vo;

import java.util.Date;
import java.util.List;
import lombok.Value;

@Value
public class OriginalDocDetailVO {
  private int docId;
  private Date docCreatedTime;
  private List<AnnotationTaskVO> annotationTaskVOList;
}
