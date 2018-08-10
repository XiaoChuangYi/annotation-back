package cn.malgo.annotation.request;

import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.Value;

@Data
public class BatchDeleteEntityMultipleRequest {

  private final Set<EntityMultipleType> entityMultipleTypeSet;

  @Data
  public static class EntityMultipleType {

    private final long id;
    private final List<String> tags;
  }
}
