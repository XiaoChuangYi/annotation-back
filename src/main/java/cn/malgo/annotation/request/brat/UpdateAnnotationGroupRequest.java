package cn.malgo.annotation.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
public class UpdateAnnotationGroupRequest implements BaseAnnotationRequest {
  private int id;
  private String tag;
  private String newType;
  private int startPosition;
  private int endPosition;
  private String term;

  private String reTag;
  private String relation;

  public boolean isUpdatingEntity() {
    return StringUtils.isBlank(reTag);
  }
}
