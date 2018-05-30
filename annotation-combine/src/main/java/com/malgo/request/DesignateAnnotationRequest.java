package com.malgo.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/5/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesignateAnnotationRequest {
  private int userId;
  private List<Integer> idList;
}
