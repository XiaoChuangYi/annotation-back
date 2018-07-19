package cn.malgo.annotation.request;

import java.sql.Date;
import lombok.Data;

@Data
public class AnnotationEstimateQueryRequest {
  private int pageIndex;
  private int pageSize;
  private long taskId;
  private Date workDay;
  private long assignee;
}
