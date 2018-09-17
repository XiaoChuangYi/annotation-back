package cn.malgo.annotation.request.anno;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchDeleteAnnotationBratTypeRequest {

  private long id;
  private List<String> tags;
  private List<String> rTags;
}
