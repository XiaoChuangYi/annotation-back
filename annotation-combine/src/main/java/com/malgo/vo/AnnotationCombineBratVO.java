package com.malgo.vo;

import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import lombok.Data;

/**
 * Created by cjl on 2018/5/30.
 */
@Data
public class AnnotationCombineBratVO {

  private int id;
  private int assignee;
  private String state;
  private int annotationType;
  private Date gmtCreated;
  private Date gmtModified;

  private JSONObject finalAnnotation;
  private JSONObject reviewedAnnotation;
}
