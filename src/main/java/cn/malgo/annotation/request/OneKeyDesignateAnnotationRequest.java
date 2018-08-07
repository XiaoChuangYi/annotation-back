package cn.malgo.annotation.request;

import java.util.List;
import lombok.Value;

@Value
public class OneKeyDesignateAnnotationRequest {
  private List<Long> userIdList;
  private int designateWordNum;
}
