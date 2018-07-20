package cn.malgo.annotation.vo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class AnnotationCombineBratVO {
  private long id;
  private int assignee;
  private String state;
  private int annotationType;

  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createdTime;

  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date lastModified;

  private String userName;

  private Long blockId;
  private String comment;

  private JSONObject finalAnnotation;
  private JSONObject reviewedAnnotation;
}
