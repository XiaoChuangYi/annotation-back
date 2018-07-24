package cn.malgo.annotation.request;

import lombok.Value;

@Value
public class ListOverlapEntityRequest {
  private int taskId;
  private int pageIndex;
  private int pageSize;
}
