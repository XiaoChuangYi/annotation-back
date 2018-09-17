package cn.malgo.annotation.request.anno;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListAnnotationRequest {
  private List<Long> idList;
  private List<Long> blockIds;
  private String term;
  private int pageIndex;
  private int pageSize;
  private List<String> states;
  private long userId;
  private List<Integer> annotationTypes;
  private Date leftDate;
  private Date rightDate;
  private boolean includeReviewedAnnotation;
}
