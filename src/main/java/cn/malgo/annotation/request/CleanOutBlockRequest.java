package cn.malgo.annotation.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CleanOutBlockRequest {
  private long taskId;
}
