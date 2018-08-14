package cn.malgo.annotation.vo;

import cn.malgo.annotation.dto.error.FixAnnotationResult;
import lombok.Data;

import java.util.List;

@Data
public class FixAnnotationResponse {
  private List<FixAnnotationResult> results;
  private int successCount;
  private int errorCount;

  public FixAnnotationResponse(List<FixAnnotationResult> results) {
    this.results = results;
    this.successCount = (int) results.stream().filter(FixAnnotationResult::isSuccess).count();
    this.errorCount = results.size() - this.successCount;
  }
}
