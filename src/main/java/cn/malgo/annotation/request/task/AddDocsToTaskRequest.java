package cn.malgo.annotation.request.task;

import lombok.Value;

import java.util.Set;

@Value
public class AddDocsToTaskRequest {
  private final long id;
  private final Set<Long> docIds;
  private final int annotationType;
}
