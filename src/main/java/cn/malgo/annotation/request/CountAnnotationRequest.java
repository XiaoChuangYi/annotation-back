package cn.malgo.annotation.request;

import cn.malgo.annotation.enums.AnnotationTypeEnum;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Created by cjl on 2018/5/31. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountAnnotationRequest {
  private List<AnnotationTypeEnum> annotationTypes;
}
