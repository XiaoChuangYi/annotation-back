package cn.malgo.annotation.request.brat;

import lombok.Data;

@Data
public class DeleteAnnotationGroupRequest {
  private int id;
  private String tag;
  private int startPosition;
  private int endPosition;
  private String term;

  public DeleteAnnotationGroupRequest(
      int id, String tag, int startPosition, int endPosition, String term) {
    this.id = id;
    this.tag = tag;
    this.startPosition = startPosition;
    this.endPosition = endPosition;
    this.term = term;
  }

  private String reTag;

  public DeleteAnnotationGroupRequest(int id, String reTag) {
    this.id = id;
    this.reTag = reTag;
  }
}
