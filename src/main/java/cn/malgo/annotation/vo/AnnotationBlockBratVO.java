package cn.malgo.annotation.vo;

import com.alibaba.fastjson.JSONObject;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Value;

@Value
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
}
