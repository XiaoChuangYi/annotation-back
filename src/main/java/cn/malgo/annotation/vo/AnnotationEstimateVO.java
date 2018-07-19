package cn.malgo.annotation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Date;
import lombok.Value;

@Value
public class AnnotationEstimateVO {
  private int taskId;
  private int assignee;
  private String accountName;
  private String taskName;

  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
  private Date workDay;

  private int totalBranch;
  private int totalWordNum;
  private int currentAnnotatedBranch;
  private int currentAnnotatedWordNum;
  private int restBranch;
  private int restWordNum;
  private int currentAbandonBranch;
  private int currentAbandonWordNum;
  private double inConformity;
}
