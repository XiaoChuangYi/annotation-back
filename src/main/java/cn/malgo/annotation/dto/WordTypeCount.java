package cn.malgo.annotation.dto;

import lombok.Value;

@Value
public class WordTypeCount {
  private String type;
  private int count;
  private int conceptId;
}
