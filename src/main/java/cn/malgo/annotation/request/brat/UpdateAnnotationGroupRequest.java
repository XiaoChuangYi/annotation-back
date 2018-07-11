package cn.malgo.annotation.request.brat;

import lombok.Data;

@Data
public class UpdateAnnotationGroupRequest {
  private int id;
  private String tag;
  private String newType;
  private int startPosition;
  private int endPosition;
  private String term;

  public UpdateAnnotationGroupRequest(
      int id, String tag, String newType, int startPosition, int endPosition, String term) {
    this.id = id;
    this.tag = tag;
    this.newType = newType;
    this.startPosition = startPosition;
    this.endPosition = endPosition;
    this.term = term;
  }

  private String reTag;
  private String relation;

  public UpdateAnnotationGroupRequest(int id, String reTag, String relation) {
    this.id = id;
    this.reTag = reTag;
    this.relation = relation;
  }
}
