package cn.malgo.annotation.vo;

import com.alibaba.fastjson.annotation.JSONField;
import java.sql.Date;
import lombok.Value;

@Value
public class AnnotationEstimateVO {
  private long taskId;
  private long assignee;
  private String accountName;
  private String taskName;

  @JSONField(format = "yyyy-MM-dd")
  private Date workDay;

  private int totalBranch;
  private int totalWordNum;
  private int currentAnnotatedBranch;
  private int currentAnnotatedWordNum;
  private int restBranch;
  private int restWordNum;
  private int currentAbandonBranch;
  private int currentAbandonWordNum;

  private Double preciseRate;
  private Double recallRate;
}
