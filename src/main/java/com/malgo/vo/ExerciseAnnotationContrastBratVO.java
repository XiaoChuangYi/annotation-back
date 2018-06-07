package com.malgo.vo;

import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/6/4.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseAnnotationContrastBratVO {
  private int id;
  private Date gmtModified;
  private String term;
  private boolean result;
  private JSONObject userAnnotation;
  private JSONObject standardAnnotation;
}
