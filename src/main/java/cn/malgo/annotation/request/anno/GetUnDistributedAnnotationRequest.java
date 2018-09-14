package cn.malgo.annotation.request.anno;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUnDistributedAnnotationRequest {

  private String annotationType;
}
