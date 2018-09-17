package cn.malgo.annotation.request.block;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CleanOutBlockRequest {

  private List<String> annotationTypes;
}
