package cn.malgo.annotation.request;

import lombok.Data;

@Data
public class PersonalTaskSummaryRecordRequest {
  private long assigneeId;
  private long taskId;
  private int precisionRate;
}
