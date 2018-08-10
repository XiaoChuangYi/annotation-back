package cn.malgo.annotation.request.task;

import lombok.Data;
import lombok.Value;

import java.util.List;

@Data
public class ImportDocRequest {
  private final String secretKey;
  private final String source;
  private final List<ImportDoc> data;

  @Override
  public String toString() {
    return "ImportDocRequest(secretKey="
        + this.getSecretKey()
        + ", source="
        + this.getSource()
        + ", dataSize="
        + this.data.size()
        + ")";
  }

  @Data
  public static class ImportDoc {
    private final String name;
    private final String text;
  }
}
