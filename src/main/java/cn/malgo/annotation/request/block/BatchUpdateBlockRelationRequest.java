package cn.malgo.annotation.request.block;

import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class BatchUpdateBlockRelationRequest {

  private final Set<BlockRelationUpdate> blockRelationSet;
  private final String newType;

  @Data
  public static class BlockRelationUpdate {

    private final long id;
    private final List<String> rTags;
  }
}
