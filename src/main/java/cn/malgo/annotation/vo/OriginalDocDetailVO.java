package cn.malgo.annotation.vo;

import java.util.Date;
import java.util.List;
import lombok.Value;

@Value
public class OriginalDocDetailVO {
  private long docId;
  private Date docCreatedTime;
  private List<AnnotationTaskVO> annotationTaskVOList;
}
