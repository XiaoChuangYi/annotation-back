package cn.malgo.annotation.request.task;

import lombok.Value;

@Value
public class CreateTaskRequest {
  private final String name;
}
