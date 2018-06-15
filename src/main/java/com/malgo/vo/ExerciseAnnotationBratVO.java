package com.malgo.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/6/3.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseAnnotationBratVO {

  private int id;
  private int annotationType;
  private String term;
  private boolean designate;
  private long num;
  private JSONObject finalJson;
}
