package cn.malgo.annotation.request;

import java.util.List;
import lombok.Data;
import lombok.Value;

@Data
public class OneKeyDesignateAnnotationRequest {
  private List<Long> userIdList;
  private int designateWordNum;
}
