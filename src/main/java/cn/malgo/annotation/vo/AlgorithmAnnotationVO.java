package cn.malgo.annotation.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlgorithmAnnotationVO {
  private String autoAnnotation;
  private AnnotationBratVO annotationCombineBratVO;
}
