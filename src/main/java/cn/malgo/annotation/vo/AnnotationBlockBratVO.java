package cn.malgo.annotation.vo;

import com.alibaba.fastjson.JSONObject;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationBlockBratVO {

  private long id;
  private JSONObject annotation;
  private int annotationType;

  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createdTime;

  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date lastModified;

  private String state;
  private String text;
  private long assigneeId;

  public AnnotationBlockBratVO(
      long id,
      JSONObject annotation,
      int annotationType,
      Date createdTime,
      Date lastModified,
      String state,
      String text,
      long assigneeId) {
    this.id = id;
    this.annotation = annotation;
    this.annotationType = annotationType;
    this.createdTime = createdTime;
    this.lastModified = lastModified;
    this.state = state;
    this.text = text;
    this.assigneeId = assigneeId;
  }

  private String assignee;
}
