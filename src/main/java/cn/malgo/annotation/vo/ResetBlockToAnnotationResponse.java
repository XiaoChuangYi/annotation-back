package cn.malgo.annotation.vo;

import lombok.Value;

import java.util.List;

@Value
public class ResetBlockToAnnotationResponse {
  private final List<Integer> createdAnnotations;
}
