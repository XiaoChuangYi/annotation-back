package cn.malgo.annotation.vo;

import java.util.Date;
import lombok.Data;

@Data
public class AnnotationEstimateVO {
  private int taskId;
  private int assignee;
  private String accountName;
  private Date gmtModified;
  private String taskName;
  private int totalBranch;
  private int totalWordNum;
  private int finishWordNum;
  private int restWordNum;
  private int finishBranch;
  private int restBranch;
  private double inConformity;
}
