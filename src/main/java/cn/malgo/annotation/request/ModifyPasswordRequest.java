package cn.malgo.annotation.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyPasswordRequest {
  private long userId;
  private String password;
}
