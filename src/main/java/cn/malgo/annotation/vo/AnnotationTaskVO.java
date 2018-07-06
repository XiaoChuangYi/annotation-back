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
}
