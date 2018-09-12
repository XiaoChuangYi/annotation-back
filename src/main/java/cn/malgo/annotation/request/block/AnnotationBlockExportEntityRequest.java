package cn.malgo.annotation.request.block;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationBlockExportEntityRequest {

  private Integer annotationType;
}
