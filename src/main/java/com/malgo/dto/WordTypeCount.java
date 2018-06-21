package com.malgo.dto;

import lombok.Value;

@Value
public class WordTypeCount {
  private String type;
  private int count;
  private int conceptId;
}
