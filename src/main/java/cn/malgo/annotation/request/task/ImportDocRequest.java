package cn.malgo.annotation.request.task;

import lombok.Value;

import java.util.List;

@Value
public class ImportDocRequest {
  private final String secretKey;
  private final String source;
  private final List<ImportDoc> data;

  @Value
  public static class ImportDoc {
    private final String name;
    private final String text;
  }
}
