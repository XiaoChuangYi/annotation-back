package cn.malgo.annotation.request.task;

import lombok.Data;

import java.util.Set;

@Data
public class CreateBlocksFromDocRequest {
  private final Set<Long> docIds;
  private final int annotationType;
}
