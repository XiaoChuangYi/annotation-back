package cn.malgo.annotation.request.block;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchDeleteBlockBratTypeRequest {

  private long id;
  private List<String> tags;
  private List<String> rTags;
}
