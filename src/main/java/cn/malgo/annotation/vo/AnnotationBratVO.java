package cn.malgo.annotation.vo;

import cn.malgo.annotation.enums.AnnotationTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.math.BigDecimal;
import lombok.Data;

import java.util.Date;

@Data
public class AnnotationBratVO {

  private long id;
  private long assignee;
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

  @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
  private Double precisionRate;

  @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
  private Double recallRate;

  private long deleteToken;

  private BigDecimal estimatePrice;

  private JSONObject finalAnnotation;
  private JSONObject reviewedAnnotation;
}
