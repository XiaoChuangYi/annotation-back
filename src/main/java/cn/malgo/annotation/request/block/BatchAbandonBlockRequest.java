package cn.malgo.annotation.request.block;

import java.util.List;
import lombok.Data;

@Data
public class BatchAbandonBlockRequest {
  private List<Long> blockIds;
}
