package cn.malgo.annotation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListAnnotationCombineRequest {
  private List<Long> idList;
  private String term;
  private int pageIndex;
  private int pageSize;
  private List<String> states;
  private long userId;
  private List<Integer> annotationTypes;
}
