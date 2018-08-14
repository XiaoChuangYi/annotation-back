package cn.malgo.annotation.vo;

import cn.malgo.annotation.entity.OriginalDoc;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OriginalDocListVO {

  int total;

  List<OriginalDoc> originalDocList;
}
