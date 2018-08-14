package cn.malgo.annotation.request;

import lombok.Data;
import lombok.Value;

@Data
public class ListOverlapEntityRequest {
  private int taskId;
  private int pageIndex;
  private int pageSize;
}
