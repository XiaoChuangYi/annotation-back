package cn.malgo.annotation.vo;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class PersonalTaskRankSummaryVO {

  private long id;

  private long taskId;

  private long assigneeId;

  private int annotatedTotalWordNum;

  private double precisionRate;

  private double recallRate;

  private Date createdTime;

  private Date lastModified;

  private BigDecimal payment;

  private int totalWordNum;
}
