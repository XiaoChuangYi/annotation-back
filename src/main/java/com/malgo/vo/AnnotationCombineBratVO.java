package com.malgo.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;

/** Created by cjl on 2018/5/30. */
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

  private JSONObject finalAnnotation;
  private JSONObject reviewedAnnotation;
}
