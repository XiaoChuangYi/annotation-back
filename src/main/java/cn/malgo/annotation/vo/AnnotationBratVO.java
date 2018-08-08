package cn.malgo.annotation.vo;

import cn.malgo.annotation.enums.AnnotationTypeEnum;
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
  private AnnotationTypeEnum annotationType;

  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createdTime;

  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date lastModified;

  private String userName;
  private Long blockId;
  private String comment;

  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date expirationTime;

  private double precisionRate;
  private double recallRate;
  private long deleteToken;

  private BigDecimal estimatePrice;

  private JSONObject finalAnnotation;
  private JSONObject reviewedAnnotation;
}
