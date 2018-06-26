package cn.malgo.annotation.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Created by cjl on 2018/5/29. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListAnnotationCombineRequest {

  private List<Integer> idList;
  private String term;
  private int pageIndex;
  private int pageSize;
  private List<String> states;
  private int userId;
  private List<Integer> annotationTypes;
}
