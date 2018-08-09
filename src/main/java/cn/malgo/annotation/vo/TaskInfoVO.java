package cn.malgo.annotation.vo;

import lombok.Value;

@Value
public class TaskInfoVO {

  private long taskId;
  private String taskName;
  private String taskState;
}
