package cn.malgo.annotation.request;

import java.util.Set;
import lombok.Value;

@Value
public class BatchDeleteEntityMultipleRequest {

  private final Set<EntityMultipleType> entityMultipleTypeSet;

  @Value
  public static class EntityMultipleType {

    private final long id;
    private final Set<String> tags;
  }
}
