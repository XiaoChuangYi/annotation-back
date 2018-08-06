package cn.malgo.annotation.vo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import java.math.BigDecimal;
import lombok.Data;

import java.util.Date;

@Data
public class AnnotationBratVO {

  private long id;
  private int assignee;
  private String state;
  private int annotationType;
  private Date createdTime;
  private Date lastModified;
  private String userName;
  private Long blockId;
  private String comment;
  private Date expirationTime;
  private BigDecimal estimatePrice;

  private JSONObject finalAnnotation;
  private JSONObject reviewedAnnotation;
}
