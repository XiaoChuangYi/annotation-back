package cn.malgo.annotation.request.block;

import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.Value;

@Data
public class BatchDeleteBlockRelationRequest {

  private final Set<BlockRelation> blockRelationSet;

  @Data
  public static class BlockRelation {

    private final long id;
    private final List<String> rTags;
  }
}
