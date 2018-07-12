package cn.malgo.annotation.utils.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RelationEntity {
  private String tag;
  private String type;
  private String sourceTag;
  private String targetTag;
  @Deprecated private String source;
  @Deprecated private String target;
}
