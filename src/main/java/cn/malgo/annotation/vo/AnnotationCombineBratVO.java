package cn.malgo.annotation.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class AnnotationCombineBratVO {
  private int id;
  private int assignee;
  private String state;
  private int annotationType;

  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  private Date gmtCreated;

  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  private Date gmtModified;

  private String userName;

  private Integer blockId;
  private String comment;

  private JSONObject finalAnnotation;
  private JSONObject reviewedAnnotation;
}
