package com.malgo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/5/30.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
  private String accountName;
  private String password;
}
