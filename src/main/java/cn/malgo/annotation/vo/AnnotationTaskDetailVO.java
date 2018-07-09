package cn.malgo.annotation.vo;

import java.util.List;
import lombok.Value;

@Value
public class AnnotationTaskDetailVO {
  private String name; // 任务名称
  private String state; // 任务状态
  private List<OriginalDocVO> originalDocVOList;
}
