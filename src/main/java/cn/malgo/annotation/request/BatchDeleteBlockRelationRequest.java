package cn.malgo.annotation.request;

import java.util.List;
import java.util.Set;
import lombok.Value;

@Value
public class BatchDeleteBlockRelationRequest {

  private final Set<BlockRelation> blockRelationSet;

  @Value
  public static class BlockRelation {

    private final long id;
    private final List<String> rTags;
  }
}
