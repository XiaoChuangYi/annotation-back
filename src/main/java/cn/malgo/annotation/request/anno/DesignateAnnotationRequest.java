package cn.malgo.annotation.request.anno;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesignateAnnotationRequest {
  private long userId;
  private List<Long> idList;
}
