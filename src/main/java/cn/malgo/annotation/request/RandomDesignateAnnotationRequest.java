package cn.malgo.annotation.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Created by cjl on 2018/5/30. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RandomDesignateAnnotationRequest {
  private List<Integer> userIdList;
  private List<Integer> annotationTypes;
  private int num;
  private int task;
}
