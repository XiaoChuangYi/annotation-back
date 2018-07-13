package cn.malgo.annotation.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
public class AddAnnotationGroupRequest implements BaseAnnotationRequest {
  private int id;
  private String term;
  private String type;
  private int startPosition;
  private int endPosition;
  private String sourceTag;
  private String targetTag;
  private String relation;

  public boolean isAddEntity() {
    return StringUtils.isAllBlank(this.sourceTag, this.targetTag, this.relation);
  }
}
