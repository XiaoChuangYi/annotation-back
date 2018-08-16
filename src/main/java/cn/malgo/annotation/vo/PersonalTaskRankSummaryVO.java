package cn.malgo.annotation.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class PersonalTaskRankSummaryVO {

  private long id;

  private long taskId;

  private long assigneeId;

  private int annotatedTotalWordNum;

  @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
  private Double precisionRate;

  @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
  private Double recallRate;

  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createdTime;

  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date lastModified;

  private BigDecimal payment;

  private int totalWordNum;
}
