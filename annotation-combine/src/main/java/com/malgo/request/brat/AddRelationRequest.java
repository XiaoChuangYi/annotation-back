package com.malgo.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/5/31.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddRelationRequest {
  private int id;
  private String sourceTag;
  private String targetTag;
  private String relation;
}
