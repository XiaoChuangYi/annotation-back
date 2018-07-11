package cn.malgo.annotation.vo;

import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import lombok.Value;

@Value
public class AnnotationBlockBratVO {
  private int id;
  private JSONObject annotation;
  private int annotationType;
  private Date createdTime;
  private Date lastModified;
  private String state;
  private String text;
}
