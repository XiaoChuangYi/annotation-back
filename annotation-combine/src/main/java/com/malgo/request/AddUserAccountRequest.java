package com.malgo.request;

import lombok.Data;

/**
 * Created by cjl on 2018/5/30.
 */
@Data
public class AddUserAccountRequest {
  private String accountName;
  private String role;
  private String password;
}
