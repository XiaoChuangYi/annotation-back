package cn.malgo.annotation.request;

import lombok.Data;

@Data
public class AddUserAccountRequest {
  private String accountName;
  private int roleId;
  private String role;
  private String password;
}
