package com.malgo.request.exercise;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/6/3.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListExerciseAnnotationRequest {
  private int pageIndex;
  private int pageSize;
  private List<Integer> annotationTypes;
  private int userId;
}
