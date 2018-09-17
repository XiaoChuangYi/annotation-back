package cn.malgo.annotation.request;

import java.util.List;
import lombok.Data;

@Data
public class OneKeyDesignateAnnotationRequest {

  private List<String> annotationTypes;
  private List<Long> userIdList;
  private int designateWordNum;
}
