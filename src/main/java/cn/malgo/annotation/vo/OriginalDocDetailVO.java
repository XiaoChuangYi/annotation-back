package cn.malgo.annotation.vo;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Value;

@Value
public class OriginalDocDetailVO {
  private long docId;

  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date docCreatedTime;

  private List<AnnotationTaskVO> annotationTaskVOList;
}
