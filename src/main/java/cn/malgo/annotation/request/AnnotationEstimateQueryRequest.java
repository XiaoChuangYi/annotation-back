package cn.malgo.annotation.request;

import java.util.Date;
import lombok.Data;

@Data
public class AnnotationEstimateQueryRequest {

  private int pageIndex;
  private int pageSize;
  private int taskId;
  private Date workDay;
  private int assignee;
}
