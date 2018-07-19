package cn.malgo.annotation.request;

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
  private int task; // 0为任务标注，1为练习标注
}
