package cn.malgo.annotation.dto;

import lombok.Data;

@Data
public class AnnotationOverview {
  private long taskId;
  private String taskName;
  private int totalBranch; // 批次总条数
  private int totalWordNum; // 批次总字数
  private int annotatedBranch;
  private int annotatedWordNum; // 已标注字数
  private int restBranch;
  private int restWordNum;
  private double inConformity;
  private int abandonBranch;
  private int abandonWordNum;
}
