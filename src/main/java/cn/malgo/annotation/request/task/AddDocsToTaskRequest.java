package cn.malgo.annotation.request.task;

import lombok.Value;

import java.util.Set;

@Value
public class AddDocsToTaskRequest {
  private final int id;
  private final Set<Integer> docIds;
  private final int annotationType;
}
