package cn.malgo.annotation.vo;

import cn.malgo.annotation.entity.OriginalDoc;
import java.util.List;
import lombok.Value;

@Value
public class CreateBlocksFromDocVO {
  private final List<OriginalDoc> docs;
  private final int createdBlocks;
}
