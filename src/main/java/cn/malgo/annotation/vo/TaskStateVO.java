package cn.malgo.annotation.vo;

import lombok.Value;

@Value
public class TaskStateVO {

  private int blockDoing;
  private int docImported;
  private int docProcessing;
  private int docProcessed;
}
