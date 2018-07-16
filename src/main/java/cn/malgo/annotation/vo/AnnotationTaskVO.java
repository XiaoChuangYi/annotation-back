package cn.malgo.annotation.vo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationTaskVO {

  private int id;
  private Date createdTime;
  private Date lastModifiedTime;
  private String name;
  private String state;

  //  public AnnotationTaskVO(
  //      int id, Date createdTime, Date lastModifiedTime, String name, String state) {
  //    this.id = id;
  //    this.createdTime = createdTime;
  //    this.lastModifiedTime = lastModifiedTime;
  //    this.name = name;
  //    this.state = state;
  //  }
}
